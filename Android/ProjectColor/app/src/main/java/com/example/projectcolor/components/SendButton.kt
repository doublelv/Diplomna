package com.example.projectcolor.components

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projectcolor.RGBMatrix
import com.example.projectcolor.bluetooth.BluetoothManager
import java.io.IOException

// Button to send the matrix data to the device
@Composable
fun SendButton(
    modifier: Modifier = Modifier,
    matrix: MutableState<RGBMatrix>,
    bluetoothManager: BluetoothManager
) {
    val isBluetoothConnected = bluetoothManager.isConnected()
    Row(
        modifier = modifier.padding(4.dp)) {
        Button(
            modifier = Modifier
                .width(120.dp)
                .padding(end = 4.dp),
            onClick = {
                //sendFullMatrixData(matrix, bluetoothManager)
                communicationValidationTest(bluetoothManager)
            },
            enabled = isBluetoothConnected
        ) {
            Text(text = "Send")
        }
    }
}

// Adds checksum to a row of pixel data
fun addChecksumToRow(data: String): String{
    var checksum = checkSum(hexStringToBinaryString(data), 8)
    checksum = checksum.toInt(2).toString(16).padStart(2, '0')
    return (data + checksum)
}

// Serializes a full row of pixel data into a byte array
@OptIn(ExperimentalStdlibApi::class)
fun serializeRow(
    matrix: MutableState<RGBMatrix>,
    row: Int = 0,
    ): String {

    val byteArray = ByteArray(4*16)
    var index = 0
    try {
        for (column in 0 until matrix.value.width) {
            val pixel = matrix.value.getPixel(row, column)
            byteArray[index] = ((row shl 4) + column).toByte()
            byteArray[index+1] = (pixel.red * 255f).toInt().toByte()
            byteArray[index+2] = (pixel.green * 255f).toInt().toByte()
            byteArray[index+3] = (pixel.blue * 255f).toInt().toByte()
            index += 4
        }
    } catch (e: IOException) {
        Log.e("MainActivity", "Error serializing matrix", e)
    }

    var byteArrayString = byteArray.toHexString()
    byteArrayString = addChecksumToRow(byteArrayString) + "\n"

    return byteArrayString
}

// Serializes the full matrix of pixel data into a byte array
fun serializeMatrix(matrix: MutableState<RGBMatrix>): String {
    var outputString = ""
    try {
        for (row in 0 until matrix.value.height) {
            val serializedRow = serializeRow(matrix, row)
            outputString += serializedRow + "\n"
        }
    } catch (e: IOException) {
        Log.e("MainActivity", "Error serializing matrix", e)
    }
    return outputString
}

fun sendFullMatrixData(matrix: MutableState<RGBMatrix>, bluetoothManager: BluetoothManager) {
    for (row in 0 until matrix.value.height) {
        sendRow(matrix, row, bluetoothManager)
        Thread.sleep(500)
    }
}

fun sendRow(matrix: MutableState<RGBMatrix>, row: Int, bluetoothManager: BluetoothManager) {
    val serializedRow = serializeRow(matrix, row)
    Log.d("SendButton", "serializedRow: \n$serializedRow")

    bluetoothManager.sendData(serializedRow)   // / is a delimiter
}

// TEST
fun communicationValidationTest(bluetoothManager: BluetoothManager) {
    val retryLimit = 3
    val timeoutMillis = 5000L
    var retryCount = 0
    var handshakeSuccessful = false

    while (retryCount < retryLimit && !handshakeSuccessful) {
        bluetoothManager.sendData("syn")
        Log.d("SendButton", "SYN sent, waiting for SYN-ACK...")

        val response = bluetoothManager.receiveData(timeoutMillis)
        if (response == "syn-ack") {
            Log.d("SendButton", "SYN-ACK received.")
            bluetoothManager.sendData("ack")
            Log.d("SendButton", "ACK sent.")
            handshakeSuccessful = true
        } else {
            retryCount++
            Log.d("SendButton", "SYN-ACK not received, retrying... ($retryCount/$retryLimit)")
            Thread.sleep(1000)
        }
    }

    if (handshakeSuccessful) {
        val message = "Hello, World!"
        bluetoothManager.sendData("data: $message")
        Log.d("SendButton", "Data sent: $message")

        val dataAck = bluetoothManager.receiveData(timeoutMillis)
        if (dataAck == "data-ack") {
            bluetoothManager.sendData("fin")
            Log.d("SendButton", "FIN sent, waiting for FIN-ACK...")

            val finAck = bluetoothManager.receiveData(timeoutMillis)
            if (finAck == "fin-ack") {
                bluetoothManager.sendData("ack")
                Log.d("SendButton", "FIN-ACK received, connection terminated.")
            } else {
                Log.d("SendButton", "Failed to terminate connection: FIN-ACK not received.")
            }
        } else {
            Log.d("SendButton", "Failed to receive data acknowledgment.")
        }
    } else {
        Log.d("SendButton", "Failed to establish connection after $retryLimit attempts.")
    }
}


