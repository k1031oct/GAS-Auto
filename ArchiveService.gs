/**
 * @fileoverview This file contains the service for archiving Excel files as Google Sheets.
 * It handles searching for files, converting them, and organizing them into a structured folder hierarchy.
 */

/**
 * The main handler function for the Excel archiving process.
 * This function is designed to be called from a trigger or the UI.
 *
 * @param {object} settings The settings for the archiving process.
 * @param {string} settings.sourceFolderId The ID of the Google Drive folder to search for Excel files.
 * @param {string} settings.searchTerm The text to search for in the Excel file names.
 * @param {string} settings.archiveFolderName The name of the root folder for archiving.
 * @returns {void}
 */
function archiveExcelFilesHandler(settings) {
  const { sourceFolderId, searchTerm, archiveFolderName } = settings;

  if (!sourceFolderId || !searchTerm || !archiveFolderName) {
    LogService.error('ArchiveService: Missing required settings.');
    return;
  }

  try {
    const sourceFolder = DriveApp.getFolderById(sourceFolderId);
    const excelFiles = searchExcelFiles(sourceFolder, searchTerm);

    excelFiles.forEach(file => {
      processAndArchiveFile(file, archiveFolderName);
    });

    LogService.log('ArchiveService: Archiving process completed successfully.');
  } catch (error) {
    LogService.error(`ArchiveService: An error occurred during the archiving process. ${error.toString()}`);
  }
}

/**
 * Searches for Excel files in a given folder.
 *
 * @param {GoogleAppsScript.Drive.Folder} folder The folder to search in.
 * @param {string} searchTerm The term to search for in file titles.
 * @returns {Array<GoogleAppsScript.Drive.File>} An array of found Excel files.
 */
function searchExcelFiles(folder, searchTerm) {
  const query = `title contains '${searchTerm}' and (mimeType = '${MimeType.MICROSOFT_EXCEL}' or mimeType = '${MimeType.MICROSOFT_EXCEL_LEGACY}')`;
  const fileIterator = folder.searchFiles(query);
  const files = [];
  while (fileIterator.hasNext()) {
    files.push(fileIterator.next());
  }
  return files;
}

/**
 * Processes a single Excel file: converts it to a Google Sheet and archives it.
 *
 * @param {GoogleAppsScript.Drive.File} file The Excel file to process.
 * @param {string} archiveFolderName The name of the root archive folder.
 * @returns {void}
 */
function processAndArchiveFile(file, archiveFolderName) {
  try {
    // Convert Excel to Google Sheet
    const newSheet = convertExcelToSheet(file);
    if (!newSheet) return;

    // Determine the date of the file
    const fileDate = getFileDate(file.getName()) || new Date();
    
    // Get or create the target archive folder
    const targetFolder = getOrCreateArchiveFolder(archiveFolderName, fileDate);

    // Check for existing files and handle replacement logic
    const existingFiles = targetFolder.getFiles();
    let shouldMoveFile = true;

    if (existingFiles.hasNext()) {
      const existingFile = existingFiles.next();
      const existingFileDate = getFileDate(existingFile.getName()) || existingFile.getDateCreated();

      if (fileDate.getTime() > existingFileDate.getTime()) {
        // New file is newer, remove the old one
        LogService.log(`ArchiveService: Deleting old file: ${existingFile.getName()}`);
        existingFile.setTrashed(true); 
      } else {
        // Existing file is newer or same, discard the new one
        LogService.log(`ArchiveService: Discarding new file as a newer one already exists: ${newSheet.getName()}`);
        DriveApp.getFileById(newSheet.getId()).setTrashed(true);
        shouldMoveFile = false;
      }
    }
    
    if (shouldMoveFile) {
      LogService.log(`ArchiveService: Archiving new file: ${newSheet.getName()} to folder: ${targetFolder.getName()}`);
      DriveApp.getFileById(newSheet.getId()).moveTo(targetFolder);
    }

  } catch (error) {
    LogService.error(`ArchiveService: Failed to process file ${file.getName()}. ${error.toString()}`);
  }
}

/**
 * Converts an Excel file to a Google Sheet.
 *
 * @param {GoogleAppsScript.Drive.File} excelFile The Excel file to convert.
 * @returns {GoogleAppsScript.Drive.File|null} The new Google Sheet file or null if conversion fails.
 */
function convertExcelToSheet(excelFile) {
  try {
    const blob = excelFile.getBlob();
    const newSheetFile = {
      title: excelFile.getName().replace(/\.xlsx?$/, ''),
      parents: [{ id: DriveApp.getRootFolder().getId() }] // Create in root temporarily
    };
    const newFile = Drive.Files.insert(newSheetFile, blob, { convert: true });
    return DriveApp.getFileById(newFile.id);
  } catch (e) {
    LogService.error(`ArchiveService: Could not convert file: ${excelFile.getName()}. Error: ${e.toString()}`);
    return null;
  }
}

/**
 * Gets or creates the archive folder structure: /<root>/<year>/<month>.
 *
 * @param {string} rootFolderName The name of the root archive folder.
 * @param {Date} date The date to use for the folder structure.
 * @returns {GoogleAppsScript.Drive.Folder} The target month folder.
 */
function getOrCreateArchiveFolder(rootFolderName, date) {
  const rootFolder = getOrCreateFolder(DriveApp.getRootFolder(), rootFolderName);
  const year = date.getFullYear().toString();
  const yearFolder = getOrCreateFolder(rootFolder, year);
  const month = ('0' + (date.getMonth() + 1)).slice(-2); // Zero-padded month
  const monthFolder = getOrCreateFolder(yearFolder, month);
  return monthFolder;
}

/**
 * Gets a folder by name within a parent folder, or creates it if it doesn't exist.
 *
 * @param {GoogleAppsScript.Drive.Folder} parentFolder The parent folder.
 * @param {string} folderName The name of the folder to get or create.
 * @returns {GoogleAppsScript.Drive.Folder} The found or created folder.
 */
function getOrCreateFolder(parentFolder, folderName) {
  const folders = parentFolder.getFoldersByName(folderName);
  if (folders.hasNext()) {
    return folders.next();
  }
  return parentFolder.createFolder(folderName);
}

/**
 * Extracts a date from a filename.
 * Supports yyyymmdd, yyyy/mm/dd, yyyy-mm-dd formats.
 *
 * @param {string} filename The name of the file.
 * @returns {Date|null} The extracted date or null if no date is found.
 */
function getFileDate(filename) {
  let match = filename.match(/(\d{4})[\/-]?(\d{2})[\/-]?(\d{2})/);
  if (match) {
    const year = parseInt(match[1], 10);
    const month = parseInt(match[2], 10) - 1; // Month is 0-indexed
    const day = parseInt(match[3], 10);
    // Basic validation for date components
    if (month >= 0 && month < 12 && day > 0 && day <= 31) {
      return new Date(year, month, day);
    }
  }
  return null;
}
