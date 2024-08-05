package com.example.projectcolor.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ColorPickerButtons(
    modifier: Modifier = Modifier,
    onColorSelected: (Color) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth().padding(top = 4.dp)) {

        Spacer(modifier = modifier.weight(1f))

        //RED
        Button(
            modifier = modifier,
            onClick = { onColorSelected(Color.Red) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        ) {}

        Spacer(modifier = modifier.weight(1/2f))

        //GREEN
        Button(
            modifier = modifier,
            onClick = { onColorSelected(Color.Green) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
        ) {}

        Spacer(modifier = modifier.weight(1/2f))

        //BLUE
        Button(
            modifier = modifier,
            onClick = { onColorSelected(Color.Blue) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
        ) {}

        Spacer(modifier = modifier.weight(1/2f))

        Button(
            modifier = modifier,
            onClick = { onColorSelected(Color.LightGray) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
        ) {}
        Spacer(modifier = modifier.weight(1f))
    }
}