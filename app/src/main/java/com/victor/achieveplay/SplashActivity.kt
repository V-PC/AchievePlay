package com.victor.achieveplay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Comprueba si el usuario ya está logeado
        if (FirebaseAuth.getInstance().currentUser != null) {
            // Usuario ya logeado, verifica si necesita completar su perfil
            checkUserProfile()
        } else {
            // No logeado, envía al usuario a la pantalla de Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkUserProfile() {
        // Supongamos que tienes un método que verifica si el perfil del usuario está completo
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
                        completion(!username.isNullOrEmpty()) // Llama al callback con true si el username existe y no está vacío
                    } else {
                        completion(false) // Llama al callback con false si el documento no existe
                    }
                } else {
                    completion(false) // Llama al callback con false si la consulta falló
                }
            }
        } else {
            completion(false) // Llama al callback con false si el usuario no está logueado
        }
    }

}
