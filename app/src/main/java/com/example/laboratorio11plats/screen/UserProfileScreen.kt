package com.example.laboratorio11plats.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laboratorio11plats.viewmodel.AuthViewModel

@Composable
fun UserProfileScreen(
    authViewModel: AuthViewModel = viewModel()
) {
    // Observar los datos del perfil de usuario desde el ViewModel
    val userProfile = authViewModel.userProfile.observeAsState()

    // Al inicializar, cargar los datos del perfil de usuario
    LaunchedEffect(Unit) {
        authViewModel.fetchUserProfile()
    }

    // Mostrar los datos del perfil de usuario
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        userProfile.value?.let { profile ->
            Text(
                text = "Nombre: ${profile.firstName}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Apellido: ${profile.lastName}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Fecha de Nacimiento: ${profile.dateOfBirth}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Correo Electrónico: ${profile.email}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        } ?: run {
            // Mostrar un mensaje mientras se cargan los datos del perfil
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            Text(
                text = "Cargando perfil...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
