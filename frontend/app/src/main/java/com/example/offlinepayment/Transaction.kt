package com.example.offlinepayment

data class Transaction(
    val transactionId: String,
    val senderId: String,
    val receiverId: String,
    val amount: Double,
    val timestamp: Long,
    val ttl: Int
)