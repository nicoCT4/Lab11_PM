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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class BlogViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // LiveData para almacenar las publicaciones
    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>> = _postsLiveData

    // Obtener publicaciones del usuario actual
    fun getPosts() {
        val userId = auth.currentUser?.uid ?: return

        database.child("posts").child(userId).get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                _postsLiveData.value = posts
            }
            .addOnFailureListener { error ->
                Log.d("BlogViewModel", "Failed to retrieve posts: \${error.message}")
            }
    }

    // Crear publicación (implementación de ejemplo usando coroutines)
    fun createPost(postText: String, imageUri: Uri?, onPostSuccess: () -> Unit, onPostFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Si el usuario no está autenticado, no se puede crear el post
            onPostFailure(Exception("User is not authenticated. Please log in."))
            return
        }

        val userId = currentUser.uid
        val postId = database.child("posts").child(userId).push().key ?: return
        val post = Post(postText = postText, imageUrl = "")

        // Publicación del texto del post primero
        database.child("posts").child(userId).child(postId).setValue(post)
            .addOnSuccessListener {
                if (imageUri != null) {
                    // Solo sube la imagen si existe `imageUri`
                    val imageRef = storage.child("images/\$postId")
                    imageRef.putFile(imageUri)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                                database.child("posts").child(userId).child(postId).child("imageUrl").setValue(uri.toString())
                                    .addOnSuccessListener { onPostSuccess() }
                                    .addOnFailureListener { onPostFailure(it) }
                            }
                        }
                        .addOnFailureListener { onPostFailure(it) }
                } else {
                    // Si no hay imagen, simplemente concluye el post exitoso
                    onPostSuccess()
                }
            }
            .addOnFailureListener { onPostFailure(it) }
    }
}

