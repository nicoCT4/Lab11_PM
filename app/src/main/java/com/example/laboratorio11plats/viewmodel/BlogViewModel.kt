package com.example.laboratorio11plats.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BlogViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // LiveData para almacenar las publicaciones
    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>> = _postsLiveData

    // Obtener publicaciones del usuario actual
    fun getPosts() {
        val userId = currentUser?.uid ?: return

        database.child("posts").child(userId).get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                _postsLiveData.value = posts
            }
            .addOnFailureListener { error ->
                Log.d("BlogViewModel", "Failed to retrieve posts: ${error.message}")
            }
    }

    // Crear publicación (implementación de ejemplo usando coroutines)
    fun createPost(postText: String, imageUri: Uri?, onPostSuccess: () -> Unit) {
        // Implementación como discutido anteriormente usando coroutines...
    }
}
