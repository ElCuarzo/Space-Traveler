package com.example.spacetraveler.ui.navigation

object Routes {
    const val MISSION_LIST = "mission_list"
    const val CREATE_MISSION = "create_mission"
    const val INFO_MISSION_WITH_MISSION_ID = "info_mission/{missionId}"
    fun infoMission(missionId: Int) = "info_mission/$missionId"
}