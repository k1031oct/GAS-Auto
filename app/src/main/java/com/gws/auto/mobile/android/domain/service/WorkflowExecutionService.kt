package com.gws.auto.mobile.android.domain.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.Sheets
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Workflow
import java.io.ByteArrayOutputStream

class WorkflowExecutionService(private val context: Context, private val credential: GoogleAccountCredential) {

    private val drive = Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
        .setApplicationName("GWS Auto for Android")
        .build()

    private val sheets = Sheets.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
        .setApplicationName("GWS Auto for Android")
        .build()

    fun execute(workflow: Workflow) {
        Thread {
            Log.d(TAG, "Executing workflow: ${workflow.name}")
            workflow.modules.forEach { module ->
                when (module.type) {
                    "LOG_MESSAGE" -> executeLogMessageModule(module)
                    "SHOW_TOAST" -> executeShowToastModule(module)
                    "CREATE_PDF_FROM_SHEET" -> executeCreatePdfFromSheetModule(module)
                    "CONVERT_EXCEL_TO_SHEET" -> executeConvertExcelToSheetModule(module)
                    else -> Log.w(TAG, "Unknown module type: ${module.type}")
                }
            }
        }.start()
    }

    private fun executeLogMessageModule(module: Module) {
        val message = module.parameters["message"] ?: "No message specified"
        Log.i(TAG, "[LOG_MESSAGE] $message")
    }

    private fun executeShowToastModule(module: Module) {
        val message = module.parameters["message"] ?: "No message to show"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun executeCreatePdfFromSheetModule(module: Module) {
        try {
            val sheetId = module.parameters["sheet_id"] ?: throw IllegalArgumentException("Sheet ID is required")

            val outputStream = ByteArrayOutputStream()
            drive.files().export(sheetId, "application/pdf").executeMediaAndDownloadTo(outputStream)

            Log.d(TAG, "Successfully exported sheet to PDF")
            // In a real implementation, you would save this to a file or another location.

        } catch (e: Exception) {
            Log.e(TAG, "Error creating PDF from sheet", e)
        }
    }

    private fun executeConvertExcelToSheetModule(module: Module) {
        try {
            val excelFileId = module.parameters["excel_file_id"] ?: throw IllegalArgumentException("Excel file ID is required")

            val fileMetadata = File().setName("Converted Spreadsheet")
                .setMimeType("application/vnd.google-apps.spreadsheet")

            drive.files().copy(excelFileId, fileMetadata).execute()

            Log.d(TAG, "Successfully converted Excel to Sheet")

        } catch (e: Exception) {
            Log.e(TAG, "Error converting Excel to Sheet", e)
        }
    }

    companion object {
        private const val TAG = "WorkflowExecutionSvc"
    }
}
