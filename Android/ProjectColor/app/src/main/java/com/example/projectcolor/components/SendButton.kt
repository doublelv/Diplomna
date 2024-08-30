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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SendButton is a Composable function that displays a row of buttons for sending pixel grid data and specific color commands
 * via Bluetooth. The buttons are conditionally enabled based on the Bluetooth connection status.
 *
 * **Parameters:**
 *
 * - `modifier`: A `Modifier` applied to the row of buttons. It can be used to customize the appearance and layout of the buttons. The default value is `Modifier`.
 * - `matrix`: A `MutableState<RGBMatrix>` representing the pixel data of the grid. This data is sent to the Bluetooth device when the "Send" button is clicked.
 * - `bluetoothManager`: A `BluetoothManager` instance that manages the Bluetooth connection and handles the transmission of data.
 *
 * **UI Structure:**
 *
 * - The function creates a `Row` composable that contains multiple buttons, each with specific functionality:
 *     - The "Send" button, which occupies more space (`weight` of 2), initiates the process of sending the pixel grid data via Bluetooth when clicked.
 *     - Color buttons (Red, Green, Blue, Black, and White), each sending a corresponding color command to the Bluetooth device when clicked.
 *
 * - Each button's `enabled` state depends on whether a Bluetooth device is connected, as indicated by the `bluetoothManager`.
 *
 * - The `Send` button triggers a coroutine to execute the `handshakeSendPixelQuarterRows` function, which handles the actual data transmission.
 *
 * - The color buttons use `sendColor` to transmit a specific color command to the Bluetooth device.
 *
 * - The buttons are styled using `ButtonDefaults.buttonColors` to reflect their respective colors.
 *
 * This function provides a user interface for interacting with a Bluetooth-connected device, allowing the user to send pixel data or specific color commands.
 */
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
                CoroutineScope(Dispatchers.Main).launch {
                    handshakeSendPixelQaurterRows(matrix, bluetoothManager, context)
                }
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

/**
 * handshakeSendPixelQaurterRows is a function that manages the process of transmitting pixel data in a grid to a Bluetooth-connected device.
 * It handles the handshake protocol, sends the grid data row by row, and then terminates the connection gracefully. This function also provides
 * feedback to the user through log messages and optional Toast notifications.
 *
 * **Parameters:**
 *
 * - `matrix`: A `MutableState<RGBMatrix>` representing the pixel grid data to be sent to the Bluetooth device.
 * - `bluetoothManager`: A `BluetoothManager` instance responsible for managing the Bluetooth connection and data transmission.
 * - `context`: A `Context` used to display Toast messages, providing visual feedback to the user.
 *
 * **Functionality:**
 *
 * - The function first attempts to establish a connection by performing a handshake with the Bluetooth device. It sends a "syn" message and expects a "syn-ack" response.
 *   If successful, it sends an "ack" message to complete the handshake.
 *
 * - If the handshake is successful, the function proceeds to send the pixel grid data row by row. Each row is divided into four parts and sent sequentially.
 *   The function retries sending each part up to 20 times until a "ROW-SUCCESS" acknowledgment is received.
 *
 * - After successfully sending all rows, the function terminates the connection by sending a "fin" message and waiting for a "fin-ack" response.
 *   If the termination is unsuccessful, it retries the process up to three times.
 *
 * - Throughout the process, the function logs each step and can optionally display Toast messages to inform the user of the current status.
 *
 * - If the handshake or data transmission fails, the function displays an appropriate error message to the user.
 */
