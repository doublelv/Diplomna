package com.example.projectcolor.components

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.projectcolor.RGBMatrix
import com.example.projectcolor.bluetooth.BluetoothManager
import java.io.IOException

/**
 * addChecksumToRow is a function that appends a checksum to a given hexadecimal string. The checksum is calculated from the binary
 * representation of the input data to ensure data integrity during transmission.
 *
 * **Parameters:**
 *
 * - `data`: A `String` representing the hexadecimal data to which the checksum will be added.
 *
 * **Returns:**
 *
 * - `String`: Returns the original hexadecimal string with the checksum appended to the end.
 *
 * **Functionality:**
 *
 * - The function converts the input hexadecimal string to its binary representation using `hexStringToBinaryString`.
 * - It calculates an 8-bit checksum from the binary data using the `checkSum` function.
 * - The checksum is then converted from binary to a 2-character hexadecimal string, ensuring it is padded with leading zeros if necessary.
 * - The original data string is concatenated with the checksum, and the combined string is returned.
 *
 * - This function is typically used to add a checksum to data being prepared for transmission, allowing the receiver to verify the integrity of the received data.
 */
fun addChecksumToRow(data: String): String{
    var checksum = checkSum(hexStringToBinaryString(data), 8)
    checksum = checksum.toInt(2).toString(16).padStart(2, '0')
    return (data + checksum)
}

/**
 * sendQuarterRow is a function that serializes a quarter of a specific row from a pixel grid and sends it to a Bluetooth-connected device.
 * The function divides the row into quarters, serializes the selected quarter, and transmits it as a single message.
 *
 * **Parameters:**
 *
 * - `matrix`: A `MutableState<RGBMatrix>` representing the pixel grid data to be sent.
 * - `row`: An `Int` representing the index of the row to be sent.
 * - `part`: An `Int` indicating which quarter of the row to send. The value should be between 0 and 3, where each value corresponds to a specific quarter of the row.
 * - `bluetoothManager`: A `BluetoothManager` instance responsible for managing the Bluetooth connection and transmitting the serialized data.
 * - `addition`: A `String` that can be prepended to the serialized quarter-row message before transmission. This parameter is optional and defaults to an empty string.
 *
 * **Functionality:**
 *
 * - The function serializes the specified quarter of the row using the `serializeQuarterRow` function.
 * - It concatenates the `addition` string with the serialized quarter-row data to form the full message.
 * - The full message is then sent to the Bluetooth device using the `bluetoothManager.sendData` function.
 * - The function logs the row number, the quarter number, and the full message for debugging purposes.
 */
fun sendQuarterRow(
    matrix: MutableState<RGBMatrix>,
    row: Int,
    part: Int, // 0(first half) or 1(second half)
    bluetoothManager: BluetoothManager,
    addition: String = ""
) {
    val serializedQuarterRow = serializeQuarterRow(matrix, row, part)
//    Log.d("SendButton", "serializedRow: \n$serializedHalfRow")

    // Send the entire row as one message
    val fullMessage = addition + serializedQuarterRow
    bluetoothManager.sendData(fullMessage)
    Log.d("SendButton", "Row $row, part $part message:\n$fullMessage")
}

/**
 * serializeQuarterRow is a function that serializes a specific quarter of a row from a pixel grid into a hexadecimal string representation.
 * This serialized data is formatted to include pixel color information and is typically used for transmission to a Bluetooth device.
 *
 * **Parameters:**
 *
 * - `matrix`: A `MutableState<RGBMatrix>` representing the pixel grid data to be serialized.
 * - `row`: An `Int` representing the index of the row to be serialized. The default value is `0`.
 * - `part`: An `Int` indicating which quarter of the row to serialize. The default value is `0`, and it should be between 0 and 3, where each value corresponds to a specific quarter of the row.
 *
 * **Returns:**
 *
 * - `String`: Returns a hexadecimal string that represents the serialized quarter-row data, including pixel color information and a checksum.
 *
 * **Functionality:**
 *
 * - The function creates a `ByteArray` of size 16, where each set of four bytes represents one pixel in the quarter of the row being serialized.
 * - For each pixel in the specified quarter, the function calculates the pixel's position in the grid, and converts the red, green, and blue color values into bytes, storing them in the `ByteArray`.
 * - The function catches any `IOException` that may occur during serialization and logs an error message.
 * - The `ByteArray` is then converted to a hexadecimal string using the `toHexString` extension function.
 * - A checksum is added to the serialized string using the `addChecksumToRow` function to ensure data integrity.
 *
 * - The resulting string, which includes the pixel data and checksum, is returned for further use, typically for transmission to an external device.
 */
