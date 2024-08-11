package com.example.projectcolor.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.State
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.toSize
//import com.example.projectcolor.PixelData
//import com.example.projectcolor.RGBMatrix
//
//
//
//@Composable
//fun PixelGrid(
//    modifier: Modifier = Modifier,
//    size: Int = 8,
//    selectedColor: State<Color?>,
//    matrix: MutableState<RGBMatrix>
//) {
//    var containerSize by remember { mutableStateOf(Size.Zero) }
//
//    Box(
//        modifier = Modifier
//            .padding(4.dp)
//            .aspectRatio(1f)
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(20.dp))
//            .border(1.dp, Color.White, RoundedCornerShape(20.dp))
//            .onGloballyPositioned { coordinates ->
//                containerSize = coordinates.size.toSize()
//            }
//    ) {
//        Column(
//            modifier = modifier.padding(0.dp).aspectRatio(1f),
//            verticalArrangement = Arrangement.Center
//        ) {
//            for (column in 0 until size) {
//                Row(
//                    modifier = Modifier
//                        .padding(0.dp)
//                        .fillMaxWidth()
//                ) {
//                    for (row in 0 until size) {
//                        PixelBox(
//                            modifier = Modifier
//                                .weight(1f)
//                                .aspectRatio(1f),
//                            column = column,
//                            row = row,
//                            selectedColor.value,
//                            matrix,
//                            containerSize,
//                            size
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun PixelBox(
//    modifier: Modifier = Modifier,
//    column: Int,
//    row: Int,
//    selectedColor: Color?,
//    matrix: MutableState<RGBMatrix>,
//    containerSize: Size,
//    gridSize: Int
//) {
//    var boxColor by remember { mutableStateOf(Color.Black) }
//
//    Box(
//        modifier = modifier
//            .background(boxColor)
//            .border(1.dp, Color.White)
//            .pointerInput(Unit) {
//                detectTapGestures { offset ->
//                    // Update color on tap
//                    boxColor = selectedColor ?: Color.Black
//                    updatePixel(column, row, selectedColor, matrix)
//                }
//                detectDragGestures { change, _ ->
//                    // Update color on drag
//                    val x = change.position.x
//                    val y = change.position.y
//
//                    // Convert touch position to grid cell
//                    val cellWidth = containerSize.width / gridSize
//                    val cellHeight = containerSize.height / gridSize
//
//                    val dragColumn = (x / cellWidth).toInt().coerceIn(0, gridSize - 1)
//                    val dragRow = (y / cellHeight).toInt().coerceIn(0, gridSize - 1)
//
//                    // Update pixel in matrix
//                    updatePixel(dragColumn, dragRow, selectedColor, matrix)
//                }
//            }
//    )
//}
//
//private fun updatePixel(
//    column: Int,
//    row: Int,
//    selectedColor: Color?,
//    matrix: MutableState<RGBMatrix>
//) {
//    val pixel = PixelData(
//        selectedColor?.red ?: 0f,
//        selectedColor?.green ?: 0f,
//        selectedColor?.blue ?: 0f
//    )
//    matrix.value.setPixel(column, row, pixel)
//}