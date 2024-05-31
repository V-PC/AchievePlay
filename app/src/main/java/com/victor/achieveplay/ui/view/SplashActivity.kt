package com.victor.achieveplay.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            checkUserProfile()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkUserProfile() {
        val profileComplete = checkIfUserProfileComplete { isProfileComplete ->
            if (isProfileComplete) {
                // Navegar a la pantalla principal
                startActivity(Intent(this, DiscoveryActivity::class.java))
                finish()
            } else {
                // Navegar a la pantalla de login o creación de perfil
                startActivity(Intent(this, ProfileCreationActivity::class.java))
                finish()
            }
        }

    }

    fun checkIfUserProfileComplete(completion: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val username = document.getString("userName")
                        completion(!username.isNullOrEmpty())
                    } else {
                        completion(false)
                    }
                } else {
                    completion(false)
                }
            }
        } else {
            completion(false)
        }
    }

}
