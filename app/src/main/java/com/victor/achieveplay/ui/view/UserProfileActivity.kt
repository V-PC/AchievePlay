package com.victor.achieveplay.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.User

class UserProfileActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    private lateinit var profileImageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var usernameEditText: EditText
    private lateinit var emailTextView: TextView
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        profileImageView = findViewById(R.id.profile_image)
        usernameEditText = findViewById(R.id.username_input)
        emailTextView = findViewById(R.id.email_text_view)

        profileImageView.setOnClickListener {
            openImageSelector()
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            uploadImageToStorage(imageUri)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val logoutButton = findViewById<FloatingActionButton>(R.id.logout_button)
        logoutButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener(this) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        loadUserProfile()
        applyAnimations()
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            imageUri = data?.data
            profileImageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToStorage(imageUri: Uri?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().getReference("profileImages/$userId")
        val uploadTask = imageUri?.let { storageRef.putFile(it) }
        if (uploadTask != null) {
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val photoUrl = uri.toString()
                    updateProfileData(photoUrl)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfileData(photoUrl: String) {
        val userName = usernameEditText.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userMap = hashMapOf(
            "userName" to userName,
            "photoUrl" to photoUrl
        )

        db.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado correctamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return
        emailTextView.text = user.email

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        usernameEditText.setText(it.userName)
                        Glide.with(this)
                            .load(it.photoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(profileImageView)
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        profileImageView.startAnimation(fadeIn)
        usernameEditText.startAnimation(fadeIn)
        findViewById<Button>(R.id.save_button).startAnimation(fadeIn)
        emailTextView.startAnimation(fadeIn)
    }
}
