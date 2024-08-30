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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * BluetoothManager is a class that provides comprehensive management of Bluetooth operations, including discovery,
 * connection, data transmission, and reception. It is designed to simplify Bluetooth interactions in Android
 * applications by abstracting complex tasks into easy-to-use methods.
 *
 * **Constructor Parameters:**
 * - `context`: The Android `Context` used for accessing system services and resources.
 *
 * **Fields:**
 * - `bluetoothAdapter`: The system's Bluetooth adapter, which provides the core Bluetooth functionality.
 * - `_discoveredDevices`: A `MutableLiveData` holding a set of discovered Bluetooth devices. This is observed by the
 *   UI to display available devices.
 * - `discoveredDevices`: A `LiveData` that exposes the discovered devices to observers.
 * - `discoveredDevicesSet`: A mutable set of `BluetoothDevice` objects used internally to keep track of discovered devices.
 * - `myUUID`: A unique identifier for creating RFCOMM Bluetooth sockets.
 * - `connectJob`, `sendJob`: `Coroutine` jobs managing connection and data transmission, respectively.
 * - `connectionTimeout`: The timeout period for establishing a Bluetooth connection.
 * - `bluetoothSocket`: The `BluetoothSocket` used for communication with a connected device.
 * - `outputStream`, `inputStream`: Streams for sending and receiving data through the Bluetooth socket.
 *
 * **Companion Object:**
 * - `TAG`: A constant used for logging.
 *
 * **Private Inner Classes:**
 * - `discoveryReceiver`: A `BroadcastReceiver` that handles device discovery results by adding found devices
 *   to the `discoveredDevicesSet`.
 *
 * This class handles the complexities of Bluetooth interactions, providing an easy-to-use interface for
 * connecting to, communicating with, and managing Bluetooth devices in an Android application.
 */
