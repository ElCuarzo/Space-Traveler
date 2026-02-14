package com.example.spacetraveler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)
        val masked = buildString {
            digits.forEachIndexed { index, c ->
                append(c)
                if (index == 1 || index == 3) append('-')
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 1 -> offset
                offset <= 3 -> offset + 1
                offset <= 8 -> offset + 2
                else -> masked.length
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 5 -> offset - 1
                offset <= 10 -> offset - 2
                else -> 8
            }
        }
        return TransformedText(AnnotatedString(masked), offsetMapping)
    }
}

@Composable
fun FormDateField(
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
                .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
                .border(width = borderWidth.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(8)
                    onValueChange(digits)
                },
                visualTransformation = DateVisualTransformation(),
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
