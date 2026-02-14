package com.example.spacetraveler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spacetraveler.ui.missions.CreateMissionScreen
import com.example.spacetraveler.ui.missions.InfoMissionScreen
import com.example.spacetraveler.ui.missions.MissionListScreen
import com.example.spacetraveler.ui.missions.MissionViewModel
import com.example.spacetraveler.ui.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceTravelerApp()
        }
    }
}

@Composable
fun SpaceTravelerApp() {
    val navController = rememberNavController()
    val viewModel: MissionViewModel = hiltViewModel()

    MaterialTheme {
        Surface(modifier = Modifier) {
            NavHost(navController = navController, startDestination = "mission_list") {
                composable(Routes.MISSION_LIST) {
                    MissionListScreen(
                        onCreateMissionClick = {
                            navController.navigate(Routes.CREATE_MISSION)
                        },
                        onMissionClick = { missionId ->
                            navController.navigate(
                                Routes.infoMission(missionId))},
                        onDeleteMissionClick = { missionId ->
                            viewModel.deleteMission(missionId)}
                    )
                }

                composable(Routes.CREATE_MISSION) {
                    CreateMissionScreen(
                        navController = navController,
                        onMissionCreated = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.INFO_MISSION_WITH_MISSION_ID) { backStackEntry ->

                    val missionId =
                        backStackEntry.arguments?.getString("missionId")?.toIntOrNull()

                    missionId?.let {
                        InfoMissionScreen(
                            missionId = it,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}