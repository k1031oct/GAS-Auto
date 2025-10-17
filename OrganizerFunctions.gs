/**
 * @file OrganizerFunctions.gs
 * @brief This file contains the server-side functions for the file organization modules in the Automater project.
 * It includes functionalities for filtering, converting, and archiving files from Google Drive.
 * These functions are designed to be called from WorkflowService.gs as part of a larger workflow.
 */

/**
 * Filters files in a specified Google Drive folder based on various criteria.
 *
 * @param {object} configs The module's configuration settings.
 * @param {string[]} fileIds Input file IDs (ignored in this module as it's a starting point).
 * @return {string[]} A list of file IDs that match the filter criteria.
 */
function drive_filter_files(configs, fileIds) {
  const { 
    sourceFolderId, 
    filterFileType, 
    filterKeyword, 
    filterCondition, 
    moveToFolderId 
  } = configs;

  if (!sourceFolderId) {
    throw new Error('Source folder ID is not specified.');
  }

  const sourceFolder = DriveApp.getFolderById(sourceFolderId);
  const files = sourceFolder.getFiles();
  const filteredFileIds = [];

  while (files.hasNext()) {
    const file = files.next();
    let match = true;

    // Filter by file type
    if (filterFileType && filterFileType !== 'any') {
      const mimeType = file.getMimeType();
      switch (filterFileType) {
        case 'excel':
          if (mimeType !== MimeType.MICROSOFT_EXCEL && mimeType !== MimeType.MICROSOFT_EXCEL_LEGACY) {
            match = false;
          }
          break;
        case 'csv':
          if (mimeType !== MimeType.CSV) {
            match = false;
          }
          break;
        case 'pdf':
          if (mimeType !== MimeType.PDF) {
            match = false;
          }
          break;
        default:
          // For other specific mime types if needed
          if (mimeType !== filterFileType) {
            match = false;
          }
          break;
      }
    }

    // Filter by keyword
    if (match && filterKeyword) {
      const fileName = file.getName();
      switch (filterCondition) {
        case 'contains':
          if (!fileName.includes(filterKeyword)) {
            match = false;
          }
          break;
        case 'does_not_contain':
          if (fileName.includes(filterKeyword)) {
            match = false;
          }
          break;
        case 'starts_with':
          if (!fileName.startsWith(filterKeyword)) {
            match = false;
          }
          break;
        case 'ends_with':
          if (!fileName.endsWith(filterKeyword)) {
            match = false;
          }
          break;
      }
    }

    if (match) {
      filteredFileIds.push(file.getId());
    }
  }

  // Move filtered files to another folder if specified
  if (moveToFolderId && filteredFileIds.length > 0) {
    const moveFolder = DriveApp.getFolderById(moveToFolderId);
    filteredFileIds.forEach(fileId => {
      DriveApp.getFileById(fileId).moveTo(moveFolder);
    });
  }

  return filteredFileIds;
}

/**
 * Converts files to specified Google Workspace formats.
 *
 * @param {object} configs The module's configuration settings.
 * @param {string[]} fileIds A list of file IDs to be converted.
 * @return {string[]} A list of IDs of the newly converted files.
 */
function drive_convert_files(configs, fileIds) {
  const { actionType, outputFolderId, templateSheetUrl } = configs;

  if (!fileIds || fileIds.length === 0) {
    return [];
  }

  const convertedFileIds = [];

  fileIds.forEach(fileId => {
    const file = DriveApp.getFileById(fileId);
    const blob = file.getBlob();
    let newFile;

    switch (actionType) {
      case 'excel_to_sheet':
        const resource = {
          title: file.getName().replace(/\.xlsx?$/, ''),
          parents: outputFolderId ? [{ id: outputFolderId }] : []
        };
        newFile = Drive.Files.insert(resource, blob, {
          convert: true
        });
        convertedFileIds.push(newFile.id);
        break;
      
      case 'csv_to_sheet':
        if (!templateSheetUrl) {
          throw new Error('Template Google Sheet URL is required for CSV to Sheet conversion.');
        }
        const spreadsheet = SpreadsheetApp.openByUrl(templateSheetUrl);
        const sheet = spreadsheet.getSheets()[0]; // Use the first sheet
        const csvData = Utilities.parseCsv(blob.getDataAsString());
        sheet.getRange(1, 1, csvData.length, csvData[0].length).setValues(csvData);
        convertedFileIds.push(spreadsheet.getId()); // Returns the template sheet ID
        break;
    }
  });

  return convertedFileIds;
}

/**
 * Archives files into a specified folder structure.
 *
 * @param {object} configs The module's configuration settings.
 * @param {string[]} fileIds A list of file IDs to be archived.
 * @return {string[]} A list of the archived file IDs.
 */
function drive_archive_files(configs, fileIds) {
  const { 
    archiveRootFolderId, 
    archiveFormat, 
    renameKeyword, 
    overwriteAction 
  } = configs;

  if (!archiveRootFolderId) {
    throw new Error('Archive root folder ID is not specified.');
  }
  if (!fileIds || fileIds.length === 0) {
    return [];
  }

  const rootFolder = DriveApp.getFolderById(archiveRootFolderId);
  const archivedFileIds = [];

  fileIds.forEach(fileId => {
    const file = DriveApp.getFileById(fileId);
    const now = new Date();
    let targetFolder = rootFolder;

    // Create subfolders based on format
    if (archiveFormat) {
      const year = now.getFullYear().toString();
      const month = ('0' + (now.getMonth() + 1)).slice(-2);
      
      if (archiveFormat.includes('YYYY')) {
        targetFolder = getOrCreateFolder(targetFolder, year);
      }
      if (archiveFormat.includes('MM')) {
        targetFolder = getOrCreateFolder(targetFolder, month);
      }
    }

    let newName = file.getName();
    if (renameKeyword) {
      newName = `${renameKeyword}_${newName}`;
    }

    // Handle overwrite logic
    const existingFiles = targetFolder.getFilesByName(newName);
    let finalFile = null;

    if (existingFiles.hasNext()) {
      switch (overwriteAction) {
        case 'overwrite':
          while(existingFiles.hasNext()){
            existingFiles.next().setTrashed(true);
          }
          finalFile = file.moveTo(targetFolder).setName(newName);
          break;
        case 'add_sequence':
          let i = 1;
          let sequencedName = `${newName.substring(0, newName.lastIndexOf('.'))}_${i}${newName.substring(newName.lastIndexOf('.'))}`;
          while (targetFolder.getFilesByName(sequencedName).hasNext()) {
            i++;
            sequencedName = `${newName.substring(0, newName.lastIndexOf('.'))}_${i}${newName.substring(newName.lastIndexOf('.'))}`;
          }
          finalFile = file.moveTo(targetFolder).setName(sequencedName);
          break;
        case 'skip':
        default:
          // Do nothing, skip the file
          break;
      }
    } else {
      finalFile = file.moveTo(targetFolder).setName(newName);
    }
    
    if (finalFile) {
      archivedFileIds.push(finalFile.getId());
    }
  });

  return archivedFileIds;
}

/**
 * Helper function to get a folder by name, or create it if it doesn't exist.
 * @param {Folder} parentFolder The folder to search within.
 * @param {string} folderName The name of the folder to find or create.
 * @return {Folder} The found or newly created folder.
 */
function getOrCreateFolder(parentFolder, folderName) {
  const folders = parentFolder.getFoldersByName(folderName);
  if (folders.hasNext()) {
    return folders.next();
  } else {
    return parentFolder.createFolder(folderName);
  }
}
