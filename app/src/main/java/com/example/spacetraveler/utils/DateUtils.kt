package com.example.spacetraveler.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun parseDateFlexible(dateString: String): Date? {
    val formats = listOf(
        "dd-MM-yyyy",
        "yyyy-MM-dd",
        "ddMMyyyy",
        "dd/MM/yyyy"
    )

    for (pattern in formats) {
        try {
            return SimpleDateFormat(pattern, Locale.getDefault()).parse(dateString)
        } catch (_: Exception) {
        }
    }

    return null
}

fun isValidDate(date: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        sdf.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}
