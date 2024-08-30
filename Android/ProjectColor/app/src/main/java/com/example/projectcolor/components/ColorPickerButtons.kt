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

/**
 * ColorPickerButtons is a Composable function that renders a horizontal row of color selection buttons, allowing the user
 * to pick a color from a predefined list. The selected color is highlighted, and a callback function is invoked when a
 * new color is selected.
 *
 * **Parameters:**
 *
 * - `modifier`: A `Modifier` used to adjust the layout and appearance of the row. The default value is `Modifier`, which
 *   applies no modification.
 * - `onColorSelected`: A callback function that is triggered whenever the user selects a color. It takes the selected
 *   `Color` as its argument.
 *
 * **State:**
 *
 * - `selectedIndex`: A mutable state that tracks the index of the currently selected color. Initially set to `0`
 *   (the first color in the list).
 * - `colors`: A list of `Color` objects representing the available colors for selection. The colors provided are
 *   `Red`, `Green`, `Blue`, `White`, `Black`, and `LightGray`.
 *
 * **UI Structure:**
 *
 * - The function uses a `Row` composable to arrange the buttons horizontally, centered within the parent, and
 *   padded with `16.dp` on the left and right, and `8.dp` on the top and bottom.
 *
 * - Inside the `Row`, a `SingleChoiceSegmentedButtonRow` composable is used to hold the individual `SegmentedButton`
 *   elements, each representing a color option.
 *
 * - For each color in the `colors` list, a `SegmentedButton` is created with the following characteristics:
 *   - `border`: A border matching the color of the button, with a stroke width of `2.dp`.
 *   - `onClick`: An action that updates the `selectedIndex` and triggers the `onColorSelected` callback with the selected color.
 *   - `colors`: Defines the appearance of the button, setting `activeContainerColor` to the button's color and
 *     `inactiveContainerColor` to transparent.
 *   - `selected`: A boolean that indicates if the button is currently selected, based on the `selectedIndex`.
 *
 * This function provides a simple and intuitive interface for color selection, typically used in scenarios
 * like drawing applications or custom UI theme selectors.
 */
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