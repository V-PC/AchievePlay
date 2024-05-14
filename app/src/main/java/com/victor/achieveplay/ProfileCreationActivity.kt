package com.victor.achieveplay

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileCreationActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var profileImageView: ImageView
    private lateinit var imageUri: Uri
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
            uploadImageToStorage(imageUri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val Uri = data.data
            if (Uri != null) {
                imageUri = Uri
            }
            profileImageView.setImageURI(imageUri)
        }
    }

    fun uploadImageToStorage(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().getReference("profileImages/${UUID.randomUUID()}")
        val uploadTask = storageReference.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val photoUrl = uri.toString()
                saveUserProfileToFirestore(photoUrl)
            }
        }.addOnFailureListener {
            // Manejar errores de subida
        }
    }
    fun saveUserProfileToFirestore(photoUrl: String) {
        val userName = findViewById<EditText>(R.id.usernameEditText).text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val userMap = hashMapOf(
            "userName" to userName,
            "photoUrl" to photoUrl
        )

        FirebaseFirestore.getInstance().collection("users").document(userId!!).set(userMap)
            .addOnSuccessListener {
                val intent = Intent(this, DiscoveryActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                // Mostrar mensaje de error
            }
    }

}
