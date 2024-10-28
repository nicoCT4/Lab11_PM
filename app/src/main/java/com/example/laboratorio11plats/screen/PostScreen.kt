package com.example.laboratorio11plats.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.laboratorio11plats.viewmodel.BlogViewModel

@Composable
fun PostScreen(
    blogViewModel: BlogViewModel,
    onPostSuccess: () -> Unit,
    onNavigateToPostsList: () -> Unit
) {
    var postText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }  // Estado de carga
    var showSnackbar by remember { mutableStateOf(false) }

    // Para elegir una imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Mostrar el indicador de progreso si está cargando
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Título
            Text(text = "Create a New Post", fontSize = 24.sp)

            // Campo de texto para la publicación
            OutlinedTextField(
                value = postText,
                onValueChange = { postText = it },
                label = { Text("Post Text") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )

            // Botón para seleccionar una imagen
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Select Image (Optional)")
            }

            // Mostrar la imagen seleccionada
            imageUri?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para crear la publicación
            Button(
                onClick = {
                    Log.d("PostScreen", "Create Post button clicked")
                    isLoading = false  // Comienza la carga
                    blogViewModel.createPost(postText, imageUri) {
                        Log.d("PostScreen", "Post successfully created")
                        isLoading = false  // Finaliza la carga cuando el post se crea
                        showSnackbar = true // Mostrar snackbar cuando se cree exitosamente
                        onPostSuccess()  // Navegar después de la creación del post
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Create Post")
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ver las publicaciones del usuario
            Button(
                onClick = { onNavigateToPostsList() },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(text = "View My Posts")
            }
        }

        // Mostrar Snackbar si el post fue creado
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.padding(8.dp),
                action = {
                    Button(onClick = { showSnackbar = false }) {
                        Text("OK")
                    }
                }
            ) {
                Text(text = "Post creado")
            }
        }
    }
}
