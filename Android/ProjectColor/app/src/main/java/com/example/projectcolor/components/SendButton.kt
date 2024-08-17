package com.example.projectcolor.components

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.projectcolor.RGBMatrix
import com.example.projectcolor.bluetooth.BluetoothManager

// Button to send the matrix data to the device
@Composable
fun SendButton(
    modifier: Modifier = Modifier,
    matrix: MutableState<RGBMatrix>,
    bluetoothManager: BluetoothManager,
) {
    val isBluetoothConnected = bluetoothManager.isConnected()
    val context = LocalContext.current
    Row(
        modifier = modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {

        Button(
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 4.dp)
            ,
            onClick = {
                handshakeSendSinglePixelsOneByOne(matrix, bluetoothManager, context);
            },
            enabled = isBluetoothConnected
        ) {
            Text(text = "Send")
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
                sendColor("red", bluetoothManager)
            },
            enabled = isBluetoothConnected
        ) {}

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Green),
            onClick = {
                sendColor("green", bluetoothManager)
            },
            enabled = isBluetoothConnected
        ) {
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Blue),
            onClick = {
                sendColor("blue", bluetoothManager)
            },
            enabled = isBluetoothConnected
        ) {

        }

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Black),
            onClick = {
                sendColor("black", bluetoothManager)
            },
            enabled = isBluetoothConnected
        ) {

        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.White),
            onClick = {
                sendColor("white", bluetoothManager)
            },
            enabled = isBluetoothConnected
        ) {

        }
    }
}


fun handshakeSendSinglePixelsOneByOne(
    matrix: MutableState<RGBMatrix>,
    bluetoothManager: BluetoothManager,
    context: Context // Add context to show Toast messages
) {
    val retryLimit = 3
    val timeoutMillis = 5000L
    var retryCount = 0

    fun performHandshake(): Boolean {
        while (retryCount < retryLimit && bluetoothManager.isConnected()) {
            bluetoothManager.sendData("syn")
            Log.d("SendButton", "SYN sent, waiting for SYN-ACK...")
//            Toast.makeText(context, "SYN sent, waiting for SYN-ACK...", Toast.LENGTH_SHORT).show()

            val response = bluetoothManager.receiveData(timeoutMillis)
            if (response == "syn-ack") {
                bluetoothManager.sendData("ack")
                Log.d("SendButton", "ACK sent. Handshake successful.")
                Toast.makeText(context, "ACK sent. Handshake successful.", Toast.LENGTH_SHORT).show()
                return true
            } else {
                retryCount++
                Log.d("SendButton", "SYN-ACK not received, retrying... ($retryCount/$retryLimit)")
//                Toast.makeText(context, "SYN-ACK not received, retrying... ($retryCount/$retryLimit)", Toast.LENGTH_SHORT).show()
                Thread.sleep(1000)
            }
        }
        Log.d("SendButton", "Failed to establish connection after $retryLimit attempts.")
        Toast.makeText(context, "Failed to establish connection after $retryLimit attempts.", Toast.LENGTH_LONG).show()
        return false
    }

    fun sendMatrixPixelByPixel(): Boolean {
        for (row in 0 until 1 * matrix.value.height) {
            for (column in 0 until matrix.value.width) {
                var tryCount = 0
                val pixel = matrix.value.getPixel(row, column)
                var pixelAck = "PIXEL-FAIL"

                while (pixelAck != "PIXEL-SUCCESS" && tryCount < 20) {
                    sendPixel(pixel, row, column, bluetoothManager, "data:")
                    pixelAck = bluetoothManager.receiveData(timeoutMillis).toString()
                    tryCount++
//                    Thread.sleep(1)
                }

                if(pixelAck != "PIXEL-SUCCESS") {
                    Log.d("SendButton", "Failed to send pixel: $pixel, received: $pixelAck")
                    return false
                }
            }
        }
        return true
    }

    fun terminateConnection() {
        bluetoothManager.sendData("fin")
        Log.d("SendButton", "FIN sent, waiting for FIN-ACK...")
//        Toast.makeText(context, "FIN sent, waiting for FIN-ACK...", Toast.LENGTH_SHORT).show()

        val finAck = bluetoothManager.receiveData(timeoutMillis)
        if (finAck == "fin-ack") {
            Log.d("SendButton", "FIN-ACK received, connection terminated.")
            Toast.makeText(context, "Data sent successfully and connection terminated.", Toast.LENGTH_LONG).show()
        } else {
            Log.d("SendButton", "Failed to terminate connection: FIN-ACK not received.")
            Toast.makeText(context, "Failed to terminate connection.", Toast.LENGTH_LONG).show()
        }
    }

    if (performHandshake() && sendMatrixPixelByPixel()) {
        terminateConnection()
    } else {
        Toast.makeText(context, "Failed to send matrix data.", Toast.LENGTH_LONG).show()
    }
}

fun sendColor(color: String, bluetoothManager: BluetoothManager) {
    if (color == "red") {
        bluetoothManager.sendData("set-leds-red")
    }
    else if (color == "green") {
        bluetoothManager.sendData("set-leds-green")
    }
    else if (color == "blue") {
        bluetoothManager.sendData("set-leds-blue")
    }
    else if (color == "white") {
        bluetoothManager.sendData("set-leds-white")
    }
    else if (color == "black") {
        bluetoothManager.sendData("set-leds-black")
    }
}