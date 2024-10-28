package com.example.laboratorio11plats.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = database.child("posts").child(userId).get().await()
                val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                withContext(Dispatchers.Main) {
                    _postsLiveData.value = posts
                }
            } catch (e: Exception) {
                Log.d("BlogViewModel", "Failed to retrieve posts: ${e.message}")
            }
        }
    }

    // Crear publicación
    fun createPost(postText: String, imageUri: Uri?, onPostSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = currentUser?.uid ?: return@launch
                val postId = database.child("posts").child(userId).push().key ?: return@launch

                var imageUrl = ""
                if (imageUri != null) {
                    val storageRef = storage.child("posts/$userId/$postId")
                    storageRef.putFile(imageUri).await()
                    imageUrl = storageRef.downloadUrl.await().toString()
                }

                val post = Post(postText, imageUrl, System.currentTimeMillis())
                database.child("posts").child(userId).child(postId).setValue(post).await()

                // Cambiar al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    onPostSuccess()
                }
            } catch (e: Exception) {
                Log.e("BlogViewModel", "Error creating post: ${e.message}")
            }
        }
    }
}
