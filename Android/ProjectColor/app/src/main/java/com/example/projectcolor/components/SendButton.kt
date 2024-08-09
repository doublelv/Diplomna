package com.example.projectcolor.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.projectcolor.RGBMatrix
import com.example.projectcolor.bluetooth.BluetoothManager
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun SendButton(
    modifier: Modifier = Modifier,
    matrix: MutableState<RGBMatrix>,
    bluetoothManager: BluetoothManager
) {
    Row(
        modifier = modifier.padding(4.dp),) {
        Button(
            modifier = Modifier
                .width(120.dp)
                .padding(end = 4.dp),
            onClick = {
                logMatrixInfo(matrix)
                var serializedMatrixData = serializeRow(matrix, 0).toHexString()
                Log.d("SendButton", "serializedMatrixData: ${serializedMatrixData}")


                var fullData = addChecksums(serializedMatrixData)
                Log.d("SendButton", "fullData: ${fullData}")
                bluetoothManager.sendData(fullData + "\n")   // \n is a delimeter
            },
        ) {
            Text(text = "Send")
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun addChecksums(data: String): String {

    var fulldata: String = "";

    for (i in 0 until data.length step 8)
        {
            val chunk = data.substring(i, i + 8)
            var checksum = checkSum(hexStringToBinaryString(chunk), 8)
            Log.d("SendButton", "chunk: ${chunk}")
            Log.d("SendButton", "checksum: ${checksum}")
            checksum = checksum.toInt(2).toString(16).padStart(2, '0')
            Log.d("SendButton", "checksum: ${checksum}")
            fulldata = fulldata + chunk + checksum
        }
    return fulldata
}

fun serializeRow(
    matrix: MutableState<RGBMatrix>,
    row: Int = 0,
    ): ByteArray {
    val byteArray = ByteArray(4*16)
    var index = 0
    try {
        for (column in 0 until matrix.value.width) {
            val pixel = matrix.value.getPixel(row, column)
            byteArray[index] = ((row shl 4) + column).toByte()
            byteArray[index+1] = (pixel.red * 255f).toInt().toByte()
            byteArray[index+2] = (pixel.green * 255f).toInt().toByte()
            byteArray[index+3] = (pixel.blue * 255f).toInt().toByte()
//            Log.d("SendButton", "Data($row, $column): ")
//            Log.d("SendButton", "R: byteArray[index+2] ${byteArray[index+1]}")
//            Log.d("SendButton", "G: byteArray[index+3] ${byteArray[index+2]}")
//            Log.d("SendButton", "B: byteArray[index+4] ${byteArray[index+3]}")
            index += 4
        }
    } catch (e: IOException) {
        Log.e("MainActivity", "Error serializing matrix", e)
    }

    return byteArray
}

fun serializeMatrix(
    matrix: MutableState<RGBMatrix>,
    row: Int = 0,
): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val dataOutputStream = DataOutputStream(byteArrayOutputStream)

    try {
        for (row in 0 until matrix.value.height) {
            for (column in 0 until matrix.value.width) {
                val pixel = matrix.value.getPixel(row, column)
                dataOutputStream.writeByte(row)
                dataOutputStream.writeByte(column)
                dataOutputStream.writeByte((pixel.red * 255f).toInt())
                dataOutputStream.writeByte((pixel.green * 255f).toInt())
                dataOutputStream.writeByte((pixel.blue * 255f).toInt())
            }
        }
    } catch (e: IOException) {
        Log.e("MainActivity", "Error serializing matrix", e)
    }

    return byteArrayOutputStream.toByteArray().toString()
}


@SuppressLint("DefaultLocale")
fun logMatrixInfo(matrix: MutableState<RGBMatrix>) {
    for (row in 0 until matrix.value.width) {
        for (column in 0 until matrix.value.height) {
            val pixel = matrix.value.getPixel(row, column)
            val redString = String.format("%03d", (pixel.red * 255f).toInt())
            val greenString = String.format("%03d", (pixel.green * 255f).toInt())
            val blueString = String.format("%03d", (pixel.blue * 255f).toInt())
            val message = "Pixel($row, $column) - R: $redString, G: $greenString, B: $blueString"
            Log.d("PixelGrid", message)
        }
    }
}