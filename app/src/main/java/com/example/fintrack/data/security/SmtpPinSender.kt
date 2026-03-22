package com.example.fintrack.data.security

import com.example.fintrack.BuildConfig
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object SmtpPinSender {

    fun sendPin(toEmail: String, pin: String) {
        val host = BuildConfig.SMTP_HOST.trim()
        val username = BuildConfig.SMTP_USERNAME.trim()
        val password = BuildConfig.SMTP_PASSWORD
        val fromEmail = BuildConfig.SMTP_FROM_EMAIL.trim()
        val port = BuildConfig.SMTP_PORT

        require(host.isNotBlank()) { "SMTP_HOST no configurado." }
        require(username.isNotBlank()) { "SMTP_USERNAME no configurado." }
        require(password.isNotBlank()) { "SMTP_PASSWORD no configurado." }
        require(fromEmail.isNotBlank()) { "SMTP_FROM_EMAIL no configurado." }
        require(port > 0) { "SMTP_PORT invalido." }

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", host)
            put("mail.smtp.port", port.toString())
            put("mail.smtp.connectiontimeout", "10000")
            put("mail.smtp.timeout", "10000")
            put("mail.smtp.writetimeout", "10000")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(fromEmail))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
            subject = "PIN temporal Ortvyn"
            setText(
                """
                Tu PIN temporal es: $pin
                Este PIN vence en 10 minutos.
                Si no solicitaste este cambio, ignora este correo.
                """.trimIndent()
            )
        }

        Transport.send(message)
    }
}
