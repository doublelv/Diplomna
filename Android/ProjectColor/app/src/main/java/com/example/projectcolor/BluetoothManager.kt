//package com.example.projectcolor
//
//import android.Manifest
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import androidx.core.app.ActivityCompat
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothManager
//import android.content.Context
//
//class BluetoothManager(private val context: Context) {
//
//    private val bluetoothAdapter: BluetoothAdapter? by lazy {
//        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        bluetoothManager.adapter
//    }
//
//    fun isBluetoothEnabled(): Boolean {
//        return bluetoothAdapter?.isEnabled ?: false
//    }
//
//    fun enableBluetooth() {
//        bluetoothAdapter?.let {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//        }
//    }
//
//    // ... other functions for discovering devices, creating server, connecting, sending/receiving data, etc.
//}
