/**
 * @file OrganizerFunctions.gs
 * @brief This file contains the server-side functions for the file organizer modules.
 * @license MIT
 * @version 1.0.0
 */

/**
 * @namespace OrganizerFunctions
 * @brief A collection of functions for file organization tasks.
 */
const OrganizerFunctions = {
  /**
   * Filters files in a specified Google Drive folder based on given criteria.
   *
   * @param {object} config - The module configuration object.
   * @param {string} config.sourceFolderId - The ID of the folder to search in.
   * @param {string} [config.filterFileType] - The type of file to filter (e.g., 'excel', 'csv', 'pdf'). 'any' for all files.
   * @param {string} [config.filterKeyword] - The keyword to search for in filenames.
   * @param {string} [config.filterCondition] - The condition for the keyword ('contains', 'not_contains', 'starts_with').
   * @param {string} [config.moveToFolderId] - The ID of the folder to move the filtered files to.
   * @param {any} lastReturnValue - The value returned from the previous module (not used in this function).
   * @returns {string[]} An array of file IDs that match the criteria.
   */
  filterFiles(config, lastReturnValue) {
    const { sourceFolderId, filterFileType, filterKeyword, filterCondition, moveToFolderId } = config;
    if (!sourceFolderId) {
      throw new Error('Source folder ID is required.');
    }

    const sourceFolder = DriveApp.getFolderById(sourceFolderId);
    let files = sourceFolder.getFiles();
    const filteredFileIds = [];

    const mimeTypeMap = {
      'excel': MimeType.MICROSOFT_EXCEL,
      'csv': MimeType.CSV,
      'pdf': MimeType.PDF,
      'spreadsheet': MimeType.GOOGLE_SHEETS,
    };

    while (files.hasNext()) {
      const file = files.next();
      let match = true;

      // Filter by file type
      if (filterFileType && filterFileType !== 'any') {
        if (file.getMimeType() !== mimeTypeMap[filterFileType]) {
          match = false;
        }
      }

      // Filter by keyword
      if (match && filterKeyword) {
        const fileName = file.getName();
        switch (filterCondition) {
          case 'contains':
            if (!fileName.includes(filterKeyword)) match = false;
            break;
          case 'not_contains':
            if (fileName.includes(filterKeyword)) match = false;
            break;
          case 'starts_with':
            if (!fileName.startsWith(filterKeyword)) match = false;
            break;
          default:
            // If condition is not set, we assume 'contains'
            if (!fileName.includes(filterKeyword)) match = false;
            break;
        }
      }

      if (match) {
        filteredFileIds.push(file.getId());
      }
    }

    // Move files if moveToFolderId is specified
    if (moveToFolderId && filteredFileIds.length > 0) {
      const moveFolder = DriveApp.getFolderById(moveToFolderId);
      filteredFileIds.forEach(fileId => {
        DriveApp.getFileById(fileId).moveTo(moveFolder);
      });
    }

    Logger.log(`Found ${filteredFileIds.length} files matching criteria.`);
    return filteredFileIds;
  },

  /**
   * Converts files to a different format (e.g., Excel to Google Sheets).
   *
   * @param {object} config - The module configuration object.
   * @param {string} config.actionType - The type of conversion ('excel_to_sheet', 'csv_to_sheet').
   * @param {string} [config.outputFolderId] - The destination folder for converted files.
   * @param {string} [config.templateSheetUrl] - The URL of a template spreadsheet for CSV import.
   * @param {string[]} lastReturnValue - An array of file IDs from the previous module.
   * @returns {string[]} An array of IDs of the newly created/modified files.
   */
  convertFiles(config, lastReturnValue) {
    const { actionType, outputFolderId, templateSheetUrl } = config;
    if (!lastReturnValue || !Array.isArray(lastReturnValue) || lastReturnValue.length === 0) {
      Logger.log('No files to convert from previous module.');
      return [];
    }

    const newFileIds = [];

    lastReturnValue.forEach(fileId => {
      const file = DriveApp.getFileById(fileId);
      switch (actionType) {
        case 'excel_to_sheet':
          const folder = outputFolderId ? DriveApp.getFolderById(outputFolderId) : DriveApp.getRootFolder();
          const spreadsheet = Drive.Files.copy({
            title: file.getName().replace(/\.xlsx?/, ''),
            mimeType: MimeType.GOOGLE_SHEETS,
          }, file.getId(), {
            parents: [{ id: folder.getId() }]
          });
          newFileIds.push(spreadsheet.id);
          break;

        case 'csv_to_sheet':
          if (!templateSheetUrl) {
            throw new Error('Template Spreadsheet URL is required for CSV to Sheet conversion.');
          }
          const sheet = SpreadsheetApp.openByUrl(templateSheetUrl);
          const csvData = Utilities.parseCsv(file.getBlob().getDataAsString());
          const targetSheet = sheet.getSheets()[0];
          targetSheet.getRange(1, 1, csvData.length, csvData[0].length).setValues(csvData);
          newFileIds.push(sheet.getId()); // Returns the template sheet ID
          break;

        default:
          Logger.log(`Unsupported conversion action type: ${actionType}`);
          break;
      }
    });

    Logger.log(`Converted ${newFileIds.length} files.`);
    return newFileIds;
  },

  /**
   * Archives files into a structured folder hierarchy based on date.
   *
   * @param {object} config - The module configuration object.
   * @param {string} config.archiveRootFolderId - The root folder for archives.
   * @param {string} [config.archiveFormat] - The folder structure format ('YYYY/MM' or 'YYYY').
   * @param {string} [config.renameKeyword] - A keyword to prepend to archived files.
   * @param {string} [config.overwriteAction] - Action for duplicate filenames ('overwrite', 'skip', 'add_sequence').
   * @param {string[]} lastReturnValue - An array of file IDs from the previous module.
   * @returns {string[]} An array of the archived file IDs.
   */
  archiveFiles(config, lastReturnValue) {
    const { archiveRootFolderId, archiveFormat, renameKeyword, overwriteAction } = config;
    if (!archiveRootFolderId) {
      throw new Error('Archive root folder ID is required.');
    }
    if (!lastReturnValue || !Array.isArray(lastReturnValue) || lastReturnValue.length === 0) {
      Logger.log('No files to archive from previous module.');
      return [];
    }

    const rootFolder = DriveApp.getFolderById(archiveRootFolderId);
    const archivedFileIds = [];

    lastReturnValue.forEach(fileId => {
      const file = DriveApp.getFileById(fileId);
      const date = new Date();
      let targetFolder = rootFolder;

      // Create year/month folders
      if (archiveFormat) {
        const year = date.getFullYear().toString();
        let yearFolder = getOrCreateFolder(targetFolder, year);
        targetFolder = yearFolder;

        if (archiveFormat === 'YYYY/MM') {
          const month = ('0' + (date.getMonth() + 1)).slice(-2);
          let monthFolder = getOrCreateFolder(targetFolder, month);
          targetFolder = monthFolder;
        }
      }

      let newName = file.getName();
      if (renameKeyword) {
        newName = `${renameKeyword}_${newName}`;
      }

      const existingFiles = targetFolder.getFilesByName(newName);
      let shouldMove = true;

      if (existingFiles.hasNext()) {
        switch (overwriteAction) {
          case 'skip':
            shouldMove = false;
            break;
          case 'overwrite':
            while (existingFiles.hasNext()) {
              existingFiles.next().setTrashed(true);
            }
            break;
          case 'add_sequence':
            let i = 1;
            let tempName = newName;
            const parts = newName.split('.');
            const ext = parts.length > 1 ? '.' + parts.pop() : '';
            const baseName = parts.join('.');
            while (targetFolder.getFilesByName(tempName).hasNext()) {
              tempName = `${baseName}_${i}${ext}`;
              i++;
            }
            newName = tempName;
            break;
          default: // Default is skip
             shouldMove = false;
             break;
        }
      }

      if (shouldMove) {
        file.moveTo(targetFolder);
        file.setName(newName);
        archivedFileIds.push(file.getId());
      }
    });

    /**
     * Helper to get a folder by name or create it if it doesn't exist.
     * @param {Folder} parentFolder - The parent folder.
     * @param {string} folderName - The name of the folder to get/create.
     * @returns {Folder} The found or created folder.
     */
    function getOrCreateFolder(parentFolder, folderName) {
      const folders = parentFolder.getFoldersByName(folderName);
      if (folders.hasNext()) {
        return folders.next();
      }
      return parentFolder.createFolder(folderName);
    }

    Logger.log(`Archived ${archivedFileIds.length} files.`);
    return archivedFileIds;
  }
};