fun handshakeSendPixelQaurterRows(
    matrix: MutableState<RGBMatrix>,
    bluetoothManager: BluetoothManager,
    context: Context // Add context to show Toast messages
) {
    val retryLimit = 3
    val timeoutMillis = 5000L
    var retryCount = 0

    /**
     * performHandshake is a function that attempts to establish a connection with a Bluetooth device using a handshake protocol.
     * It sends a "syn" message and expects a "syn-ack" response. If the handshake is successful, it sends an "ack" message to complete
     * the process. The function retries the handshake a limited number of times if it fails initially.
     *
     * **Returns:**
     *
     * - `Boolean`: Returns `true` if the handshake is successful, otherwise returns `false`.
     *
     * **Functionality:**
     *
     * - The function sends a "syn" message and waits for a "syn-ack" response from the Bluetooth device.
     * - If the "syn-ack" response is received, the function sends an "ack" message and logs the successful handshake.
     * - If the handshake fails (i.e., "syn-ack" is not received), the function retries up to a predefined limit (`retryLimit`).
     * - The function provides feedback via log messages and can optionally show Toast messages for user information.
     */
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

    /**
     * sendMatrixRows is a function responsible for transmitting the pixel grid data row by row to the Bluetooth device.
     * Each row is divided into four parts, and the function sends these parts sequentially, waiting for an acknowledgment
     * after each part. If an acknowledgment is not received, the function retries sending that part multiple times.
     *
     * **Returns:**
     *
     * - `Boolean`: Returns `true` if all rows are successfully sent, otherwise returns `false`.
     *
     * **Functionality:**
     *
     * - The function iterates over each row of the matrix and divides it into four parts.
     * - For each part, the function sends the data and waits for a "ROW-SUCCESS" acknowledgment.
     * - If the acknowledgment is not received, the function retries sending the part up to 20 times.
     * - The function logs the progress and status of each row and part, providing detailed feedback on the transmission process.
     */
    fun sendMatrixRows(): Boolean {
        for (row in 0 until matrix.value.height) {
            Log.d("SendButton", "Sending row: $row ")
//            Toast.makeText(context, "Sending row: $row", Toast.LENGTH_SHORT).show()

            for (part in 0 until 4) {
                var tryCount = 0
                var rowAck = "ROW-FAIL"

                while (rowAck != "ROW-SUCCESS" && tryCount < 20) {
                    sendQuarterRow(matrix, row, part, bluetoothManager, "data:")
                    rowAck = bluetoothManager.receiveData(timeoutMillis).toString()
                    tryCount++
                    Thread.sleep(5) // for testing
                }

                if(rowAck != "ROW-SUCCESS") {
                    Log.d("SendButton", "Failed to send row $row, part: $part, received: $rowAck")
                    return false
                }
            }
        }
        return true
    }

    /**
     * terminateConnection is a function that gracefully terminates the connection with the Bluetooth device by following a termination protocol.
     * It sends a "fin" message and expects a "fin-ack" response from the device. If the termination is unsuccessful, the function retries
     * the process up to a predefined limit.
     *
     * **Functionality:**
     *
     * - The function sends a "fin" message to the Bluetooth device and waits for a "fin-ack" response.
     * - If the "fin-ack" response is received, the function logs the successful termination of the connection.
     * - If the termination fails (i.e., "fin-ack" is not received), the function retries the termination process up to a predefined limit (`retryLimit`).
     * - The function provides feedback via log messages and can optionally show Toast messages to inform the user about the connection status.
     */
    fun terminateConnection() {
        retryCount = 0
        val response = ""
        while (retryCount < retryLimit && bluetoothManager.isConnected() && response != "fin-ack") {
            bluetoothManager.sendData("fin")
            Log.d("SendButton", "FIN sent, waiting for FIN-ACK...")
//        Toast.makeText(context, "FIN sent, waiting for FIN-ACK...", Toast.LENGTH_SHORT).show()

            val response = bluetoothManager.receiveData(timeoutMillis)
            if (response == "fin-ack") {
                Log.d("SendButton", "FIN-ACK received, connection terminated.")
                Toast.makeText(context, "Data sent successfully and connection terminated.", Toast.LENGTH_SHORT).show()
                break
            } else {
                Log.d("SendButton", "Failed to terminate connection: FIN-ACK not received.")
            }
            retryCount++
            Thread.sleep(100)
        }
        if (retryCount == retryLimit) {
            Log.d("SendButton", "Failed to terminate connection after $retryLimit attempts.")
            Toast.makeText(context, "Failed to terminate connection after $retryLimit attempts.", Toast.LENGTH_SHORT).show()
        }
    }

    if (performHandshake() && sendMatrixRows()) {
        terminateConnection()
    } else {
        Toast.makeText(context, "Failed to send matrix data.", Toast.LENGTH_LONG).show()
    }
}

