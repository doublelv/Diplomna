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
//    val bluetoothManager = remember { BluetoothManager(context) }

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
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(discoveredDevices.toList()) { device ->
                    DeviceItem(device = device, onConnectClick = onConnectClick)
                }
            }
        }
    }
}

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