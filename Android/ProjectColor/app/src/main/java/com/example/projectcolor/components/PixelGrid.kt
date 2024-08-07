package com.example.projectcolor.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.projectcolor.PixelData
import com.example.projectcolor.RGBMatrix


@Composable
fun PixelGrid(
    modifier: Modifier = Modifier,
    size: Int = 8,
    selectedColor: State<Color?>,
    matrix: MutableState<RGBMatrix>
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .fillMaxWidth(1f)
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp)
            ),
    ) {
        Column(
            modifier = modifier.padding(0.dp).aspectRatio(1f),
            verticalArrangement = Arrangement.Center) {
            for (column in 0 until size) {
                Row(
                    modifier = Modifier
                        .padding(0.dp)
                        .weight(1f)
                        .fillMaxWidth(1f)
                ) {
                    for (row in 0 until size) {
                        PixelButton(
                            modifier = Modifier.weight(1f),
                            column = column,
                            row = row,
                            selectedColor.value,
                            matrix
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PixelButton(
    modifier: Modifier = Modifier,
    column: Int,
    row: Int,
    selectedColor: Color?,
    matrix: MutableState<RGBMatrix>
) {

    var buttonColor  by remember { mutableStateOf(Color.Black) }

    Button(
        onClick = {
            buttonColor = selectedColor ?: Color.Black
            Log.d("selectedColor", "Selected Color is ($buttonColor)")
            val pixel = PixelData(buttonColor.red, buttonColor.green, buttonColor.blue)
            matrix.value.setPixel(column, row, pixel)
        },
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = modifier,
//            .fillMaxSize(1f)
//            .aspectRatio(1f),
//        border = BorderStroke(1.dp, Color.White),
        shape = RoundedCornerShape(0.dp),
    )
    {}
}