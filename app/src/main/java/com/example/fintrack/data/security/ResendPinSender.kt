package com.example.fintrack.data.security

import com.example.fintrack.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ResendPinSender {
    private const val RESEND_API_URL = "https://api.resend.com/emails"

    fun sendPin(toEmail: String, pin: String) {
        sendEmail(
            toEmail = toEmail,
            subject = "PIN temporal Ortvyn",
            text = """
                Tu PIN temporal es: $pin
                Este PIN vence en 10 minutos.
                Si no solicitaste este cambio, ignora este correo.
            """.trimIndent()
        )
    }

    fun sendTestEmail(toEmail: String) {
        sendEmail(
            toEmail = toEmail,
            subject = "Prueba de correo Ortvyn",
            text = """
                Este es un correo de prueba enviado desde Ortvyn.
                Si lo recibiste, tu integración con Resend está activa.
            """.trimIndent()
        )
    }

    private fun sendEmail(toEmail: String, subject: String, text: String) {
        val apiKey = BuildConfig.RESEND_API_KEY.trim()
        val fromEmail = BuildConfig.RESEND_FROM_EMAIL.trim()
        require(apiKey.isNotBlank()) { "RESEND_API_KEY no configurado." }
        require(fromEmail.isNotBlank()) { "RESEND_FROM_EMAIL no configurado." }

        val body = JSONObject()
            .put("from", fromEmail)
            .put("to", JSONArray().put(toEmail))
            .put("subject", subject)
            .put("text", text)
            .toString()

        val connection = (URL(RESEND_API_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json")
        }

        runCatching {
            connection.outputStream.use { output ->
                output.write(body.toByteArray(Charsets.UTF_8))
            }

            val code = connection.responseCode
            if (code !in 200..299) {
                val errorBody = runCatching {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                }.getOrDefault("")
                error("Resend fallo ($code): $errorBody")
            }
        }.getOrThrow()
    }
}
