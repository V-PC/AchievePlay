package com.victor.achieveplay.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.User
import com.victor.achieveplay.ui.adapter.UserAdapter

class SearchUsersFragment : Fragment() {

    private lateinit var searchView: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileImageView: ImageView

    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_users, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        searchView = view.findViewById(R.id.search_view)
        profileImageView = view.findViewById(R.id.btnChangeActivity)

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_users)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val fetchUsersButton = view.findViewById<Button>(R.id.fetch_users_button)
        fetchUsersButton.setOnClickListener {
            val searchQuery = searchView.text.toString()
            searchUsers(searchQuery)
        }

        loadUserProfileImage()

        profileImageView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun searchUsers(searchQuery: String) {
        db.collection("users")
            .orderBy("userName")
            .startAt(searchQuery)
            .endAt(searchQuery + '\uf8ff')
            .get()
            .addOnSuccessListener { documents ->
                val users = documents.map { it.toObject(User::class.java) }

                recyclerView.adapter = UserAdapter(users)

                val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                recyclerView.startAnimation(fadeIn)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }

    private fun loadUserProfileImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("Glide", "User Id: $userId")
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            // Cargar la imagen de perfil desde Firebase Storage
                            val profileImageUrl = it.photoUrl
                            Log.d("Glide", "Profile Image URL: $profileImageUrl")
                            Glide.with(this)
                                .load(profileImageUrl)
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
    }
}
