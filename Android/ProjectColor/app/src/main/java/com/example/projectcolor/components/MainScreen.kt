package com.example.projectcolor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.projectcolor.RGBMatrix
import com.example.projectcolor.bluetooth.BluetoothManager

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val bluetoothManager = remember { BluetoothManager(context)}
    val pixelGridMatrix = remember { mutableStateOf(RGBMatrix(16, 16)) }
    var isConnected by remember { mutableStateOf(false) }
    val selectedColor = remember { mutableStateOf<Color?>(Color.Red) }
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
                bluetoothManager = bluetoothManager,
                isConnected = isConnected,
                onConnectClick = { isConnected = true},
                onDisconnectClick = { isConnected = false})
            Spacer(modifier = Modifier.weight(1f))
            ColorPickerButtons(onColorSelected = onColorSelected)
            PixelGrid(
                modifier = Modifier.fillMaxWidth(1f),
                selectedColor = selectedColor,
                size = 16,
                matrix = pixelGridMatrix)
            SendButton(
                bluetoothManager = bluetoothManager,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                matrix = pixelGridMatrix)
        }
    }
}
