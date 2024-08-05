package com.example.projectcolor.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@SuppressLint("MissingPermission")
@Composable
fun ShowPairedDevices(
    context: Context,
    pairedDevices: Set<BluetoothDevice>,
    onConnectClick: (BluetoothDevice) -> Unit
) {
    if (pairedDevices.isNotEmpty()) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(pairedDevices.toList()) { device ->
                    DeviceItem(device = device, onConnectClick = onConnectClick)
                }
            }
        }
    } else {
        Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun showDiscoveredDevices(
    context: Context,
    discoveredDevices: Set<BluetoothDevice>,
    onConnectClick: (BluetoothDevice) -> Unit
) {
    if (discoveredDevices.isNotEmpty()) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(discoveredDevices.toList()) { device ->
                    DeviceItem(device = device, onConnectClick = onConnectClick)
                }
            }
        }
    } else {
        Text(text = "Scanning for devices...", color = Color.White)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onConnectClick: (BluetoothDevice) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = device.name ?: "Unknown Device")
            Text(text = device.address)
        }
        Button(onClick = { onConnectClick(device) }) {
            Text(text = "Connect")
        }
    }
}