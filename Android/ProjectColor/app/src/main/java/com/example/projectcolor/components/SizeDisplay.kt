package com.example.projectcolor.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


/**
 * SizeDisplay is a composable function that displays the dimensions of a pixel grid within the user interface.
 * It provides a simple and centered textual representation of the grid size, which in this case is "16x16".
 *
 * The function performs the following:
 *
 * - Utilizes a `Row` composable to arrange its child elements horizontally.
 * - Centers the text horizontally within the available space by using `Spacer` composables with equal weight on
 *   either side of the text, ensuring that the "16x16" text is perfectly centered in the row.
 * - Displays the grid size text ("16x16") using a `Text` composable, styled with the `titleLarge` typography
 *   from the `MaterialTheme`, providing a clear and prominent display.
 *
 * This function is typically used in a larger layout to indicate the dimensions of the pixel grid being manipulated
 * or displayed, offering a clear visual cue to the user about the grid's size.
 */

@Composable
fun SizeDisplay() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "16x16", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))
    }
}