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

@Composable
fun BluetoothConnectionIndicator(
    modifier: Modifier = Modifier,
    isConnected: Boolean,
    onConnectClick: (BluetoothDevice) -> Unit,
    onDisconnectClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val bluetoothManager = remember { BluetoothManager(context) }

    var showPairedDevicesDialog by remember { mutableStateOf(false) }
    var showDiscoveredDevicesDialog by remember { mutableStateOf(false) }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (bluetoothManager.hasPermissions()) {
                showDiscoveredDevicesDialog = true
                bluetoothManager.startDiscovery()
            }
        }
    }

    val discoveredDevices by bluetoothManager.discoveredDevices.observeAsState(emptySet())

    Box(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                color = if (isConnected) Color.Green else Color.Red
            )
            .clickable {
                if (isConnected) {
                    onDisconnectClick()
                } else {
                    if (!bluetoothManager.hasBluetoothSupport()) {
                        return@clickable
                    }

                    if (bluetoothManager.isBluetoothEnabled()) {
                        if (!bluetoothManager.hasPermissions()) {
                            bluetoothManager.requestPermissions(activity, 1001)
                        } else {
                            showPairedDevicesDialog = true
                            showDiscoveredDevicesDialog = true
                            bluetoothManager.startDiscovery()
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
        }

//        if (showPairedDevicesDialog) {
//            PairedDevicesDialog(
//                pairedDevices = bluetoothManager.getPairedDevices(),
//                onDismissRequest = { showPairedDevicesDialog = false },
//                onConnectClick = { device ->
//                    onConnectClick(device)
//                    showPairedDevicesDialog = false
//                }
//            )
//        }
//
//        if (showDiscoveredDevicesDialog) {
//            DiscoveredDevicesDialog(
//                discoveredDevices = discoveredDevices,
//                onDismissRequest = { showDiscoveredDevicesDialog = false },
//                onConnectClick = { device ->
//                    onConnectClick(device)
//                    showDiscoveredDevicesDialog = false
//                }
//            )
//        }

        if (showPairedDevicesDialog && showDiscoveredDevicesDialog) {
            DevicesDialog(
                pairedDevices = bluetoothManager.getPairedDevices(),
                discoveredDevices = discoveredDevices,
                onDismissRequest = { showPairedDevicesDialog = false },
                onConnectClick = { device ->
                    onConnectClick(device)
                    showPairedDevicesDialog = false
                }
            )
        }
    }
}

@Composable
fun PairedDevicesDialog(
    pairedDevices: Set<BluetoothDevice>,
    onDismissRequest: () -> Unit,
    onConnectClick: (BluetoothDevice) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text(
                    text = "Paired Devices",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(pairedDevices.toList()) { device ->
                DeviceItem(device = device, onConnectClick = onConnectClick)
            }
        }
    }
}

@Composable
fun DiscoveredDevicesDialog(
    discoveredDevices: Set<BluetoothDevice>,
    onDismissRequest: () -> Unit,
    onConnectClick: (BluetoothDevice) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text(
                    text = "Available Devices",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(discoveredDevices.toList()) { device ->
                DeviceItem(device = device, onConnectClick = onConnectClick)
            }
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