package com.example.projectcolor.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectcolor.PixelData
import com.example.projectcolor.RGBMatrix

/**
 * PixelGrid is a Composable function that displays a grid of interactive pixels, allowing users to "paint" each pixel
 * by selecting a color and clicking on the respective grid cell. The grid's size and color state are customizable, and
 * the painted colors are stored in an RGB matrix.
 *
 * **Parameters:**
 *
 * - `modifier`: A `Modifier` that can be used to customize the appearance and layout of the grid. The default value is `Modifier`.
 * - `size`: An `Int` representing the size of the grid (both width and height in number of pixels). The default value is `16`.
 * - `selectedColor`: A `State<Color?>` representing the currently selected color for painting the pixels. If `null`, the
 *   default color `Black` is used.
 * - `matrix`: A `MutableState<RGBMatrix>` representing the underlying data structure that holds the color values of the
 *   pixels in the grid.
 *
 * **UI Structure:**
 *
 * - The function creates a `Box` composable to define the grid's outer boundary, adding padding, aspect ratio, rounded corners,
 *   and a white border.
 *
 * - Inside the `Box`, a `Column` is used to arrange `Row` composables vertically, where each `Row` represents a line of pixels.
 *
 * - Each `Row` contains a series of `PixelButton` composables, one for each pixel in the grid, arranged horizontally.
 *
 * - The grid size is defined by the `size` parameter, resulting in a `size x size` grid. The `PixelButton` for each
 *   cell is generated based on the `column` and `row` indices, and the button's color is dynamically updated
 *   when clicked, using the selected color.
 *
 * This function provides a simple and flexible way to create a customizable, interactive pixel grid, typically used
 * in pixel art applications or educational tools for learning about colors and grids.
 */
@Composable
fun PixelGrid(
    modifier: Modifier = Modifier,
    size: Int = 16,
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
                    bottomEnd = 20.dp))
            .border(1.dp, Color.White, RoundedCornerShape(20.dp))
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

/**
 * PixelButton is a Composable function that represents an individual button within a pixel grid, allowing users to
 * "paint" a specific pixel by selecting a color and clicking the button.
 *
 * **Parameters:**
 *
 * - `modifier`: A `Modifier` to apply to this button. It can be used to customize the appearance and layout of the button. The default value is `Modifier`.
 * - `column`: An `Int` representing the column index of this pixel within the grid.
 * - `row`: An `Int` representing the row index of this pixel within the grid.
 * - `selectedColor`: A nullable `Color` that represents the currently selected color for painting. If `null`, the default color `Black` is used.
 * - `matrix`: A `MutableState<RGBMatrix>` that holds the RGB values of all pixels in the grid, allowing the function to update the color of this specific pixel.
 *
 * **Functionality:**
 *
 * - The function maintains a mutable state `buttonColor` which initially is set to `Color.Black`.
 *
 * - When the button is clicked, it updates `buttonColor` to the currently selected color, or `Color.Black` if no color is selected.
 *
 * - The function logs the selected color for debugging purposes.
 *
 * - The color value is then converted into a `PixelData` object containing the red, green, and blue components of the color.
 *
 * - The `PixelData` object is stored in the `matrix` at the specified `column` and `row` indices using the `setPixel` function.
 *
 * - The button's appearance is updated to reflect the `buttonColor`, and the button is styled with a rectangular shape by
 *   setting the corner radius to `0.dp`.
 */

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
        modifier = modifier,
        onClick = {
            buttonColor = selectedColor ?: Color.Black
            Log.d("selectedColor", "Selected Color is ($buttonColor)")
            val pixel = PixelData(buttonColor.red, buttonColor.green, buttonColor.blue)
            matrix.value.setPixel(column, row, pixel)
        },
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(0.dp),
//        border = ButtonDefaults.outlinedButtonBorder
    ) {}
}


@Preview
@Composable
fun PixelGridPreview() {
    val selectedColor = remember { mutableStateOf<Color?>(null) }
    val matrix = remember { mutableStateOf(RGBMatrix(16, 16)) }
    PixelGrid(selectedColor = selectedColor, matrix = matrix)

}