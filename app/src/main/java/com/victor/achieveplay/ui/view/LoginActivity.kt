package com.victor.achieveplay.ui.view

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.victor.achieveplay.R
import com.victor.achieveplay.ui.view.custom.ParallaxView

class LoginActivity : AppCompatActivity() , SensorEventListener {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var sensorManager: SensorManager
    private lateinit var backgroundImage: ParallaxView
    private val images = arrayOf(
        R.drawable.thelastofus,
        R.drawable.doom,
        R.drawable.godofwar,
        R.drawable.uncharted,
        R.drawable.hades
    )
    private var imageIndex = 0
    private val changeImageInterval: Long = 6000
    private val handler = Handler()
    private val changeImageRunnable = object : Runnable {
        override fun run() {
            val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
            val fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    imageIndex = (imageIndex + 1) % images.size
                    backgroundImage.setImageResource(images[imageIndex])
                    backgroundImage.startAnimation(fadeIn)
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
            backgroundImage.startAnimation(fadeOut)
            handler.postDelayed(this, changeImageInterval)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val animScale = AnimationUtils.loadAnimation(this, R.anim.button_press)
        backgroundImage = findViewById(R.id.backgroundImageView)
        backgroundImage.setImageResource(images[imageIndex])
        handler.postDelayed(changeImageRunnable, changeImageInterval)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)


        // Configurar opciones de Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()

        val signInButton = findViewById<ImageView>(R.id.sign_in_button)
        signInButton.setBackgroundResource(R.drawable.botonloginrecortado)
        signInButton.setOnClickListener {
            it.startAnimation(animScale)
            signIn()
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        val x = event.values[0] * 10
        val y = event.values[1] * 10
        backgroundImage.setOffset(x, y)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(changeImageRunnable)
    }
    private fun signIn() {
        Log.d(TAG, "Iniciando el proceso de inicio de sesión con Google")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult llamado con requestCode: $requestCode, resultCode: $resultCode")

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In fue exitoso, autenticar con Firebase
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Log.d(TAG, "Google Sign-In exitoso, autenticando con Firebase")
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    Log.e(TAG, "Google Sign-In no retornó ninguna cuenta")
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "Autenticando con Firebase usando el token de Google")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.i(TAG, "Inicio de sesión exitoso con Firebase: ${user?.displayName}")

                    // Redirigir a DiscoveryActivity
                    val intent = Intent(this, ProfileCreationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(TAG, "Error en la autenticación con Firebase", task.exception)
                }
            }
    }
    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }
}

