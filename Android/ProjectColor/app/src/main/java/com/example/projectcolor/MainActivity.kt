package com.example.projectcolor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectcolor.ui.theme.ProjectColorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectColorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val pixelGridMatrix = remember { mutableStateOf(RGBMatrix(16, 16)) }
    var isConnected by remember { mutableStateOf(false) }
    val selectedColor = remember { mutableStateOf<Color?>(Color.White) }
    val onColorSelected: (Color) -> Unit = { color ->
        selectedColor.value = color
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SizeDisplay()
            Spacer(modifier = Modifier.weight(1f))
            BluetoothConnectionIndicator(
                isConnected = isConnected,
                onConnectClick = { isConnected = true},
                onDisconnectClick = { isConnected = false})
            Spacer(modifier = Modifier.weight(1f))
            ColorPickerButtons(onColorSelected = onColorSelected)
            PixelGrid(selectedColor = selectedColor, size = 16, matrix = pixelGridMatrix)
            SendButton(modifier = Modifier.align(Alignment.CenterHorizontally), matrix = pixelGridMatrix)
        }
    }
}

@Composable
fun SizeDisplay(modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "8x8", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun BluetoothConnectionIndicator(
    modifier: Modifier = Modifier,
    isConnected: Boolean,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                color = if (isConnected) Color.Green else Color.Red
            )
            .clickable {
                if (isConnected) {
                    onDisconnectClick()
                } else {
                    onConnectClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = if (isConnected) "Connected" else "Disconnected",
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (isConnected) "Tap to disconnect" else "Tap to connect",
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
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
        modifier = modifier
            .padding(0.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(0.dp),
    )
    {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
////            Text(
////                text = "$column $row",
////                fontSize = (6.sp),
////                maxLines = 1)
//        }
    }
}

@Composable
fun PixelGrid(
    modifier: Modifier = Modifier,
    size: Int = 8,
    selectedColor: State<Color?>,
    matrix: MutableState<RGBMatrix>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(
                RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp)),
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Center) {
            for (column in 0 until size) {
                Row(
                    modifier = modifier
                        .fillMaxWidth(1f)
                        .padding(0.dp)
                ) {
//                val density = LocalDensity.current // Access density for size calculation
//                val screenWidth = DisplayMetrics().widthPixels // Get screen width in pixels
//                val rowSize = (screenWidth / size).dp(density) // Calculate square size based on screen and count
//                val rowSize = (screenWidth / size).dp

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
fun SendButton(
    modifier: Modifier = Modifier,
    matrix: MutableState<RGBMatrix>
) {
    Row(
        modifier = modifier.padding(4.dp),) {
        Button(
            modifier = Modifier.width(120.dp).padding(end = 4.dp),
            onClick = { logMatrixInfo(matrix) },
        ) {
            Text(text = "Send")
        }
        Button(
            modifier = Modifier.width(120.dp).padding(start = 4.dp),
            onClick = { /*TODO*/ },
        ) {
            Text(text = "Receive")
        }
    }
}

@SuppressLint("DefaultLocale")
fun logMatrixInfo(matrix: MutableState<RGBMatrix>) {
    for (row in 0 until 1/*matrix.value.width*/) {
        for (column in 0 until matrix.value.height) {
            val pixel = matrix.value.getPixel(row, column)
            val redString = String.format("%03d", (pixel.red * 255f).toInt())
            val greenString = String.format("%03d", (pixel.green * 255f).toInt())
            val blueString = String.format("%03d", (pixel.blue * 255f).toInt())
            val message = "Pixel($row, $column) - R: $redString, G: $greenString, B: $blueString"
            Log.d("PixelGrid", message)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProjectColorTheme {
        MainScreen()
    }
}