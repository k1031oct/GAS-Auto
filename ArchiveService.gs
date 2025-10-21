var ArchiveService = {
  archiveExcelFilesHandler: function(settings) {
    const { sourceFolder, searchTerm, archiveFolderName } = settings;

    if (!sourceFolder || !searchTerm || !archiveFolderName) {
      LogService.error('ArchiveService: Missing required settings.');
      return;
    }

    try {
      const sourceFolder = this.getFolderFromIdOrUrl(sourceFolder);
      if (!sourceFolder) {
        LogService.error(`ArchiveService: Could not find folder with ID or URL: ${sourceFolder}`);
        return;
      }
      const excelFiles = this.searchExcelFiles(sourceFolder, searchTerm);

      excelFiles.forEach(file => {
        this.processAndArchiveFile(file, archiveFolderName);
      });

      LogService.log('ArchiveService: Archiving process completed successfully.');
    } catch (error) {
      LogService.error(`ArchiveService: An error occurred during the archiving process. ${error.toString()}`);
    }
  },

  searchExcelFiles: function(folder, searchTerm) {
    const query = `title contains '${searchTerm}' and (mimeType = '${MimeType.MICROSOFT_EXCEL}' or mimeType = '${MimeType.MICROSOFT_EXCEL_LEGACY}')`;
    const fileIterator = folder.searchFiles(query);
    const files = [];
    while (fileIterator.hasNext()) {
      files.push(fileIterator.next());
    }
    return files;
  },

  processAndArchiveFile: function(file, archiveFolderName) {
    try {
      // Convert Excel to Google Sheet
      const newSheet = this.convertExcelToSheet(file);
      if (!newSheet) return;

      // Determine the date of the file
      const fileDate = this.getFileDate(file.getName()) || file.getLastUpdated();
      
      // Get or create the target archive folder
      const targetFolder = this.getOrCreateArchiveFolder(archiveFolderName, fileDate);

      // Check for existing file with the same name
      const newSheetName = newSheet.getName();
      const existingFiles = targetFolder.getFilesByName(newSheetName);
      let shouldMoveFile = true;

      if (existingFiles.hasNext()) {
        const existingFile = existingFiles.next();
        const existingFileDate = this.getFileDate(existingFile.getName()) || existingFile.getLastUpdated();

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
  },

  convertExcelToSheet: function(excelFile) {
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
  },

  getOrCreateArchiveFolder: function(rootFolderName, date) {
    const rootFolder = this.getOrCreateFolder(DriveApp.getRootFolder(), rootFolderName);
    const year = date.getFullYear().toString();
    const yearFolder = this.getOrCreateFolder(rootFolder, year);
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // Zero-padded month
    const monthFolder = this.getOrCreateFolder(yearFolder, month);
    return monthFolder;
  },

  getOrCreateFolder: function(parentFolder, folderName) {
    const folders = parentFolder.getFoldersByName(folderName);
    if (folders.hasNext()) {
      return folders.next();
    }
    return parentFolder.createFolder(folderName);
  },

  getFileDate: function(filename) {
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
  },

  getFolderFromIdOrUrl: function(idOrUrl) {
    if (!idOrUrl) return null;

    let folderId = idOrUrl;
    
    // Check if it's a URL and extract the ID
    if (idOrUrl.startsWith('http')) {
      const match = idOrUrl.match(/folders\/([a-zA-Z0-9_-]+)/);
      if (match && match[1]) {
        folderId = match[1];
      } else {
        LogService.error(`ArchiveService: Invalid Google Drive folder URL: ${idOrUrl}`);
        return null;
      }
    }
    
    try {
      return DriveApp.getFolderById(folderId);
    } catch (e) {
      LogService.error(`ArchiveService: Could not access folder with ID: ${folderId}. Error: ${e.toString()}`);
      return null;
    }
  }
};
