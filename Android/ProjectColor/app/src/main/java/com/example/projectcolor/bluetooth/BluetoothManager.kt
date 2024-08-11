package com.example.projectcolor.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _discoveredDevices = MutableLiveData<Set<BluetoothDevice>>(emptySet())
    val discoveredDevices: LiveData<Set<BluetoothDevice>> = _discoveredDevices
    private val discoveredDevicesSet = mutableSetOf<BluetoothDevice>()
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID
    private var connectJob: Job? = null
    private var sendJob: Job? = null
    private val connectionTimeout = 10_000L // Timeout in milliseconds
    private val sendInterval = 1_000L // Interval to send data (1 second)
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null


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
            }, 10000)  // Stops after 10 seconds
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

    @SuppressLint("MissingPermission")
    fun pairDevice(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED) {
            device.createBond()
        }
    }

    fun connectToDevice(device: BluetoothDevice, onConnectionResult: (Boolean) -> Unit) {
        connectJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create a Bluetooth socket to the device
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothSocket?.connect()

                // Get the output stream
                outputStream = bluetoothSocket?.outputStream

                // Get the input stream
                inputStream = bluetoothSocket?.inputStream

                // Notify success
                withContext(Dispatchers.Main) {
                    onConnectionResult(true)
                }

                // Start sending data
//                startSendingData()
            } catch (e: IOException) {
                // Notify failure
                withContext(Dispatchers.Main) {
                    onConnectionResult(false)
                }
                // Log error
                Log.e("BluetoothManager", "Connection failed: ${e.message}", e)
            } finally {
                if (bluetoothSocket?.isConnected == false) {
                    try {
                        bluetoothSocket?.close()
                    } catch (e: IOException) {
                        Log.e("BluetoothManager", "Error closing socket", e)
                    } finally {
                        bluetoothSocket = null
                    }
                }
            }
        }

        // Cancel the job if it takes too long
        connectJob?.let { job ->
            CoroutineScope(Dispatchers.IO).launch {
                delay(connectionTimeout)
                if (job.isActive) {
                    job.cancel()
                    withContext(Dispatchers.Main) {
                        onConnectionResult(false)
                    }
                }
            }
        }
    }

    private fun startSendingData() {
        sendJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(sendInterval)
//                sendData("hello\n")
            }
        }
    }

    fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    fun sendData(message: String) {
        if (bluetoothSocket == null || bluetoothSocket?.isConnected == false) {
            Log.e("BluetoothManager", "Cannot send data: socket is not connected")
            return
        }

        try {
            Log.d("BluetoothManager", "Sending data: $message")
            bluetoothSocket?.outputStream?.write((message + "\n").toByteArray())  // Adding newline to delimit messages
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Failed to send data: ${e.message}", e)
            // Optionally, handle socket reinitialization or reconnection here
        }
    }

    fun receiveData(timeoutMillis: Long = 5000L): String? {
        if (bluetoothSocket == null || bluetoothSocket?.isConnected == false) {
            Log.e("BluetoothManager", "Cannot receive data: socket is not connected")
            return null
        }

        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            val available = bluetoothSocket?.inputStream?.available() ?: 0
            if (available > 0) {
                val buffer = ByteArray(available)
                bluetoothSocket?.inputStream?.read(buffer)
                val response = String(buffer).trim()  // Trim whitespace and newlines
                Log.d("BluetoothManager", "Received data: $response")
                return response
            }
            Thread.sleep(100)  // Small delay to avoid busy-waiting
        }
        Log.d("BluetoothManager", "Timeout waiting for response")
        return null
    }


    fun cancelConnection() {
        try {
            connectJob?.cancel()
            sendJob?.cancel()
            connectJob = null
            sendJob = null
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.e("BluetoothManager", "Error closing socket or stream: ${e.message}", e)
        } finally {
            outputStream = null
            bluetoothSocket = null
        }
    }
}