/**
 * sendColor is a function that sends a specific color command to a Bluetooth-connected device. The function maps color names to corresponding
 * commands and transmits them to control the color of an external device, such as an LED array.
 *
 * **Parameters:**
 *
 * - `color`: A `String` representing the color to be sent. Valid values are "red", "green", "blue", "white", and "black".
 * - `bluetoothManager`: A `BluetoothManager` instance responsible for managing the Bluetooth connection and sending the color command.
 *
 * **Functionality:**
 *
 * - The function checks the value of the `color` parameter and sends the corresponding command to the Bluetooth device.
 *   - For "red", it sends "set-leds-red".
 *   - For "green", it sends "set-leds-green".
 *   - For "blue", it sends "set-leds-blue".
 *   - For "white", it sends "set-leds-white".
 *   - For "black", it sends "set-leds-black".
 *
 * - This function is typically used to control the color of an LED display or similar device via Bluetooth.
 */
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

//fun handshakeSendSinglePixelsOneByOne(
//    matrix: MutableState<RGBMatrix>,
//    bluetoothManager: BluetoothManager,
//    context: Context // Add context to show Toast messages
//) {
//    val retryLimit = 3
//    val timeoutMillis = 5000L
//    var retryCount = 0
//
//    fun performHandshake(): Boolean {
//        while (retryCount < retryLimit && bluetoothManager.isConnected()) {
//            bluetoothManager.sendData("syn")
//            Log.d("SendButton", "SYN sent, waiting for SYN-ACK...")
////            Toast.makeText(context, "SYN sent, waiting for SYN-ACK...", Toast.LENGTH_SHORT).show()
//
//            val response = bluetoothManager.receiveData(timeoutMillis)
//            if (response == "syn-ack") {
//                bluetoothManager.sendData("ack")
//                Log.d("SendButton", "ACK sent. Handshake successful.")
//                Toast.makeText(context, "ACK sent. Handshake successful.", Toast.LENGTH_SHORT).show()
//                return true
//            } else {
//                retryCount++
//                Log.d("SendButton", "SYN-ACK not received, retrying... ($retryCount/$retryLimit)")
////                Toast.makeText(context, "SYN-ACK not received, retrying... ($retryCount/$retryLimit)", Toast.LENGTH_SHORT).show()
//                Thread.sleep(1000)
//            }
//        }
//        Log.d("SendButton", "Failed to establish connection after $retryLimit attempts.")
//        Toast.makeText(context, "Failed to establish connection after $retryLimit attempts.", Toast.LENGTH_LONG).show()
//        return false
//    }
//
//    fun sendMatrixPixelByPixel(): Boolean {
//        for (row in 0 until 1 * matrix.value.height) {
//            for (column in 0 until matrix.value.width) {
//                var tryCount = 0
//                val pixel = matrix.value.getPixel(row, column)
//                var pixelAck = "PIXEL-FAIL"
//
//                while (pixelAck != "PIXEL-SUCCESS" && tryCount < 20) {
//                    sendPixel(pixel, row, column, bluetoothManager, "data:")
//                    pixelAck = bluetoothManager.receiveData(timeoutMillis).toString()
//                    tryCount++
////                    Thread.sleep(1)
//                }
//
//                if(pixelAck != "PIXEL-SUCCESS") {
//                    Log.d("SendButton", "Failed to send pixel: $pixel, received: $pixelAck")
//                    return false
//                }
//            }
////            Toast.makeText(context, "Sent row $row", Toast.LENGTH_SHORT).show()
//        }
//        return true
//    }
//
//    fun terminateConnection() {
//        bluetoothManager.sendData("fin")
//        Log.d("SendButton", "FIN sent, waiting for FIN-ACK...")
////        Toast.makeText(context, "FIN sent, waiting for FIN-ACK...", Toast.LENGTH_SHORT).show()
//
//        val finAck = bluetoothManager.receiveData(timeoutMillis)
//        if (finAck == "fin-ack") {
//            Log.d("SendButton", "FIN-ACK received, connection terminated.")
//            Toast.makeText(context, "Data sent successfully and connection terminated.", Toast.LENGTH_LONG).show()
//        } else {
//            Log.d("SendButton", "Failed to terminate connection: FIN-ACK not received.")
//            Toast.makeText(context, "Failed to terminate connection.", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    if (performHandshake() && sendMatrixPixelByPixel()) {
//        terminateConnection()
//    } else {
//        Toast.makeText(context, "Failed to send matrix data.", Toast.LENGTH_LONG).show()
//    }
//}
//
//fun handshakeSendSinglePixelRows(
//    matrix: MutableState<RGBMatrix>,
//    bluetoothManager: BluetoothManager,
//    context: Context // Add context to show Toast messages
//) {
//    val retryLimit = 3
//    val timeoutMillis = 5000L
//    var retryCount = 0
//
//    fun performHandshake(): Boolean {
//        while (retryCount < retryLimit && bluetoothManager.isConnected()) {
//            bluetoothManager.sendData("syn")
//            Log.d("SendButton", "SYN sent, waiting for SYN-ACK...")
////            Toast.makeText(context, "SYN sent, waiting for SYN-ACK...", Toast.LENGTH_SHORT).show()
//
//            val response = bluetoothManager.receiveData(timeoutMillis)
//            if (response == "syn-ack") {
//                bluetoothManager.sendData("ack")
//                Log.d("SendButton", "ACK sent. Handshake successful.")
//                Toast.makeText(context, "ACK sent. Handshake successful.", Toast.LENGTH_SHORT).show()
//                return true
//            } else {
//                retryCount++
//                Log.d("SendButton", "SYN-ACK not received, retrying... ($retryCount/$retryLimit)")
////                Toast.makeText(context, "SYN-ACK not received, retrying... ($retryCount/$retryLimit)", Toast.LENGTH_SHORT).show()
//                Thread.sleep(1000)
//            }
//        }
//        Log.d("SendButton", "Failed to establish connection after $retryLimit attempts.")
//        Toast.makeText(context, "Failed to establish connection after $retryLimit attempts.", Toast.LENGTH_LONG).show()
//        return false
//    }
//
//    fun sendMatrixPixelRows(): Boolean {
//        for (row in 0 until 1 * matrix.value.height) {
//            var tryCount = 0
//            var rowAck = "ROW-FAIL"
//
//            while (rowAck != "ROW-SUCCESS" && tryCount < 20) {
//                sendRow(matrix, row, bluetoothManager, "data:")
//                rowAck = bluetoothManager.receiveData(timeoutMillis).toString()
//                tryCount++
//        //                    Thread.sleep(1)
//            }
//            if(rowAck != "PIXEL-SUCCESS") {
//                Log.d("SendButton", "Failed to send row: $row, received: $rowAck")
//                return false
//            }
////            Toast.makeText(context, "Sent row $row", Toast.LENGTH_SHORT).show()
//        }
//        return true
//    }
//
//    fun terminateConnection() {
//        bluetoothManager.sendData("fin")
//        Log.d("SendButton", "FIN sent, waiting for FIN-ACK...")
////        Toast.makeText(context, "FIN sent, waiting for FIN-ACK...", Toast.LENGTH_SHORT).show()
//
//        val finAck = bluetoothManager.receiveData(timeoutMillis)
//        if (finAck == "fin-ack") {
//            Log.d("SendButton", "FIN-ACK received, connection terminated.")
//            Toast.makeText(context, "Data sent successfully and connection terminated.", Toast.LENGTH_LONG).show()
//        } else {
//            Log.d("SendButton", "Failed to terminate connection: FIN-ACK not received.")
//            Toast.makeText(context, "Failed to terminate connection.", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    if (performHandshake() && sendMatrixPixelRows()) {
//        terminateConnection()
//    } else {
//        Toast.makeText(context, "Failed to send matrix data.", Toast.LENGTH_LONG).show()
//    }
//}
//
//
//
//fun handshakeSendPixelHalfRows(
//    matrix: MutableState<RGBMatrix>,
//    bluetoothManager: BluetoothManager,
//    context: Context // Add context to show Toast messages
//) {
//    val retryLimit = 3
//    val timeoutMillis = 5000L
//    var retryCount = 0
//
//    fun performHandshake(): Boolean {
//        while (retryCount < retryLimit && bluetoothManager.isConnected()) {
//            bluetoothManager.sendData("syn")
//            Log.d("SendButton", "SYN sent, waiting for SYN-ACK...")
////            Toast.makeText(context, "SYN sent, waiting for SYN-ACK...", Toast.LENGTH_SHORT).show()
//
//            val response = bluetoothManager.receiveData(timeoutMillis)
//            if (response == "syn-ack") {
//                bluetoothManager.sendData("ack")
//                Log.d("SendButton", "ACK sent. Handshake successful.")
//                Toast.makeText(context, "ACK sent. Handshake successful.", Toast.LENGTH_SHORT).show()
//                return true
//            } else {
//                retryCount++
//                Log.d("SendButton", "SYN-ACK not received, retrying... ($retryCount/$retryLimit)")
////                Toast.makeText(context, "SYN-ACK not received, retrying... ($retryCount/$retryLimit)", Toast.LENGTH_SHORT).show()
//                Thread.sleep(1000)
//            }
//        }
//        Log.d("SendButton", "Failed to establish connection after $retryLimit attempts.")
//        Toast.makeText(context, "Failed to establish connection after $retryLimit attempts.", Toast.LENGTH_LONG).show()
//        return false
//    }
//
//    fun sendMatrixRows(): Boolean {
//        for (row in 0 until matrix.value.height) {
//            Log.d("SendButton", "Sending row: $row ")
//            Toast.makeText(context, "Sending row: $row", Toast.LENGTH_SHORT).show()
//
//            for (part in 0 until 2) {
//                var tryCount = 0
//                var rowAck = "ROW-FAIL"
//
//                while (rowAck != "ROW-SUCCESS" && tryCount < 5) {
//                    sendHalfRow(matrix, row, part, bluetoothManager, "data:")
//                    rowAck = bluetoothManager.receiveData(timeoutMillis).toString()
//                    tryCount++
//                    Thread.sleep(3000)
//                }
//
//                if(rowAck != "ROW-SUCCESS") {
//                    Log.d("SendButton", "Failed to send row $row, part: $part, received: $rowAck")
//                    return false
//                }
//            }
//        }
//        return true
//    }
//
//    fun terminateConnection() {
//        bluetoothManager.sendData("fin")
//        Log.d("SendButton", "FIN sent, waiting for FIN-ACK...")
////        Toast.makeText(context, "FIN sent, waiting for FIN-ACK...", Toast.LENGTH_SHORT).show()
//
//        val finAck = bluetoothManager.receiveData(timeoutMillis)
//        if (finAck == "fin-ack") {
//            Log.d("SendButton", "FIN-ACK received, connection terminated.")
//            Toast.makeText(context, "Data sent successfully and connection terminated.", Toast.LENGTH_LONG).show()
//        } else {
//            Log.d("SendButton", "Failed to terminate connection: FIN-ACK not received.")
//            Toast.makeText(context, "Failed to terminate connection.", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    if (performHandshake() && sendMatrixRows()) {
//        terminateConnection()
//    } else {
//        Toast.makeText(context, "Failed to send matrix data.", Toast.LENGTH_LONG).show()
//    }
//}


