package com.example.offlinepayment

import android.util.Base64

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec


class CryptoManager {


    // =========================================================
    // AES-256 KEY GENERATION
    // =========================================================

    fun generateAESKey(): SecretKey {

        val keyGenerator =
            KeyGenerator.getInstance("AES")

        keyGenerator.init(256)

        return keyGenerator.generateKey()
    }


    // =========================================================
    // AES-256-GCM ENCRYPTION
    // =========================================================

    fun encryptAES(
        plainText: String,
        aesKey: SecretKey
    ): Triple<String, String, String> {

        val cipher =
            Cipher.getInstance(
                "AES/GCM/NoPadding"
            )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            aesKey
        )

        val encryptedBytes =
            cipher.doFinal(
                plainText.toByteArray(
                    Charsets.UTF_8
                )
            )

        val encryptedData =
            Base64.encodeToString(
                encryptedBytes,
                Base64.NO_WRAP
            )

        val iv =
            Base64.encodeToString(
                cipher.iv,
                Base64.NO_WRAP
            )

        return Triple(
            encryptedData,
            iv,
            "AES-256-GCM"
        )
    }


    // =========================================================
    // RSA-2048 KEY PAIR
    // =========================================================

    fun generateRSAKeyPair(): KeyPair {

        val generator =
            KeyPairGenerator.getInstance(
                "RSA"
            )

        generator.initialize(2048)

        return generator.generateKeyPair()
    }


    // =========================================================
    // RSA-OAEP ENCRYPT AES KEY
    //
    // SHA-256
    // MGF1 SHA-256
    // =========================================================

    fun encryptAESKey(
        aesKey: SecretKey,
        publicKey: PublicKey
    ): String {

        val cipher =
            Cipher.getInstance(
                "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
            )

        val oaepParameterSpec =
            OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            publicKey,
            oaepParameterSpec
        )

        val encryptedKey =
            cipher.doFinal(
                aesKey.encoded
            )

        return Base64.encodeToString(
            encryptedKey,
            Base64.NO_WRAP
        )
    }


    // =========================================================
    // RSA-OAEP DECRYPT AES KEY
    //
    // Used by the Android local demo
    // =========================================================

    fun decryptAESKey(
        encryptedAESKey: String,
        privateKey: PrivateKey
    ): SecretKey {

        val cipher =
            Cipher.getInstance(
                "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
            )

        cipher.init(
            Cipher.DECRYPT_MODE,
            privateKey
        )

        val encryptedBytes =
            Base64.decode(
                encryptedAESKey,
                Base64.NO_WRAP
            )

        val decryptedKey =
            cipher.doFinal(
                encryptedBytes
            )

        return SecretKeySpec(
            decryptedKey,
            "AES"
        )
    }


    // =========================================================
    // AES-GCM DECRYPTION
    // =========================================================

    fun decryptAES(
        encryptedData: String,
        iv: String,
        aesKey: SecretKey
    ): String {

        val cipher =
            Cipher.getInstance(
                "AES/GCM/NoPadding"
            )

        val ivBytes =
            Base64.decode(
                iv,
                Base64.NO_WRAP
            )

        val encryptedBytes =
            Base64.decode(
                encryptedData,
                Base64.NO_WRAP
            )

        val gcmSpec =
            GCMParameterSpec(
                128,
                ivBytes
            )

        cipher.init(
            Cipher.DECRYPT_MODE,
            aesKey,
            gcmSpec
        )

        val decryptedBytes =
            cipher.doFinal(
                encryptedBytes
            )

        return String(
            decryptedBytes,
            Charsets.UTF_8
        )
    }


    // =========================================================
    // BUILD BACKEND COMPATIBLE CIPHERTEXT
    //
    // Backend expects:
    //
    // [RSA encrypted AES key]
    // [12-byte IV]
    // [AES ciphertext + GCM tag]
    //
    // Then the entire byte array is Base64 encoded.
    // =========================================================

    fun buildBackendCiphertext(
        encryptedAESKey: String,
        iv: String,
        encryptedData: String
    ): String {

        val encryptedKeyBytes =
            Base64.decode(
                encryptedAESKey,
                Base64.NO_WRAP
            )

        val ivBytes =
            Base64.decode(
                iv,
                Base64.NO_WRAP
            )

        val encryptedDataBytes =
            Base64.decode(
                encryptedData,
                Base64.NO_WRAP
            )

        val combinedBytes =
            encryptedKeyBytes +
                    ivBytes +
                    encryptedDataBytes

        return Base64.encodeToString(
            combinedBytes,
            Base64.NO_WRAP
        )
    }


    // =========================================================
    // DECODE SERVER RSA PUBLIC KEY
    //
    // Backend sends X.509 encoded public key as Base64.
    // =========================================================

    fun decodeRSAPublicKey(
        publicKeyBase64: String
    ): PublicKey {

        val keyBytes =
            Base64.decode(
                publicKeyBase64,
                Base64.DEFAULT
            )

        val keySpec =
            X509EncodedKeySpec(
                keyBytes
            )

        val keyFactory =
            KeyFactory.getInstance(
                "RSA"
            )

        return keyFactory.generatePublic(
            keySpec
        )
    }
}