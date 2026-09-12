package com.example.offlinepayment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class BackendApiClient(
    private val baseUrl: String
) {

    // =========================================================
    // GET SERVER RSA PUBLIC KEY
    // =========================================================

    suspend fun getServerPublicKey(): String =
        withContext(Dispatchers.IO) {

            val url = URL(
                "${baseUrl.trimEnd('/')}/api/crypto/public-key"
            )

            val connection =
                url.openConnection() as HttpURLConnection

            try {

                connection.requestMethod = "GET"

                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode =
                    connection.responseCode

                if (
                    responseCode !=
                    HttpURLConnection.HTTP_OK
                ) {
                    throw Exception(
                        "Public key request failed: HTTP $responseCode"
                    )
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                val json =
                    JSONObject(response)

                json.getString("publicKey")

            } finally {

                connection.disconnect()
            }
        }


    // =========================================================
    // PROCESS PAYMENT
    // =========================================================

    suspend fun processPayment(
        ciphertext: String
    ): String =
        withContext(Dispatchers.IO) {

            val url = URL(
                "${baseUrl.trimEnd('/')}/api/payments/process"
            )

            val connection =
                url.openConnection() as HttpURLConnection

            try {

                connection.requestMethod = "POST"

                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val requestJson =
                    JSONObject().apply {

                        put(
                            "ciphertext",
                            ciphertext
                        )
                    }

                val requestBody =
                    requestJson
                        .toString()
                        .toByteArray(Charsets.UTF_8)

                connection.outputStream.use { output ->

                    output.write(requestBody)
                    output.flush()
                }

                val responseCode =
                    connection.responseCode

                val responseStream =
                    if (responseCode in 200..299) {

                        connection.inputStream

                    } else {

                        connection.errorStream
                    }

                val response =
                    responseStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: "No response"

                if (responseCode !in 200..299) {

                    throw Exception(
                        "Payment processing failed: " +
                                "HTTP $responseCode - $response"
                    )
                }

                response

            } finally {

                connection.disconnect()
            }
        }
}