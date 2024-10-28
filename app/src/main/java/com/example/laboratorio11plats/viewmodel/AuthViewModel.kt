package com.example.laboratorio11plats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authResult = MutableLiveData<Result<FirebaseUser?>>()
    val authResult: LiveData<Result<FirebaseUser?>> get() = _authResult

    // Obtener el usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Verificar si el usuario está autenticado
    fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun signUp(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuario registrado con éxito, ahora puedes obtener el usuario actual
                    val user = FirebaseAuth.getInstance().currentUser
                    // Aquí podrías navegar a la siguiente pantalla o actualizar el LiveData correspondiente
                    _authResult.value = Result.success(user)
                } else {
                    // Manejar error de registro
                    _authResult.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
    }


    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _authResult.value = Result.success(user)
                } else {
                    _authResult.value = Result.failure(task.exception ?: Exception("Error desconocido"))
                }
            }
    }

    fun signUpAndSignIn(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuario registrado con éxito
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { signInTask ->
                            if (signInTask.isSuccessful) {
                                _authResult.value = Result.success(FirebaseAuth.getInstance().currentUser)
                            } else {
                                _authResult.value = Result.failure(signInTask.exception ?: Exception("Unknown error"))
                            }
                        }
                } else {
                    _authResult.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

}


