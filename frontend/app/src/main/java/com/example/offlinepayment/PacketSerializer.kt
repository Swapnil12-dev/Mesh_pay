package com.example.offlinepayment

import org.json.JSONObject

object PacketSerializer {

    fun toJson(packet: SecurePacket): String {

        val json = JSONObject()

        json.put("packetType", "PAYMENT")
        json.put("version", 1)

        json.put("transactionId", packet.transactionId)
        json.put("encryptedData", packet.encryptedData)
        json.put("encryptedAESKey", packet.encryptedAESKey)
        json.put("iv", packet.iv)
        json.put("timestamp", packet.timestamp)
        json.put("ttl", packet.ttl)

        return json.toString()
    }

    fun fromJson(jsonString: String): SecurePacket {

        val json = JSONObject(jsonString)

        return SecurePacket(
            transactionId =
                json.getString("transactionId"),

            encryptedData =
                json.getString("encryptedData"),

            encryptedAESKey =
                json.getString("encryptedAESKey"),

            iv =
                json.getString("iv"),

            timestamp =
                json.getLong("timestamp"),

            ttl =
                json.getInt("ttl")
        )
    }
}