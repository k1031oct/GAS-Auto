package com.gws.auto.mobile.android.domain.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class DriveApiService @Inject constructor(private val authorizer: GoogleApiAuthorizer) {

    private fun getService(): Drive {
        val credential = authorizer.getCredential(listOf(DriveScopes.DRIVE))
        return Drive.Builder(authorizer.httpTransport, authorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }

    @Throws(IOException::class)
    suspend fun duplicateAndMoveFile(sourceFileId: String, newFileName: String, targetFolderId: String?): File {
        val driveService = getService()

        // 1. Create a copy of the file
        val newFileMetadata = File().setName(newFileName)
        val copiedFile = driveService.files().copy(sourceFileId, newFileMetadata).execute()
        Timber.d("File duplicated with ID: ${copiedFile.id}")

        // 2. If a target folder is specified, move the file
        if (!targetFolderId.isNullOrBlank()) {
            val file = driveService.files().get(copiedFile.id).setFields("parents").execute()
            val previousParents = file.parents.joinToString(",")

            driveService.files().update(copiedFile.id, null)
                .setAddParents(targetFolderId)
                .setRemoveParents(previousParents)
                .setFields("id, parents")
                .execute()
            Timber.d("File ${copiedFile.id} moved to folder $targetFolderId")
        }
        
        return copiedFile
    }
}
