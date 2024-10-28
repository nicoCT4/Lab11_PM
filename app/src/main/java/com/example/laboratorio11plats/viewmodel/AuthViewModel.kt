package com.example.laboratorio11plats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val email: String = ""
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    // LiveData para almacenar el perfil del usuario
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: MutableLiveData<UserProfile?> = _userProfile

    val authResult = MutableLiveData<Result<Unit>>()

    // Método para registrar al usuario y guardar sus datos en Firebase
    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authResult.value = Result.success(Unit)
                } else {
                    authResult.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    // Método para iniciar sesión con email y contraseña
    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authResult.value = Result.success(Unit)
                } else {
                    authResult.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    // Método para guardar la información del perfil del usuario en la base de datos
    fun saveUserProfile(firstName: String, lastName: String, dateOfBirth: String) {
        val userId = auth.currentUser?.uid ?: return
        val userProfile = UserProfile(
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            email = auth.currentUser?.email ?: ""
        )

        database.child("users").child(userId).setValue(userProfile)
            .addOnSuccessListener {
                // Perfil guardado exitosamente
                _userProfile.value = userProfile
            }
            .addOnFailureListener { error ->

            }
    }

    // Método para obtener la información del perfil del usuario desde la base de datos
    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                _userProfile.value = profile
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    // Método para actualizar la información del perfil del usuario
    fun updateUserProfile(firstName: String, lastName: String, dateOfBirth: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "dateOfBirth" to dateOfBirth
        )

        database.child("users").child(userId).updateChildren(updates)
            .addOnSuccessListener {
                // Actualización exitosa
                _userProfile.value = UserProfile(
                    firstName = firstName,
                    lastName = lastName,
                    dateOfBirth = dateOfBirth,
                    email = auth.currentUser?.email ?: ""
                )
            }
            .addOnFailureListener {

            }
    }
}
