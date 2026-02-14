package com.example.spacetraveler.ui.missions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spacetraveler.utils.parseDateFlexible
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InfoMissionScreen(
    missionId: Int,
    viewModel: MissionViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val missions = viewModel.missions.collectAsState().value
    val mission = missions.find { it.id == missionId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Detalle de la Misión",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        mission?.let {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Gray)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = it.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(text = "Planeta destino: ${it.planetaDestino}")

                val parsedDate = parseDateFlexible(it.fechaLanzamiento)
                val formattedDate = parsedDate?.let { date ->
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
                } ?: "Fecha inválida"

                Text(text = "Fecha de lanzamiento: $formattedDate")

                Text(text = "Descripción:")
                Text(text = it.descripcion)
            }

        } ?: run {
            Text(
                text = "Misión no encontrada",
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
