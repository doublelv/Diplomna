package com.example.projectcolor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerButtons(
    modifier: Modifier = Modifier,
    onColorSelected: (Color) -> Unit
) {

    var selectedIndex by remember { mutableIntStateOf(0) }
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black, Color.LightGray)

    Row(modifier = modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        SingleChoiceSegmentedButtonRow {
            colors.forEachIndexed { index, option ->
                SegmentedButton(
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
                    shape = SegmentedButtonDefaults.baseShape,
                    border = SegmentedButtonDefaults.borderStroke(color = option, width = 2.dp),
                    onClick = {
                        selectedIndex = index
                        onColorSelected(option)
                    },
                    colors = SegmentedButtonDefaults.colors(
                        inactiveContainerColor = Color.Transparent,
                        activeContainerColor = option
                    ),
                    selected = index == selectedIndex
                ) {}
            }
        }
    }
}