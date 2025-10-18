/**
 * @file OrganizerService.gs
 * @description Provides file organization functionalities like filtering, converting, and archiving for workflows.
 */
var OrganizerService = {

  /**
   * Helper function to get a folder by name, or create it if it doesn't exist.
   * @private
   */
  _getOrCreateFolder: function(parentFolder, folderName) {
    const folders = parentFolder.getFoldersByName(folderName);
    if (folders.hasNext()) {
      return folders.next();
    } else {
      return parentFolder.createFolder(folderName);
    }
  },

  /**
   * Filters files in a specified Google Drive folder based on various criteria.
   */
  filterFiles: function(configs, fileIds) {
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

      if (filterFileType && filterFileType !== 'any') {
        const mimeType = file.getMimeType();
        if (mimeType !== filterFileType) { // Simplified, assuming direct mime types can be passed
          match = false;
        }
      }

      if (match && filterKeyword) {
        const fileName = file.getName();
        switch (filterCondition) {
          case 'contains':
            if (!fileName.includes(filterKeyword)) match = false;
            break;
          case 'does_not_contain':
            if (fileName.includes(filterKeyword)) match = false;
            break;
          case 'starts_with':
            if (!fileName.startsWith(filterKeyword)) match = false;
            break;
          case 'ends_with':
            if (!fileName.endsWith(filterKeyword)) match = false;
            break;
        }
      }

      if (match) {
        filteredFileIds.push(file.getId());
      }
    }

    if (moveToFolderId && filteredFileIds.length > 0) {
      const moveFolder = DriveApp.getFolderById(moveToFolderId);
      filteredFileIds.forEach(fileId => {
        DriveApp.getFileById(fileId).moveTo(moveFolder);
      });
    }

    return filteredFileIds;
  },

  /**
   * Converts files to specified Google Workspace formats.
   */
  convertFiles: function(configs, fileIds) {
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
          const sheet = spreadsheet.getSheets()[0];
          const csvData = Utilities.parseCsv(blob.getDataAsString());
          sheet.getRange(1, 1, csvData.length, csvData[0].length).setValues(csvData);
          convertedFileIds.push(spreadsheet.getId());
          break;
      }
    });

    return convertedFileIds;
  },

  /**
   * Archives files into a specified folder structure.
   */
  archiveFiles: function(configs, fileIds) {
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

      if (archiveFormat) {
        const year = now.getFullYear().toString();
        const month = ('0' + (now.getMonth() + 1)).slice(-2);
        
        if (archiveFormat.includes('YYYY')) {
          targetFolder = this._getOrCreateFolder(targetFolder, year);
        }
        if (archiveFormat.includes('MM')) {
          targetFolder = this._getOrCreateFolder(targetFolder, month);
        }
      }

      let newName = file.getName();
      if (renameKeyword) {
        newName = `${renameKeyword}_${newName}`;
      }

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
};