@OptIn(ExperimentalStdlibApi::class)
fun serializeQuarterRow(
    matrix: MutableState<RGBMatrix>,
    row: Int = 0,
    part: Int = 0,
): String {

    val byteArray = ByteArray(4*4)
    var index = 0
    try {
        for (column in part * matrix.value.width / 4 until (part * matrix.value.width / 4) + matrix.value.width / 4) {
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
    byteArrayString = addChecksumToRow(byteArrayString) //+ "\n"

    return byteArrayString
}

// Adds checksum to a row of pixel data
//fun addChecksumToPixelData(data: String): String{
//    var checksum = checkSum(hexStringToBinaryString(data), 4)
//    checksum = checksum.toInt(2).toString(16)//.padStart(2, '0')
//    return (data + checksum)
//}

//@OptIn(ExperimentalStdlibApi::class)
//fun serializePixel(
//    pixel: PixelData,
//    row: Int,
//    column: Int,
//): String {
//    val byteArray = ByteArray(4)
//
//    try {
//        byteArray[0] = ((row shl 4) + column).toByte()
//        byteArray[1] = (pixel.red * 255f).toInt().toByte()
//        byteArray[2] = (pixel.green * 255f).toInt().toByte()
//        byteArray[3] = (pixel.blue * 255f).toInt().toByte()
//    } catch (e: IOException) {
//        Log.e("MainActivity", "Error serializing pixel", e)
//    }
//
//    var byteArrayString = byteArray.toHexString()
//    byteArrayString = addChecksumToPixelData(byteArrayString)
//    return byteArrayString
//}

//// Serializes a full row of pixel data into a byte array
//@OptIn(ExperimentalStdlibApi::class)
//fun serializeRow(
//    matrix: MutableState<RGBMatrix>,
//    row: Int = 0,
//): String {
//
//    val byteArray = ByteArray(4*16)
//    var index = 0
//    try {
//        for (column in 0 until matrix.value.width) {
//            val pixel = matrix.value.getPixel(row, column)
//            byteArray[index] = ((row shl 4) + column).toByte()
//            byteArray[index+1] = (pixel.red * 255f).toInt().toByte()
//            byteArray[index+2] = (pixel.green * 255f).toInt().toByte()
//            byteArray[index+3] = (pixel.blue * 255f).toInt().toByte()
//            index += 4
//        }
//    } catch (e: IOException) {
//        Log.e("MainActivity", "Error serializing matrix", e)
//    }
//
//    var byteArrayString = byteArray.toHexString()
//    byteArrayString = addChecksumToRow(byteArrayString) + "\n"
//
//    return byteArrayString
//}

//@OptIn(ExperimentalStdlibApi::class)
//fun serializeHalfRow(
//    matrix: MutableState<RGBMatrix>,
//    row: Int = 0,
//    part: Int = 0,
//): String {
//
//    val byteArray = ByteArray(4*8)
//    var index = 0
//    try {
//        for (column in part * matrix.value.width / 2 until (part * matrix.value.width / 2) + matrix.value.width / 2) {
//            val pixel = matrix.value.getPixel(row, column)
//            byteArray[index] = ((row shl 4) + column).toByte()
//            byteArray[index+1] = (pixel.red * 255f).toInt().toByte()
//            byteArray[index+2] = (pixel.green * 255f).toInt().toByte()
//            byteArray[index+3] = (pixel.blue * 255f).toInt().toByte()
//            index += 4
//        }
//    } catch (e: IOException) {
//        Log.e("MainActivity", "Error serializing matrix", e)
//    }
//
//    var byteArrayString = byteArray.toHexString()
//    byteArrayString = addChecksumToRow(byteArrayString) //+ "\n"
//
//    return byteArrayString
//}


//
//// Serializes the full matrix of pixel data into a byte array
//fun serializeMatrix(matrix: MutableState<RGBMatrix>): String {
//    var outputString = ""
//    try {
//        for (row in 0 until matrix.value.height) {
//            val serializedRow = serializeRow(matrix, row)
//            outputString += serializedRow + "\n"
//        }
//    } catch (e: IOException) {
//        Log.e("MainActivity", "Error serializing matrix", e)
//    }
//    return outputString
//}

//fun sendPixel(
//    pixel: PixelData,
//    row: Int = 0,
//    column: Int = 0,
//    bluetoothManager: BluetoothManager,
//    addition: String = ""
//) {
//    val serializedPixel = serializePixel(pixel, row, column)
//    val fullMessage = addition + serializedPixel
//    bluetoothManager.sendData(fullMessage)
//    Log.d("SendButton", "Pixel sent: $fullMessage")
//}

//
//fun sendHalfRow(
//    matrix: MutableState<RGBMatrix>,
//    row: Int,
//    part: Int, // 0(first half) or 1(second half)
//    bluetoothManager: BluetoothManager,
//    addition: String = ""
//) {
//    val serializedHalfRow = serializeHalfRow(matrix, row, part)
////    Log.d("SendButton", "serializedRow: \n$serializedHalfRow")
//
//    // Send the entire row as one message
//    val fullMessage = addition + serializedHalfRow
//    bluetoothManager.sendData(fullMessage)
//    Log.d("SendButton", "Half row $part sent:\n$fullMessage")
//}
//
//fun sendRow(
//    matrix: MutableState<RGBMatrix>,
//    row: Int,
//    bluetoothManager: BluetoothManager,
//    addition: String = ""
//) {
//    val serializedRow = serializeRow(matrix, row)
//    Log.d("SendButton", "serializedRow: \n$serializedRow")
//
//    // Send the entire row as one message
//    val fullMessage = addition + serializedRow
//    bluetoothManager.sendData(fullMessage)
//    Log.d("SendButton", "Full row sent: $fullMessage")
//}
//fun sendFullMatrixData(matrix: MutableState<RGBMatrix>, bluetoothManager: BluetoothManager) {
//    for (row in 0 until matrix.value.height) {
//        sendRow(matrix, row, bluetoothManager)
//        Thread.sleep(500)
//    }
//}