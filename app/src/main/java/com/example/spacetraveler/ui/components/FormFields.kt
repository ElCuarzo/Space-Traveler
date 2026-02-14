package com.example.spacetraveler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String = "",
    backgroundColor: Color = Color(0xFFF0F0F0),
    borderColor: Color = Color.LightGray,
    borderWidth: Float = 1f
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .border(width = borderWidth.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) Text(label, color = Color.Gray)
                    innerTextField()
                }
            )
        }

        Box(modifier = Modifier.height(20.dp)) {
            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }
        }
    }
}
