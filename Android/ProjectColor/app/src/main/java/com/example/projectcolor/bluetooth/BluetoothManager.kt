package com.example.projectcolor.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@SuppressLint("MissingPermission")
class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _discoveredDevices = MutableLiveData<Set<BluetoothDevice>>(emptySet())
    val discoveredDevices: LiveData<Set<BluetoothDevice>> = _discoveredDevices
    private val discoveredDevicesSet = mutableSetOf<BluetoothDevice>()

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    discoveredDevicesSet.add(it)
                    _discoveredDevices.postValue(discoveredDevicesSet.toSet())  // Notify observers
                }
            }
        }
    }

    fun hasBluetoothSupport(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    fun enableBluetooth(launcher: ActivityResultLauncher<Intent>) {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcher.launch(enableBtIntent)
        }
    }

    fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                requestCode
            )
        } else {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                requestCode
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): Set<BluetoothDevice> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermissions()) {
            throw SecurityException("BLUETOOTH_CONNECT permission required")
        }
        return bluetoothAdapter?.bondedDevices ?: emptySet()
    }

    fun startDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermissions()) {
            throw SecurityException("BLUETOOTH_SCAN permission required")
        }
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            discoveredDevicesSet.clear()
            _discoveredDevices.postValue(emptySet())  // Notify observers
            context.registerReceiver(discoveryReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            adapter.startDiscovery()

            // Stop discovery after a certain period
            Handler(Looper.getMainLooper()).postDelayed({
                stopDiscovery()
            }, 30000)  // Stops after 30 seconds
        }
    }

    private fun stopDiscovery() {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            context.unregisterReceiver(discoveryReceiver)
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        // Implement the connection logic here
    }
}
