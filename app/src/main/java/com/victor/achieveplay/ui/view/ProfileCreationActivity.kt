package com.victor.achieveplay.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.victor.achieveplay.R
import java.util.UUID

class ProfileCreationActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var profileImageView: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_creation)

        profileImageView = findViewById(R.id.profileImageView)
        findViewById<Button>(R.id.uploadPhotoButton).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            if (imageUri != null) {
                uploadImageToStorage(imageUri!!)
            } else {
                Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(profileImageView)
        }
    }

    private fun uploadImageToStorage(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().getReference("profileImages/${UUID.randomUUID()}")
        val uploadTask = storageReference.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val photoUrl = uri.toString()
                saveUserProfileToFirestore(photoUrl)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error uploading image: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserProfileToFirestore(photoUrl: String) {
        val userName = findViewById<EditText>(R.id.usernameEditText).text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId != null) {
            val userMap = hashMapOf(
                "userName" to userName,
                "photoUrl" to photoUrl
            )

            FirebaseFirestore.getInstance().collection("users").document(userId).set(userMap)
                .addOnSuccessListener {
                    val intent = Intent(this, DiscoveryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving profile: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User ID is null.", Toast.LENGTH_SHORT).show()
        }
    }
}
