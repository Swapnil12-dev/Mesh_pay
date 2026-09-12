package com.example.offlinepayment

data class SecurePacket(
    val transactionId: String,
    val encryptedData: String,
    val encryptedAESKey: String,
    val iv: String,
    val timestamp: Long,
    val ttl: Int
)