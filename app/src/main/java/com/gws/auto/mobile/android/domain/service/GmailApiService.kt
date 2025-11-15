package com.gws.auto.mobile.android.domain.service

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Draft
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.inject.Inject
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GmailApiService @Inject constructor(private val authorizer: GoogleApiAuthorizer) {

    private suspend fun getService(): Gmail {
        val credential = authorizer.getCredential() as GoogleAccountCredential
        return Gmail.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }

    suspend fun createDraft(to: String, subject: String, body: String): Draft {
        val mimeMessage = createMimeMessage(to, subject, body)
        val rawMessage = createRawMessage(mimeMessage)
        val draft = Draft().setMessage(rawMessage)
        return getService().users().drafts().create("me", draft).execute()
    }

    private fun createMimeMessage(to: String, subject: String, body: String): MimeMessage {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.setFrom(InternetAddress("me"))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        email.subject = subject
        email.setText(body)
        return email
    }

    private fun createRawMessage(mimeMessage: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        mimeMessage.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail = com.google.api.client.util.Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.raw = encodedEmail
        return message
    }
}
