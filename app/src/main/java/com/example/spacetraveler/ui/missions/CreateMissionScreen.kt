package com.example.spacetraveler.ui.missions

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.spacetraveler.ui.components.FormDateField
import com.example.spacetraveler.ui.components.FormTextField

@Composable
fun CreateMissionScreen(
    navController: NavController,
    viewModel: MissionViewModel = hiltViewModel(),
    onMissionCreated: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var planetaDestino by remember { mutableStateOf("") }
    var fechaLanzamiento by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val errorNombre by remember {
        derivedStateOf {
            when {
                nombre.isBlank() -> "El nombre es obligatorio"
                !nombre.matches(Regex("^[a-zA-Z0-9 ]+$")) -> "Solo letras, números y espacios permitidos"
                else -> ""
            }
        }
    }

    val errorPlaneta by remember {
        derivedStateOf {
            if (planetaDestino.isBlank()) "El planeta es obligatorio" else ""
        }
    }

    val errorFecha by remember {
        derivedStateOf {
            if (fechaLanzamiento.length != 8) "Fecha incompleta"
            else {
                try {
                    val day = fechaLanzamiento.substring(0, 2).toInt()
                    val month = fechaLanzamiento.substring(2, 4).toInt()

                    when {
                        day !in 1..31 -> "Día inválido"
                        month !in 1..12 -> "Mes inválido"
                        else -> ""
                    }
                } catch (_: Exception) {
                    "Fecha inválida"
                }
            }
        }
    }

    val errorDescripcion by remember {
        derivedStateOf {
            when {
                descripcion.isBlank() -> "La descripción es obligatoria"
                descripcion.length < 5 -> "Debe tener al menos 5 caracteres"
                else -> ""
            }
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            nombre.isNotBlank() &&
                    planetaDestino.isNotBlank() &&
                    fechaLanzamiento.length == 8 &&
                    errorNombre.isEmpty() &&
                    errorPlaneta.isEmpty() &&
                    errorFecha.isEmpty() &&
                    errorDescripcion.isEmpty()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Formulario de creación de nueva mision",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FormTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre",
            error = errorNombre,
            modifier = Modifier.fillMaxWidth()
        )

        FormTextField(
            value = planetaDestino,
            onValueChange = { planetaDestino = it },
            label = "Planeta Destino",
            error = errorPlaneta,
            modifier = Modifier.fillMaxWidth()
        )

        FormDateField(
            value = fechaLanzamiento,
            onValueChange = { fechaLanzamiento = it },
            label = "Fecha Lanzamiento (dd-MM-yyyy)",
            error = errorFecha,
            modifier = Modifier.fillMaxWidth()
        )

        FormTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = "Descripción",
            error = errorDescripcion,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    Log.d("DEBUG_BOTON", "Botón presionado")
                    viewModel.createMission(
                        nombre,
                        planetaDestino,
                        fechaLanzamiento,
                        descripcion
                    ) { success ->

                        if (success) {
                            onMissionCreated()
                        }
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF4DA6FF) else Color.Gray
                )
            ) {
                Text("Crear Misión")
            }
        }
    }
}
