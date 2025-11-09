package com.gws.auto.mobile.android.data.repository

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun getEvents(
        accountName: String,
        startTime: com.google.api.client.util.DateTime,
        endTime: com.google.api.client.util.DateTime
    ): List<Event>? {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAccountCredential.usingOAuth2(
                    context,
                    listOf(GoogleApiAuthorizer.calendarScope.scopeUri)
                ).apply {
                    selectedAccountName = accountName
                }

                val calendar = Calendar.Builder(
                    NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("GWS Auto for Android").build()

                calendar.events().list("primary")
                    .setTimeMin(startTime)
                    .setTimeMax(endTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                    .items
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
