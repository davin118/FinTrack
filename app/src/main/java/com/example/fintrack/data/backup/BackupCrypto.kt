package com.example.fintrack.data.backup

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {
    private val MAGIC = "ORTVYNBK1".toByteArray(Charsets.UTF_8)
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12
    private const val KEY_LENGTH_BITS = 256
    private const val PBKDF2_ITERATIONS = 120_000

    fun encrypt(plainData: ByteArray, password: String): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(salt)
        SecureRandom().nextBytes(iv)

        val secretKey = deriveKey(password = password, salt = salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val cipherText = cipher.doFinal(plainData)

        val buffer = ByteBuffer.allocate(MAGIC.size + 1 + SALT_LENGTH + IV_LENGTH + cipherText.size)
        buffer.put(MAGIC)
        buffer.put(SALT_LENGTH.toByte())
        buffer.put(salt)
        buffer.put(iv)
        buffer.put(cipherText)
        return buffer.array()
    }

    fun decrypt(encryptedData: ByteArray, password: String): ByteArray {
        val buffer = ByteBuffer.wrap(encryptedData)

        val magic = ByteArray(MAGIC.size)
        buffer.get(magic)
        require(magic.contentEquals(MAGIC)) { "Formato de backup invalido" }

        val saltLength = buffer.get().toInt()
        require(saltLength > 0) { "Salt invalido" }

        val salt = ByteArray(saltLength)
        buffer.get(salt)

        val iv = ByteArray(IV_LENGTH)
        buffer.get(iv)

        val cipherText = ByteArray(buffer.remaining())
        buffer.get(cipherText)

        val secretKey = deriveKey(password = password, salt = salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        return cipher.doFinal(cipherText)
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val key = factory.generateSecret(spec).encoded
        return SecretKeySpec(key, "AES")
    }
}
