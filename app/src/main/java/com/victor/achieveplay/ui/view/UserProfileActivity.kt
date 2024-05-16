package com.victor.achieveplay.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.victor.achieveplay.R

class UserProfileActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
    private lateinit var profileImageView: ImageView
    private lateinit var imageUri: Uri
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        profileImageView = findViewById(R.id.profile_image)

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
                // Una vez que la sesión de Google esté cerrada, cierra sesión en Firebase
                FirebaseAuth.getInstance().signOut()
                // Redirige al usuario a la pantalla de inicio de sesión
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()  // Finaliza la actividad actual
            }

        }
    }

    fun openImageSelector() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val Uri = data?.data
            if (Uri != null) {
                imageUri = Uri
            }
            profileImageView.setImageURI(imageUri)
        }
    }
    fun uploadImageToStorage(imageUri: Uri?) {
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

    fun updateProfileData(photoUrl: String) {
        val userName = findViewById<EditText>(R.id.usernameEditText).text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val userMap = hashMapOf(
            "userName" to userName,
            "photoUrl" to photoUrl
        )

        FirebaseFirestore.getInstance().collection("users").document(userId!!).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado correctamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
    }
}