package com.example.offlinepayment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.offlinepayment.ui.theme.OfflinePaymentTheme

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.json.JSONObject

import java.util.UUID


class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            OfflinePaymentTheme {

                PaymentScreen()
            }
        }
    }
}


@Composable
fun PaymentScreen() {


    // =========================================================
    // PAYMENT VARIABLES
    // =========================================================

    var receiverId by remember {

        mutableStateOf("")
    }


    var amount by remember {

        mutableStateOf("")
    }


    var output by remember {

        mutableStateOf("")
    }

    var lastBackendCiphertext by remember { mutableStateOf<String?>(null) }
    var lastPaymentReceiver by remember { mutableStateOf("") }
    var lastPaymentAmount by remember { mutableStateOf("") }
    var lastPaymentNonce by remember { mutableStateOf<String?>(null) }




    var receivedPacket by remember {

        mutableStateOf("")
    }


    var lastPacket by remember {

        mutableStateOf<SecurePacket?>(null)
    }


    // =========================================================
    // CRYPTO
    // =========================================================

    val cryptoManager =
        remember {

            CryptoManager()
        }


    // =========================================================
    // BACKEND API
    //
    // Android Emulator:
    // http://10.0.2.2:8080
    //
    // Physical phone:
    // Replace with PC's IPv4 address.
    // Example:
    // http://192.168.1.5:8080
    // =========================================================

    val backendApi =
        remember {

            BackendApiClient(
                "http://192.168.1.34:8080"
            )

        }


    // =========================================================
    // LOCAL RSA KEY PAIR
    //
    // Used for the existing local encryption/decryption demo.
    //
    // Backend integration will use SERVER public key.
    // =========================================================

    val rsaKeyPair =
        remember {

            cryptoManager.generateRSAKeyPair()
        }


    // =========================================================
    // BLUETOOTH
    // =========================================================

    val context =
        LocalContext.current


    val bluetoothService =
        remember {

            BluetoothService(context)
        }


    val bluetoothAdapter =
        remember {

            BluetoothAdapter.getDefaultAdapter()
        }


    var pairedDevices by remember {

        mutableStateOf<List<BluetoothDevice>>(
            emptyList()
        )
    }


    var selectedDevice by remember {

        mutableStateOf<BluetoothDevice?>(null)
    }


    var bluetoothStatus by remember {

        mutableStateOf(
            "Bluetooth not connected"
        )
    }


    // =========================================================
    // BLUETOOTH PERMISSIONS
    // =========================================================

    val bluetoothPermissions =

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            arrayOf(

                Manifest.permission.BLUETOOTH_SCAN,

                Manifest.permission.BLUETOOTH_CONNECT
            )

        } else {

            arrayOf(

                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }


    val permissionLauncher =
        rememberLauncherForActivityResult(

            ActivityResultContracts
                .RequestMultiplePermissions()

        ) { permissions ->

            val granted =
                permissions.values.all {
                    it
                }


            bluetoothStatus =

                if (granted) {

                    "✓ Bluetooth permissions granted"

                } else {

                    "✗ Bluetooth permissions denied"
                }
        }


    // =========================================================
    // UI
    // =========================================================

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(
                    rememberScrollState()
                ),

        verticalArrangement =
            Arrangement.Top
    ) {


        Text(

            text = "Offline Payment",

            fontSize = 28.sp
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // =====================================================
        // RECEIVER ID
        // =====================================================

        OutlinedTextField(

            value = receiverId,

            onValueChange = {

                receiverId = it
            },

            label = {

                Text(
                    "Receiver Account ID"
                )
            },

            modifier =
                Modifier.fillMaxWidth()
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // =====================================================
        // AMOUNT
        // =====================================================

        OutlinedTextField(

            value = amount,

            onValueChange = {

                amount = it
            },

            label = {

                Text("Amount")
            },

            modifier =
                Modifier.fillMaxWidth()
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // =====================================================
        // BACKEND CONNECTION TEST
        // =====================================================

        Button(

            onClick = {

                CoroutineScope(
                    Dispatchers.Main
                ).launch {

                    try {

                        val publicKey =
                            backendApi
                                .getServerPublicKey()


                        output =

                            "✓ BACKEND CONNECTED\n\n" +

                                    "RSA Public Key received successfully.\n\n" +

                                    "Key length: " +
                                    "${publicKey.length} characters."

                    } catch (e: Exception) {

                        output =

                            "✗ BACKEND CONNECTION FAILED\n\n" +

                                    (
                                            e.message
                                                ?: "Unknown error"
                                            )
                    }
                }
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                "Test Backend Connection"
            )
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // =====================================================
        // CREATE SECURE PACKET
        // =====================================================

        Button(

            onClick = {


                if (
                    receiverId.isBlank() ||
                    amount.isBlank()
                ) {

                    output =
                        "Please enter receiver ID and amount."

                    return@Button
                }


                val amountValue =
                    amount.toDoubleOrNull()


                if (
                    amountValue == null ||
                    amountValue <= 0
                ) {

                    output =
                        "Please enter a valid amount."

                    return@Button
                }


                val transactionId =
                    "TXN-" +
                            UUID.randomUUID()


                val timestamp =
                    System.currentTimeMillis()


                val transaction =

                    Transaction(

                        transactionId =
                            transactionId,

                        senderId =
                            "ACC101",

                        receiverId =
                            receiverId,

                        amount =
                            amountValue,

                        timestamp =
                            timestamp,

                        ttl =
                            10
                    )


                // =============================================
                // TRANSACTION JSON
                // =============================================

                val json =

                    JSONObject().apply {


                        put(
                            "transactionId",
                            transaction.transactionId
                        )


                        put(
                            "senderId",
                            transaction.senderId
                        )


                        put(
                            "receiverId",
                            transaction.receiverId
                        )


                        put(
                            "amount",
                            transaction.amount
                        )


                        put(
                            "timestamp",
                            transaction.timestamp
                        )


                        put(
                            "ttl",
                            transaction.ttl
                        )
                    }


                // =============================================
                // AES ENCRYPTION
                // =============================================

                val aesKey =
                    cryptoManager
                        .generateAESKey()


                val encrypted =
                    cryptoManager.encryptAES(

                        json.toString(),

                        aesKey
                    )


                // =============================================
                // CURRENT LOCAL RSA ENCRYPTION
                //
                // Used by the existing local demo.
                // =============================================

                val encryptedAESKey =
                    cryptoManager.encryptAESKey(

                        aesKey,

                        rsaKeyPair.public
                    )


                // =============================================
                // SECURE PACKET
                // =============================================

                val packet =

                    SecurePacket(

                        transactionId =
                            transactionId,

                        encryptedData =
                            encrypted.first,

                        encryptedAESKey =
                            encryptedAESKey,

                        iv =
                            encrypted.second,

                        timestamp =
                            timestamp,

                        ttl =
                            10
                    )


                lastPacket =
                    packet


                val jsonPacket =
                    PacketSerializer.toJson(
                        packet
                    )


                output = """

                    ✓ SECURE PACKET CREATED

                    $jsonPacket

                    --------------------------------

                    AES-256-GCM
                    RSA-OAEP

                    Ready for Bluetooth transmission.

                """.trimIndent()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                "Create Secure Packet"
            )
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


// =====================================================
// SEND PAYMENT TO BACKEND
// =====================================================

        Button(
            onClick = {
                if (receiverId.isBlank()) {
                    output = "Please enter receiver VPA."
                    return@Button
                }

                val amountValue = amount.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    output = "Please enter a valid amount."
                    return@Button
                }

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val currentReceiver = receiverId.trim()
                        val currentAmount = amount.trim()

                        // Reuse the exact same encrypted packet when the
                        // user presses Send again for the same payment.
                        val samePayment =
                            lastBackendCiphertext != null &&
                                    lastPaymentReceiver == currentReceiver &&
                                    lastPaymentAmount == currentAmount

                        val backendCiphertext: String
                        val nonce: String

                        if (samePayment) {
                            backendCiphertext = lastBackendCiphertext!!
                            nonce = lastPaymentNonce ?: ""

                            output = """
                                Retrying the same payment...

                                Same encrypted packet
                                Same nonce

                                Sending to backend...
                            """.trimIndent()
                        } else {
                            output = "Fetching server RSA public key..."

                            val publicKeyBase64 =
                                backendApi.getServerPublicKey()

                            val serverPublicKey =
                                cryptoManager.decodeRSAPublicKey(
                                    publicKeyBase64
                                )

                            val senderVpa = "user3210@meshpay"
                            nonce = UUID.randomUUID().toString()
                            val signedAt = System.currentTimeMillis()

                            val paymentInstruction =
                                JSONObject().apply {
                                    put("senderVpa", senderVpa)
                                    put("receiverVpa", currentReceiver)
                                    put("amount", amountValue)
                                    put("nonce", nonce)
                                    put("signedAt", signedAt)
                                }

                            val aesKey =
                                cryptoManager.generateAESKey()

                            val encrypted =
                                cryptoManager.encryptAES(
                                    paymentInstruction.toString(),
                                    aesKey
                                )

                            val encryptedAESKey =
                                cryptoManager.encryptAESKey(
                                    aesKey,
                                    serverPublicKey
                                )

                            backendCiphertext =
                                cryptoManager.buildBackendCiphertext(
                                    encryptedAESKey = encryptedAESKey,
                                    iv = encrypted.second,
                                    encryptedData = encrypted.first
                                )

                            // Keep this exact packet for retries.
                            lastBackendCiphertext = backendCiphertext
                            lastPaymentReceiver = currentReceiver
                            lastPaymentAmount = currentAmount
                            lastPaymentNonce = nonce

                            output = """
                                ✓ PAYMENT ENCRYPTED

                                Sender:
                                $senderVpa

                                Receiver:
                                $currentReceiver

                                Amount:
                                ₹$amountValue

                                Encryption:
                                AES-256-GCM

                                Key Protection:
                                RSA-OAEP SHA-256

                                Sending to backend...
                            """.trimIndent()
                        }

                        val response =
                            backendApi.processPayment(backendCiphertext)

                        output = """
                            ✓ PAYMENT PROCESSED SUCCESSFULLY

                            Backend response:
                            $response

                            --------------------------------

                            Sender:
                            user3210@meshpay

                            Receiver:
                            ${receiverId.trim()}

                            Amount:
                            ₹$amountValue

                            Nonce:
                            $nonce

                            Encryption:
                            AES-256-GCM

                            Key Encryption:
                            RSA-OAEP SHA-256

                            --------------------------------

                            Payment reached the backend.
                        """.trimIndent()

                    } catch (e: Exception) {
                        val errorMessage =
                            e.message ?: "Unknown error"

                        if (
                            errorMessage.contains(
                                "Duplicate payment",
                                ignoreCase = true
                            )
                        ) {
                            output = """
                                ✗ DUPLICATE PAYMENT REJECTED

                                This payment was already processed.

                                Same transaction packet
                                Same nonce

                                The backend prevented
                                the payment from being processed twice.
                            """.trimIndent()
                        } else {
                            output = """
                                ✗ PAYMENT FAILED

                                $errorMessage
                            """.trimIndent()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Payment to Backend")
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

//
//        // =====================================================
//        // BLUETOOTH PERMISSION
//        // =====================================================
//
//        Button(
//
//            onClick = {
//
//                permissionLauncher.launch(
//                    bluetoothPermissions
//                )
//            },
//
//            modifier =
//                Modifier.fillMaxWidth()
//        ) {
//
//            Text(
//                "Enable Bluetooth"
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(12.dp)
//        )
//
//
//        // =====================================================
//        // PAIRED BLUETOOTH DEVICES
//        // =====================================================
//
//        Button(
//
//            onClick = {
//
//                try {
//
//                    pairedDevices =
//
//                        bluetoothAdapter
//                            ?.bondedDevices
//                            ?.toList()
//                            ?: emptyList()
//
//
//                    bluetoothStatus =
//
//                        if (
//                            pairedDevices.isEmpty()
//                        ) {
//
//                            "No paired Bluetooth devices found."
//
//                        } else {
//
//                            "${pairedDevices.size} paired device(s) found."
//                        }
//
//                } catch (e: Exception) {
//
//                    bluetoothStatus =
//                        "Unable to read paired devices."
//                }
//            },
//
//            modifier =
//                Modifier.fillMaxWidth()
//        ) {
//
//            Text(
//                "Show Paired Devices"
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(12.dp)
//        )
//
//
//        // =====================================================
//        // BLUETOOTH DEVICE BUTTONS
//        // =====================================================
//
//        pairedDevices.forEach { device ->
//
//            Button(
//
//                onClick = {
//
//                    selectedDevice =
//                        device
//
//
//                    bluetoothStatus =
//                        "Connecting to " +
//                                "${device.name ?: "device"}..."
//
//
//                    bluetoothService.connectToDevice(
//
//                        device = device,
//
//                        onConnected = {
//
//                            bluetoothStatus =
//                                "✓ Connected to " +
//                                        "${device.name ?: "device"}"
//                        },
//
//                        onMessageReceived = {
//                                message ->
//
//                            receivedPacket =
//                                message
//
//                            bluetoothStatus =
//                                "✓ Secure packet received"
//                        },
//
//                        onError = {
//                                error ->
//
//                            bluetoothStatus =
//                                "✗ $error"
//                        }
//                    )
//                },
//
//                modifier =
//                    Modifier.fillMaxWidth()
//            ) {
//
//                Text(
//                    device.name
//                        ?: "Unknown Device"
//                )
//            }
//
//
//            Spacer(
//                modifier =
//                    Modifier.height(8.dp)
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(12.dp)
//        )
//
//
//        // =====================================================
//        // START BLUETOOTH RECEIVER
//        // =====================================================
//
//        Button(
//
//            onClick = {
//
//                bluetoothService.startServer(
//
//                    onConnected = {
//
//                        bluetoothStatus =
//                            "✓ Phone connected"
//                    },
//
//                    onMessageReceived = {
//                            message ->
//
//                        receivedPacket =
//                            message
//
//                        bluetoothStatus =
//                            "✓ Secure packet received"
//                    },
//
//                    onError = {
//                            error ->
//
//                        bluetoothStatus =
//                            "✗ $error"
//                    }
//                )
//
//
//                bluetoothStatus =
//                    "Waiting for another phone..."
//            },
//
//            modifier =
//                Modifier.fillMaxWidth()
//        ) {
//
//            Text(
//                "Start Bluetooth Receiver"
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(12.dp)
//        )
//
//
//        // =====================================================
//        // SEND PACKET THROUGH BLUETOOTH
//        // =====================================================
//
//        Button(
//
//            onClick = {
//
//                val packet =
//                    lastPacket
//
//
//                if (packet == null) {
//
//                    output =
//                        "Create a secure packet first."
//
//                    return@Button
//                }
//
//
//                val jsonPacket =
//                    PacketSerializer.toJson(
//                        packet
//                    )
//
//
//                bluetoothService.sendMessage(
//                    jsonPacket
//                )
//
//
//                bluetoothStatus =
//                    "✓ Secure packet sent"
//            },
//
//            modifier =
//                Modifier.fillMaxWidth()
//        ) {
//
//            Text(
//                "Send Secure Packet"
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(20.dp)
//        )
//
//
//        // =====================================================
//        // BLUETOOTH STATUS
//        // =====================================================
//
//        Text(
//
//            text =
//                "Bluetooth Status:",
//
//            fontSize =
//                18.sp
//        )
//
//
//        Spacer(
//            modifier =
//                Modifier.height(6.dp)
//        )
//
//
//        Text(
//
//            text =
//                bluetoothStatus,
//
//            fontSize =
//                14.sp
//        )
//
//
//        Spacer(
//            modifier =
//                Modifier.height(20.dp)
//        )

//
//        // =====================================================
//        // RECEIVED PACKET
//        // =====================================================
//
//        Text(
//
//            text =
//                "Received Packet:",
//
//            fontSize =
//                18.sp
//        )
//
//
//        Spacer(
//            modifier =
//                Modifier.height(6.dp)
//        )
//
//
//        Text(
//
//            text =
//
//                if (
//                    receivedPacket.isEmpty()
//                ) {
//
//                    "No packet received yet."
//
//                } else {
//
//                    receivedPacket
//                },
//
//            fontSize =
//                13.sp
//        )
//
//
//        Spacer(
//            modifier =
//                Modifier.height(20.dp)
//        )
//

//        // =====================================================
//        // DECRYPT PACKET
//        // =====================================================
//
//        Button(
//
//            onClick = {
//
//                val packet =
//                    lastPacket
//
//
//                if (packet == null) {
//
//                    output =
//                        "Create a secure packet first."
//
//                    return@Button
//                }
//
//
//                try {
//
//                    val recoveredAESKey =
//                        cryptoManager
//                            .decryptAESKey(
//
//                                packet.encryptedAESKey,
//
//                                rsaKeyPair.private
//                            )
//
//
//                    val decryptedData =
//                        cryptoManager.decryptAES(
//
//                            packet.encryptedData,
//
//                            packet.iv,
//
//                            recoveredAESKey
//                        )
//
//
//                    output = """
//
//                        ✓ DECRYPTION SUCCESSFUL
//
//                        Transaction ID:
//                        ${packet.transactionId}
//
//                        Original Transaction:
//
//                        $decryptedData
//
//                    """.trimIndent()
//
//
//                } catch (e: Exception) {
//
//                    output =
//                        "✗ DECRYPTION FAILED\n\n" +
//                                "${e.message}"
//                }
//            },
//
//            modifier =
//                Modifier.fillMaxWidth()
//        ) {
//
//            Text(
//                "Decrypt Packet"
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(12.dp)
//        )

//
//        // =====================================================
//        // TAMPER TEST
//        // =====================================================
//
//        Button(
//
//            onClick = {
//
//                val packet =
//                    lastPacket
//
//
//                if (packet == null) {
//
//                    output =
//                        "Create a secure packet first."
//
//                    return@Button
//                }
//
//
//                try {
//
//                    val original =
//                        packet.encryptedData
//
//
//                    val modified =
//
//                        if (
//                            original.first() == 'A'
//                        ) {
//
//                            "B" +
//                                    original.drop(1)
//
//                        } else {
//
//                            "A" +
//                                    original.drop(1)
//                        }
//
//
//                    val tamperedPacket =
//                        packet.copy(
//
//                            encryptedData =
//                                modified
//                        )
//
//
//                    val recoveredAESKey =
//                        cryptoManager
//                            .decryptAESKey(
//
//                                tamperedPacket
//                                    .encryptedAESKey,
//
//                                rsaKeyPair.private
//                            )
//
//
//                    cryptoManager.decryptAES(
//
//                        tamperedPacket
//                            .encryptedData,
//
//                        tamperedPacket.iv,
//
//                        recoveredAESKey
//                    )
//
//
//                    output =
//                        "⚠ WARNING\n\n" +
//                                "Tampered packet was accepted."
//
//
//                } catch (e: Exception) {
//
//                    output = """
//
//                        ✓ TAMPERING DETECTED
//
//                        AES-256-GCM rejected the
//                        modified packet.
//
//                        Transaction NOT accepted.
//
//                    """.trimIndent()
//                }
//            },
//
//            modifier =
//                Modifier.fillMaxWidth()
//        ) {
//
//            Text(
//                "Test Tampering"
//            )
//        }
//
//
//        Spacer(
//            modifier =
//                Modifier.height(12.dp)
//        )
//
//
//

        // =====================================================
        // OUTPUT
        // =====================================================

        Text(

            text =
                output,

            fontSize =
                14.sp
        )
    }
}
