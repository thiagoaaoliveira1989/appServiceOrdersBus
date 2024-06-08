package com.maxcred.orderservice.utils

import android.os.AsyncTask
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class MailSender(
    private val userEmail: String,
    private val toEmail: String,
    private val subject: String,
    private val body: String,
    private val attachmentPath: String?,
    private val refreshToken: String
) :AsyncTask<Void?, Void?, Void?>() {
    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val credential = GoogleCredential.Builder()
                .setClientSecrets("431851236799-92a2tikcr4obp91hsqovmaqh0rlike13.apps.googleusercontent.com", "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")
                .build()
                .setRefreshToken(refreshToken)

            credential.refreshToken()

            val service = Gmail.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName("Gmail API")
                .build()

            val mimeMessage = createEmail(toEmail, userEmail, subject, body, attachmentPath)
            val message = createMessageWithEmail(mimeMessage)

            service.users().messages().send("me", message).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(MessagingException::class, IOException::class)
    private fun createEmail(to: String, from: String, subject: String, bodyText: String, filePath: String?): MimeMessage {
        val properties = System.getProperties()
        val session = Session.getDefaultInstance(properties, null)

        val email = MimeMessage(session)
        email.setFrom(InternetAddress(from))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        email.subject = subject
        email.setText(bodyText)

        if (filePath != null && filePath.isNotEmpty()) {
            val attachment = MimeBodyPart()
            val source = FileDataSource(File(filePath))
            attachment.dataHandler = DataHandler(source)
            attachment.fileName = source.name

            val multipart = MimeMultipart()
            multipart.addBodyPart(attachment)
            email.setContent(multipart)
        }
        return email
    }

    @Throws(IOException::class, MessagingException::class)
    private fun createMessageWithEmail(email: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        email.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail = Base64.getUrlEncoder().encodeToString(bytes)
        val message = Message()
        message.raw = encodedEmail
        return message
    }
}
