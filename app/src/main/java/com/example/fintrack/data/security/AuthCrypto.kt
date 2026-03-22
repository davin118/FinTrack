package com.example.fintrack.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AuthCrypto {
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val PBKDF2_ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_SIZE_BYTES = 16

    data class PasswordMaterial(
        val hash: String,
        val salt: String
    )

    fun hashPassword(password: String): PasswordMaterial {
        val saltBytes = ByteArray(SALT_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        val hashBytes = pbkdf2(password, saltBytes, PBKDF2_ITERATIONS)
        return PasswordMaterial(
            hash = toHex(hashBytes),
            salt = toHex(saltBytes)
        )
    }

    fun verifyPassword(password: String, hashHex: String, saltHex: String): Boolean {
        if (hashHex.isBlank() || saltHex.isBlank()) return false
        val expected = fromHex(hashHex)
        val saltBytes = fromHex(saltHex)
        val candidate = pbkdf2(password, saltBytes, PBKDF2_ITERATIONS)
        return MessageDigest.isEqual(expected, candidate)
    }

    fun hashLegacySha256(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(password.toByteArray(Charsets.UTF_8)).joinToString("") { byte ->
            "%02x".format(byte)
        }
    }

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        return SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(keySpec).encoded
    }

    private fun toHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun fromHex(value: String): ByteArray {
        val clean = value.trim()
        require(clean.length % 2 == 0) { "Hex invalido" }
        return ByteArray(clean.length / 2) { index ->
            clean.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }
}
