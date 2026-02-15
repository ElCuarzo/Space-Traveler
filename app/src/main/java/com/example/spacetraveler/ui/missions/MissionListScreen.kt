package com.example.spacetraveler.ui.missions

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.spacetraveler.utils.parseDateFlexible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MissionListScreen(
    viewModel: MissionViewModel,
    onCreateMissionClick: () -> Unit,
    onMissionClick: (Int) -> Unit,
    onDeleteMissionClick: (Int) -> Unit
) {
    val missions = viewModel.missions.collectAsState().value

    val sortedMissions = missions.sortedByDescending { mission ->
        parseDateFlexible(mission.fechaLanzamiento) ?: Date(0)
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
            text = "Lista de Misiones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sortedMissions) { mission ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(2.dp, Color.Gray)
                        .clickable { onMissionClick(mission.id) }
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mission.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        IconButton(
                            onClick = { onDeleteMissionClick(mission.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Misión",
                                tint = Color.Red
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = "Planeta: ${mission.planetaDestino}")

                    val parsedDate = parseDateFlexible(mission.fechaLanzamiento)
                    val formattedDate = parsedDate?.let {
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(it)
                    } ?: "Fecha inválida"
                    Text(text = "Fecha: $formattedDate")
                }
            }
        }

        Button(
            onClick = onCreateMissionClick,
            modifier = Modifier .fillMaxWidth() .padding(4.dp)
        )
        { Text("Crear Misión") }
    }
}
