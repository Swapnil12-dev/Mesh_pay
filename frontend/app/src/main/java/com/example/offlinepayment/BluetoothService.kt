package com.example.offlinepayment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID

class BluetoothService(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE)
                as android.bluetooth.BluetoothManager).adapter

    private var socket: BluetoothSocket? = null

    private var serverSocket: BluetoothServerSocket? = null

    private var outputStream: DataOutputStream? = null

    private val serviceName = "OfflinePayment"

    private val uuid =
        UUID.fromString(
            "8f7c9c10-7a9a-4e7e-9c8e-123456789001"
        )

    // =================================================
    // START RECEIVER
    // =================================================

    @SuppressLint("MissingPermission")
    fun startServer(
        onConnected: () -> Unit,
        onMessageReceived: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                serverSocket =
                    bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                        serviceName,
                        uuid
                    )

                socket =
                    serverSocket?.accept()

                serverSocket?.close()

                outputStream =
                    DataOutputStream(
                        socket!!.outputStream
                    )

                val input =
                    DataInputStream(
                        socket!!.inputStream
                    )

                CoroutineScope(Dispatchers.Main).launch {
                    onConnected()
                }

                while (true) {

                    val messageLength =
                        input.readInt()

                    if (messageLength <= 0) {
                        continue
                    }

                    val messageBytes =
                        ByteArray(messageLength)

                    input.readFully(messageBytes)

                    val message =
                        String(
                            messageBytes,
                            Charsets.UTF_8
                        )

                    CoroutineScope(Dispatchers.Main).launch {
                        onMessageReceived(message)
                    }
                }

            } catch (e: Exception) {

                CoroutineScope(Dispatchers.Main).launch {
                    onError(
                        e.message ?: "Bluetooth receiver error"
                    )
                }
            }
        }
    }

    // =================================================
    // CONNECT TO PAIRED DEVICE
    // =================================================

    @SuppressLint("MissingPermission")
    fun connectToDevice(
        device: BluetoothDevice,
        onConnected: () -> Unit,
        onMessageReceived: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                bluetoothAdapter?.cancelDiscovery()

                socket =
                    device.createRfcommSocketToServiceRecord(
                        uuid
                    )

                socket?.connect()

                outputStream =
                    DataOutputStream(
                        socket!!.outputStream
                    )

                val input =
                    DataInputStream(
                        socket!!.inputStream
                    )

                CoroutineScope(Dispatchers.Main).launch {
                    onConnected()
                }

                while (true) {

                    val messageLength =
                        input.readInt()

                    if (messageLength <= 0) {
                        continue
                    }

                    val messageBytes =
                        ByteArray(messageLength)

                    input.readFully(messageBytes)

                    val message =
                        String(
                            messageBytes,
                            Charsets.UTF_8
                        )

                    CoroutineScope(Dispatchers.Main).launch {
                        onMessageReceived(message)
                    }
                }

            } catch (e: Exception) {

                CoroutineScope(Dispatchers.Main).launch {
                    onError(
                        e.message ?: "Bluetooth connection failed"
                    )
                }
            }
        }
    }

    // =================================================
    // SEND MESSAGE
    // =================================================

    fun sendMessage(message: String) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val bytes =
                    message.toByteArray(Charsets.UTF_8)

                outputStream?.writeInt(bytes.size)

                outputStream?.write(bytes)

                outputStream?.flush()

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    // =================================================
    // CLOSE CONNECTION
    // =================================================

    fun close() {

        try {

            socket?.close()

            serverSocket?.close()

            outputStream?.close()

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}