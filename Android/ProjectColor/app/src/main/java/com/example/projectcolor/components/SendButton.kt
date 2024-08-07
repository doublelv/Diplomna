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


@Composable
fun SendButton(
    modifier: Modifier = Modifier,
    matrix: MutableState<RGBMatrix>,
    bluetoothManager: BluetoothManager
) {
    Row(
        modifier = modifier.padding(4.dp),) {
        Button(
            modifier = Modifier.width(120.dp).padding(end = 4.dp),
            onClick = {
                logMatrixInfo(matrix)
                val data = serializeMatrix(matrix)
//                bluetoothManager.sendData("Hello\n")
                bluetoothManager.sendData(data.toString())
            },
        ) {
            Text(text = "Send")
        }
    }
}
@SuppressLint("DefaultLocale")
fun serializeMatrix(matrix: MutableState<RGBMatrix>): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val dataOutputStream = DataOutputStream(byteArrayOutputStream)

    try {
        for (row in 0 until matrix.value.height) {
            for (column in 0 until matrix.value.width) {
                val pixel = matrix.value.getPixel(row, column)
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