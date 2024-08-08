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
//                val data = serializeMatrix(matrix)
                var serializedMatrixData = serializeRow(matrix, 0).toHexString()
                Log.d("SendButton", "RAW data: ${serializedMatrixData}")
                serializedMatrixData = addDelimeters(serializedMatrixData)
                Log.d("SendButton", "Sending delimetered data: ${serializedMatrixData}")
                bluetoothManager.sendData(serializedMatrixData + "\n")   // \n is a delimeter
            },
        ) {
            Text(text = "Send")
        }
    }
}

fun addDelimeters(serialData: String): String {
    val endOfArray = "\n"
    val endOfPixelInfo = ","
    val endOfByteInfo = "."

    var newSerialData = ""

    for (index in 1 until serialData.length + 1) {
        newSerialData += serialData[index-1]

        if ((index) % 10 == 0) {
            newSerialData += endOfPixelInfo
        }
//
//        else if (index % 2 == 0) {
//            newSerialData += endOfByteInfo
//        }


    }
    return newSerialData + endOfArray
}

fun serializeRow(
    matrix: MutableState<RGBMatrix>,
    row: Int = 0,
    ): ByteArray {
    val byteArray = ByteArray(5*16 + 1)
    var index = 0
    try {
        for (column in 0 until matrix.value.width) {

            byteArray[index] = row.toByte()
            byteArray[index+1] = column.toByte()
            byteArray[index+2] = (255).toByte()
            byteArray[index+3] = (255).toByte()
            byteArray[index+4] = (255).toByte()
            Log.d("SendButton", "Data($row, $column): ")
            Log.d("SendButton", "R: byteArray[index+2] ${byteArray[index+2]}")
            Log.d("SendButton", "G: byteArray[index+3] ${byteArray[index+3]}")
            Log.d("SendButton", "B: byteArray[index+4] ${byteArray[index+4]}")
            index += 5
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