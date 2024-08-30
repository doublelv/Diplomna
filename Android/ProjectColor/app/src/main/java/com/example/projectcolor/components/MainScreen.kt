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


/**
 * MainScreen is a composable function that serves as the main user interface for an application
 * involving Bluetooth connectivity and pixel grid manipulation. It manages the overall layout and
 * integrates several key components including a size display, Bluetooth connection controls,
 * color selection, pixel grid interaction, and a button to send data.
 *
 * The function performs the following:
 *
 * - Retrieves the current `Context` and initializes a `BluetoothManager` for handling Bluetooth operations.
 * - Maintains a state for a 16x16 RGB pixel grid using `mutableStateOf` to track and update the grid matrix.
 * - Tracks the Bluetooth connection status with a mutable state variable, `isConnected`.
 * - Manages a mutable state for the currently selected color, which defaults to `Color.Black`.
 *
 * The user interface is structured using a `Scaffold`, containing:
 *
 * - A `SizeDisplay` composable that displays relevant size information.
 * - A `BluetoothConnectionIndicator` that allows the user to connect or disconnect from a Bluetooth device.
 * - A `ColorPickerButtons` composable that provides color selection buttons to update the selected color state.
 * - A `PixelGrid` composable that displays a grid of pixels, allowing interaction based on the selected color.
 * - A `SendButton` composable that sends the current state of the pixel grid via Bluetooth when clicked.
 *
 * The function ensures that all user actions, such as connecting to Bluetooth, selecting colors, and sending
 * the pixel grid data, are handled efficiently while maintaining the correct states within the user interface.
 */
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val bluetoothManager = remember { BluetoothManager(context) }
    val pixelGridMatrix = remember { mutableStateOf(RGBMatrix(16, 16)) }
    var isConnected by remember { mutableStateOf(false) }
    val selectedColor = remember { mutableStateOf(Color.Black) } // State<Color>

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
                onConnectClick = {

                    // Handle Bluetooth connection
                    if (!isConnected) {
                        bluetoothManager.startDiscovery()
                        isConnected = true
                    }
                },
                onDisconnectClick = {
                    // Handle Bluetooth disconnection
                    if (isConnected) {
                        bluetoothManager.cancelConnection()
                        isConnected = false
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            ColorPickerButtons(onColorSelected = { color ->
                selectedColor.value = color // Update the mutable state
            })

            PixelGrid(
                modifier = Modifier.fillMaxWidth(),
                selectedColor = selectedColor, // Pass the state
                size = 16,
                matrix = pixelGridMatrix
            )

            SendButton(
                bluetoothManager = bluetoothManager,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                matrix = pixelGridMatrix
            )
        }
    }
}
