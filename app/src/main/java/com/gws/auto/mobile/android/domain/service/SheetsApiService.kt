package com.gws.auto.mobile.android.domain.service

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SheetsApiService @Inject constructor(private val authorizer: GoogleApiAuthorizer) {

    private fun getService(): Sheets {
        val credential = authorizer.getCredential(listOf(SheetsScopes.SPREADSHEETS))
        return Sheets.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }

    @Throws(IOException::class)
    suspend fun getValues(spreadsheetId: String, range: String): ValueRange {
        val service = getService()
        Timber.d("Getting values from spreadsheet: $spreadsheetId, range: $range")
        return service.spreadsheets().values().get(spreadsheetId, range).execute()
    }

    @Throws(IOException::class)
    suspend fun updateValues(spreadsheetId: String, range: String, values: ValueRange) {
        val service = getService()
        Timber.d("Updating values to spreadsheet: $spreadsheetId, range: $range")
        service.spreadsheets().values().update(spreadsheetId, range, values)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }
}