@SuppressLint("MissingPermission")
class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _discoveredDevices = MutableLiveData<Set<BluetoothDevice>>(emptySet())
    val discoveredDevices: LiveData<Set<BluetoothDevice>> = _discoveredDevices
    private val discoveredDevicesSet = mutableSetOf<BluetoothDevice>()
    private val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID
    private var connectJob: Job? = null
    private var sendJob: Job? = null
    private val connectionTimeout = 10_000L // Timeout in milliseconds
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    companion object {
        private const val TAG = "BluetoothManager"
    }

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

    /**
    ** Methods: **
    */

    /** `hasBluetoothSupport()`: Checks if the device supports Bluetooth. */
    fun hasBluetoothSupport(): Boolean {
        return bluetoothAdapter != null
    }

    /** `isBluetoothEnabled()`: Checks if Bluetooth is currently enabled on the device. */
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    /** `enableBluetooth(launcher: ActivityResultLauncher<Intent>)`: Launches an intent to enable Bluetooth if it
     *   is currently disabled. */
    fun enableBluetooth(launcher: ActivityResultLauncher<Intent>) {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcher.launch(enableBtIntent)
        }
    }

    /** `hasPermissions()`: Verifies if the necessary Bluetooth permissions are granted, taking into account the
     *   Android version. */
    fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        }
    }


    /** `requestPermissions(activity: Activity, requestCode: Int)`: Requests the necessary Bluetooth permissions
     *   from the user, using the appropriate permissions for the Android version. */
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


    /** `getPairedDevices()`: Returns a set of paired Bluetooth devices. Throws a `SecurityException` if the required
     *   permissions are not granted. */
    @SuppressLint("MissingPermission")
    fun getPairedDevices(): Set<BluetoothDevice> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasPermissions()) {
            throw SecurityException("BLUETOOTH_CONNECT permission required")
        }
        return bluetoothAdapter?.bondedDevices ?: emptySet()
    }

    /** `startDiscovery()`: Initiates the Bluetooth device discovery process, clearing previous results and updating
     *   the observed devices. Stops discovery automatically after 10 seconds. */
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


    /** `stopDiscovery()`: Stops the ongoing Bluetooth device discovery process and unregisters the discovery
     *   receiver. */
    private fun stopDiscovery() {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            try {
                context.unregisterReceiver(discoveryReceiver)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Receiver not registered or already unregistered", e)
            }
        }
    }

    /** `pairDevice(device: BluetoothDevice)`: Initiates the pairing process with a given Bluetooth device if it is
     *   not already bonded. */
    @SuppressLint("MissingPermission")
    fun pairDevice(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED) {
            device.createBond()
        }
    }


    /** `connectToDevice(device: BluetoothDevice, onConnectionResult: (Boolean) -> Unit)`: Attempts to connect to a
     *   specified Bluetooth device. The result of the connection attempt is provided through a callback. */
    fun connectToDevice(device: BluetoothDevice, onConnectionResult: (Boolean) -> Unit) {
        connectJob?.cancel() // Cancel previous job if any

        connectJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID).apply {
                    connect()
                }

                outputStream = bluetoothSocket?.outputStream
                inputStream = bluetoothSocket?.inputStream

                withContext(Dispatchers.Main) {
                    onConnectionResult(true)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Connection failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onConnectionResult(false)
                }
            } finally {
                if (bluetoothSocket?.isConnected == false) {
                    bluetoothSocket?.closeSilently()
                    bluetoothSocket = null
                }
            }
        }

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

    /** `isConnected()`: Returns `true` if a Bluetooth connection is currently established. */
    fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected ?: false
    }

    /** `sendData(message: String)`: Sends a string message to the connected Bluetooth device. Logs an error if the
     *   socket is not connected. */
    fun sendData(message: String) {
        if (bluetoothSocket == null || bluetoothSocket?.isConnected == false) {
            Log.e(TAG, "Cannot send data: socket is not connected")
            return
        }

        try {
            Log.d(TAG, "Sending data: $message")
            bluetoothSocket?.outputStream?.write((message + "\n").toByteArray())  // Adding newline to delimit messages
        } catch (e: IOException) {
            Log.e(TAG, "Failed to send data: ${e.message}", e)
            // Optionally, handle socket reinitialization or reconnection here
        }
    }

    /** `receiveData(timeoutMillis: Long = 5000L)`: Waits for data from the connected Bluetooth device within a
     *   specified timeout period. Returns the received data as a string or `null` if no data is received. */
    fun receiveData(timeoutMillis: Long = 5000L): String? {
        return runBlocking {
            val socket = bluetoothSocket
            if (socket == null || !socket.isConnected) {
                Log.e(TAG, "Cannot receive data: socket is not connected")
                return@runBlocking null
            }

            withContext(Dispatchers.IO) {
                val inputStream = socket.inputStream
                try {
                    val startTime = System.currentTimeMillis()
                    while (System.currentTimeMillis() - startTime < timeoutMillis) {
                        val available = inputStream.available()
                        if (available > 0) {
                            val buffer = ByteArray(available)
                            inputStream.read(buffer)
                            val response = String(buffer).trim()
                            Log.d(TAG, "Received data: $response")
                            return@withContext response
                        }
                        delay(25)
                    }
                    Log.d(TAG, "Timeout waiting for response")
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading from input stream", e)
                }
                null
            }
        }
    }

    /** `cancelConnection()`: Cancels any ongoing connection or data transmission, closes the Bluetooth socket and
     *   associated streams, and cleans up resources. */
    fun cancelConnection() {
        try {
            connectJob?.cancel()
            sendJob?.cancel()
            connectJob = null
            sendJob = null
            outputStream?.close()
            bluetoothSocket?.closeSilently()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket or stream: ${e.message}", e)
        } finally {
            outputStream = null
            bluetoothSocket = null
        }
    }

    /** `BluetoothSocket?.closeSilently()`: Safely closes the Bluetooth socket, logging any errors that occur during
     *   the process. */
    private fun BluetoothSocket?.closeSilently() {
        try {
            this?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket", e)
        }
    }
}
