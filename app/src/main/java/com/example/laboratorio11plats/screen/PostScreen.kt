package com.example.laboratorio11plats.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laboratorio11plats.viewmodel.BlogViewModel

@Composable
fun PostScreen(
    blogViewModel: BlogViewModel = viewModel(),
    onPostSuccess: () -> Unit,
    onNavigateToPostsList: () -> Unit,
    onNavigateToUserProfile: () -> Unit // Parámetro adicional para la navegación al perfil del usuario
) {
    var postText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Crear un launcher para abrir la galería y seleccionar una imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri // Actualizar la variable con la URI seleccionada
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = postText,
            onValueChange = { postText = it },
            label = { Text("Write your post here...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Botón para seleccionar una imagen de la galería
        Button(
            onClick = {
                launcher.launch("image/*") // Abrir la galería para seleccionar una imagen
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Select Image")
        }

        // Mostrar información sobre la imagen seleccionada si existe
        imageUri?.let {
            Text(text = "Image selected: $it")
        }

        Button(
            onClick = {
                blogViewModel.createPost(
                    postText = postText,
                    imageUri = imageUri,
                    onPostSuccess = {
                        // Navegar a la lista de publicaciones cuando la publicación se haya creado con éxito
                        onPostSuccess()
                    },
                    onPostFailure = { errorMessage ->
                        // Aquí podrías mostrar un mensaje de error al usuario, si la publicación falla
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Create Post")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToPostsList,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("View Posts")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para navegar a la pantalla del perfil del usuario
        Button(
            onClick = onNavigateToUserProfile,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("View Profile")
        }
    }
}
