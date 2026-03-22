package com.example.fintrack.data.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthCryptoTest {

    @Test
    fun hashPassword_generatesDifferentSaltAndHashEachTime() {
        val first = AuthCrypto.hashPassword("ClaveSegura123")
        val second = AuthCrypto.hashPassword("ClaveSegura123")

        assertNotEquals(first.salt, second.salt)
        assertNotEquals(first.hash, second.hash)
    }

    @Test
    fun verifyPassword_validatesCorrectAndIncorrectPassword() {
        val material = AuthCrypto.hashPassword("MiClave123")

        assertTrue(
            AuthCrypto.verifyPassword(
                password = "MiClave123",
                hashHex = material.hash,
                saltHex = material.salt
            )
        )
        assertFalse(
            AuthCrypto.verifyPassword(
                password = "OtraClave",
                hashHex = material.hash,
                saltHex = material.salt
            )
        )
    }

    @Test
    fun hashLegacySha256_isDeterministic() {
        val first = AuthCrypto.hashLegacySha256("legacy")
        val second = AuthCrypto.hashLegacySha256("legacy")

        assertEquals(first, second)
    }
}
