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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.Game
import com.victor.achieveplay.data.model.User
import com.victor.achieveplay.ui.adapter.GamesInListAdapter
import com.victor.achieveplay.ui.adapter.UserListsAdapter

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
    private lateinit var userListsRecyclerView: RecyclerView
    private lateinit var gamesInListRecyclerView: RecyclerView
    private lateinit var userListsAdapter: UserListsAdapter
    private lateinit var gamesInListAdapter: GamesInListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        profileImageView = findViewById(R.id.profile_image)
        usernameEditText = findViewById(R.id.username_input)
        emailTextView = findViewById(R.id.email_text_view)
        userListsRecyclerView = findViewById(R.id.user_lists_recyclerview)
        gamesInListRecyclerView = findViewById(R.id.games_in_list_recyclerview)

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

        val logoutButton = findViewById<ImageView>(R.id.logout_button)
        logoutButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener(this) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        userListsRecyclerView.layoutManager = LinearLayoutManager(this)
        gamesInListRecyclerView.layoutManager = LinearLayoutManager(this)

        loadUserProfile()
        applyAnimations()

        val helpButton = findViewById<ImageView>(R.id.helpButton)
        helpButton.setOnClickListener {
            val intent = Intent(this, ManualActivity::class.java)
            startActivity(intent)
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

        loadUserLists(userId)
    }

    private fun loadUserLists(userId: String) {
        db.collection("lists")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val lists = documents.map { it.getString("listName")!! }
                userListsAdapter = UserListsAdapter(lists) { listName ->
                    loadGamesInList(userId, listName)
                }
                userListsRecyclerView.adapter = userListsAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar las listas", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Error loading lists: ", exception)
            }
    }

    private fun loadGamesInList(userId: String, listName: String) {
        db.collection("lists")
            .whereEqualTo("userId", userId)
            .whereEqualTo("listName", listName)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val listId = documents.documents[0].getString("listId")!!
                    db.collection("gamesInList")
                        .whereEqualTo("listId", listId)
                        .get()
                        .addOnSuccessListener { gameDocuments ->
                            val games = gameDocuments.map {
                                Game(
                                    id = it.getString("gameId")!!,
                                    name = it.getString("name")!!,
                                    image = it.getString("image")!!
                                )
                            }
                            gamesInListAdapter = GamesInListAdapter(games) { game ->
                                deleteGameFromList(game, listId)
                            }
                            gamesInListRecyclerView.adapter = gamesInListAdapter
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error al cargar los juegos de la lista", Toast.LENGTH_SHORT).show()
                            Log.d("Firestore", "Error loading games in list: ", exception)
                        }
                } else {
                    Log.d("Firestore", "List not found")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar la lista", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Error loading list: ", exception)
            }
    }

    private fun deleteGameFromList(game: Game, listId: String) {
        db.collection("gamesInList")
            .whereEqualTo("listId", listId)
            .whereEqualTo("gameId", game.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("gamesInList").document(document.id).delete()
                }
                Toast.makeText(this, "Juego eliminado de la lista", Toast.LENGTH_SHORT).show()
                loadGamesInList(FirebaseAuth.getInstance().currentUser!!.uid, listId)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar el juego de la lista", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Error deleting game from list: ", exception)
            }
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

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        profileImageView.startAnimation(fadeIn)
        usernameEditText.startAnimation(fadeIn)
        findViewById<Button>(R.id.save_button).startAnimation(fadeIn)
        emailTextView.startAnimation(fadeIn)
    }
}
