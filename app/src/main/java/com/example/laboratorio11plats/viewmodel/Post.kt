package com.example.laboratorio11plats.viewmodel

// Clase Post para representar cada publicación
data class Post(
    val postText: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L
)
