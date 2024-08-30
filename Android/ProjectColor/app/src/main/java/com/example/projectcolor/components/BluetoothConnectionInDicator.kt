package com.example.projectcolor.components

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.projectcolor.bluetooth.BluetoothManager


/**
 * BluetoothConnectionIndicator is a composable function that provides a user interface element for managing
 * Bluetooth connectivity within the application. It allows users to connect to or disconnect from a Bluetooth device
 * and displays the connection status.
 *
 * The function includes:
 *
 * - `modifier`: A `Modifier` for customizing the appearance and layout of the component.
 * - `bluetoothManager`: An instance of `BluetoothManager` to handle Bluetooth operations.
 * - `isConnected`: A `Boolean` representing the current connection status (true if connected, false otherwise).
 * - `onConnectClick`: A callback function that is invoked when a connection to a Bluetooth device is requested.
 * - `onDisconnectClick`: A callback function that is invoked when a disconnection from a Bluetooth device is requested.
 *
 * The UI is structured within a `Box` composable that serves as a clickable area. The background color of the box
 * changes based on the connection status (`Green` for connected, `Red` for disconnected). The box contains:
 *
 * - A `Text` element displaying "Connected" or "Disconnected" based on the connection status.
 * - A secondary `Text` element providing a prompt to the user ("Tap to disconnect" or "Tap to connect").
 * - An optional error message if a connection attempt fails.
 *
 * When the box is clicked, the function checks the Bluetooth status:
 *
 * - If connected, it triggers the `onDisconnectClick` callback and cancels the connection using `bluetoothManager`.
 * - If disconnected, it checks for Bluetooth support, permissions, and enables Bluetooth if necessary. It then
 *   starts device discovery and shows a dialog with available devices for connection.
 *
 * The `DevicesDialog` is displayed when `showDevicesDialog` is true, allowing users to select and connect to a
 * Bluetooth device. The function handles the connection process, updating the UI to reflect loading or connection
 * errors as needed.
 */
@SuppressLint("MissingPermission")
@Composable
fun BluetoothConnectionIndicator(
    modifier: Modifier = Modifier,
    bluetoothManager: BluetoothManager,
    isConnected: Boolean,
    onConnectClick: (BluetoothDevice) -> Unit,
    onDisconnectClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    var showDevicesDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Loading state
    var connectionError by remember { mutableStateOf(false) } // Connection error state

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (bluetoothManager.hasPermissions()) {
                bluetoothManager.startDiscovery()
                showDevicesDialog = true
            }
        }
    }

    val discoveredDevices by bluetoothManager.discoveredDevices.observeAsState(emptySet())

    Box(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(color = if (isConnected) Color.Green else Color.Red)
            .clickable {
                if (isConnected) {
                    onDisconnectClick()
                    bluetoothManager.cancelConnection()
                } else {
                    if (!bluetoothManager.hasBluetoothSupport()) {
                        return@clickable
                    }
                    if (bluetoothManager.isBluetoothEnabled()) {
                        if (!bluetoothManager.hasPermissions()) {
                            bluetoothManager.requestPermissions(activity, 1001)
                        } else {
                            bluetoothManager.startDiscovery()
                            showDevicesDialog = true
                        }
                    } else {
                        bluetoothManager.enableBluetooth(enableBluetoothLauncher)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isConnected) "Connected" else "Disconnected",
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            Text(
                text = if (isConnected) "Tap to disconnect" else "Tap to connect",
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )

            if (connectionError) {
                Text(
                    text = "Connection failed. Try again.",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        }

        if (showDevicesDialog) {
            DevicesDialog(
                pairedDevices = bluetoothManager.getPairedDevices(),
                discoveredDevices = discoveredDevices,
                onDismissRequest = {
                    showDevicesDialog = false
                    bluetoothManager.cancelConnection()
                },
                onConnectClick = { device ->
                    isLoading = true
                    connectionError = false
                    bluetoothManager.connectToDevice(device) { success ->
                        isLoading = false
                        if (success) {
                            onConnectClick(device)
                            showDevicesDialog = false
                        } else {
                            connectionError = true
                            showDevicesDialog = true
                        }
                    }
                }
            )
        }
    }
}


/**
 * DevicesDialog is a composable function that displays a dialog containing a list of paired and discovered Bluetooth
 * devices, allowing the user to select a device for connection.
 *
 * The function includes:
 *
 * - `pairedDevices`: A set of `BluetoothDevice` objects representing devices that are already paired with the host.
 * - `discoveredDevices`: A set of `BluetoothDevice` objects representing devices that have been discovered but are
 *   not yet paired.
 * - `onDismissRequest`: A callback function that is invoked when the dialog is dismissed.
 * - `onConnectClick`: A callback function that is invoked when the user selects a device to connect to.
 *
 * The dialog is structured using a `Column` composable that contains:
 *
 * - A header text "Paired Devices" to label the list of paired devices.
 * - A `LazyColumn` displaying the paired devices, each rendered by the `DeviceItem` composable.
 * - A header text "Available Devices" to label the list of discovered devices.
 * - A `LazyColumn` (currently commented out) intended to display the discovered devices, each rendered by
 *   the `DeviceItem` composable.
 *
 * This dialog provides a user-friendly interface for selecting Bluetooth devices for connection, facilitating
 * interaction with both paired and newly discovered devices.
 */
@Composable
fun DevicesDialog(
    pairedDevices: Set<BluetoothDevice>,
    discoveredDevices: Set<BluetoothDevice>,
    onDismissRequest: () -> Unit,
    onConnectClick: (BluetoothDevice) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Paired Devices",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(pairedDevices.toList()) { device ->
                    DeviceItem(device = device, onConnectClick = onConnectClick)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Available Devices",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
//            LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                items(discoveredDevices.toList()) { device ->
//                    DeviceItem(device = device, onConnectClick = onConnectClick)
//                }
//            }
        }
    }
}


/**
 * DeviceItem is a composable function that displays information about a single Bluetooth device, along with a
 * button to initiate a connection to that device.
 *
 * The function includes:
 *
 * - `device`: A `BluetoothDevice` object representing the Bluetooth device to be displayed.
 * - `onConnectClick`: A callback function that is invoked when the user clicks the "Connect" button.
 *
 * The UI is structured within a `Row` composable that contains:
 *
 * - A `Column` displaying the device's name (or "Unknown Device" if the name is not available) and its address.
 * - A `Button` that, when clicked, triggers the `onConnectClick` callback with the associated device.
 *
 * This function provides a clear and concise display of Bluetooth device information, along with a straightforward
 * method for initiating a connection, making it an essential component within a device selection dialog.
 */
@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onConnectClick: (BluetoothDevice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name ?: "Unknown Device",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(
            onClick = { onConnectClick(device) },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = "Connect")
        }
    }
}