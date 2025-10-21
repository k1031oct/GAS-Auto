/**
 * @file FileOperationService.gs
 * @description ファイルの絞り込み、変換、アーカイブなどの操作を提供するサービス。
 */

var FileOperationService = {

  /**
   * ファイルを絞り込み、指定のフォルダへ移動する。
   * @param {object} settings - モジュールの設定
   * @param {GoogleAppsScript.Drive.File[]} inputFiles - 前のモジュールからの入力ファイル
   * @param {object} context - 実行コンテキスト (ログ記録など)
   * @returns {GoogleAppsScript.Drive.File[]} 処理されたファイル
   */
  sortFiles: function(settings, inputFiles, context) {
    const addLog = (message, type = 'info') => context.logs.push({ message, type });
    let filesToProcess = [...inputFiles];

    // 役割: 文字列を正規化し、比較可能な状態に整形する
    const sanitizeString = (str) => {
      if (typeof str !== 'string') return '';
      return str.normalize('NFKC').trim().toLowerCase();
    };

    if (settings.filterFileType && settings.filterFileType !== 'any') {
      const mimeTypes = { 'excel': [MimeType.MICROSOFT_EXCEL, MimeType.MICROSOFT_EXCEL_LEGACY], 'csv': [MimeType.CSV], 'pdf': [MimeType.PDF] };
      if (mimeTypes[settings.filterFileType]) {
        filesToProcess = filesToProcess.filter(file => mimeTypes[settings.filterFileType].includes(file.getMimeType()));
      }
    }
    if (settings.filterKeyword) {
      const keyword = sanitizeString(settings.filterKeyword);
      const condition = settings.filterCondition;

      if (keyword) {
        filesToProcess = filesToProcess.filter(file => {
          const fileName = sanitizeString(file.getName());
          switch (condition) {
            case 'contains': return fileName.includes(keyword);
            case 'not_contains': return !fileName.includes(keyword);
            case 'starts_with': return fileName.startsWith(keyword);
            case 'ends_with': return fileName.endsWith(keyword);
            default: return false;
          }
        });
      }
    }

    addLog(`${filesToProcess.length} 件のファイルが絞り込み条件に一致しました。`);
    if (filesToProcess.length === 0) return [];

    const destFolder = this._getFolderFromIdOrUrl(settings.destFolderId);
    if (!destFolder) {
      addLog(`移動先フォルダが見つかりません: ${settings.destFolderId}`, 'error');
      throw new Error(`移動先フォルダが見つかりません: ${settings.destFolderId}`);
    }

    const movedFiles = [];
    filesToProcess.forEach(file => {
      const logMsg = `ファイル「${file.getName()}」を「${destFolder.getName()}」へ移動します。`;
      addLog(context.isDryRun ? `(テスト) ${logMsg}` : logMsg);
      if (!context.isDryRun) {
        // ファイルが既に移動先フォルダにある場合はスキップ
        let isAlreadyInDest = false;
        try {
          const parents = file.getParents();
          while(parents.hasNext()) {
            if (parents.next().getId() === destFolder.getId()) {
              isAlreadyInDest = true;
              break;
            }
          }
        } catch (e) {
          // getParents()が失敗する場合があるため
          addLog(`ファイル「${file.getName()}」の親フォルダ確認中にエラー: ${e.message}`, 'warn');
        }

        if (!isAlreadyInDest) {
          file.moveTo(destFolder);
          movedFiles.push(file);
        } else {
          addLog(`ファイル「${file.getName()}」は既に移動先フォルダに存在します。`, 'info');
          movedFiles.push(file); // 既に存在する場合も処理済みとして含める
        }
      } else {
        movedFiles.push(file); // ドライラン時は常に含める
      }
    });
    return movedFiles;
  },

  /**
   * ファイル形式を変換する。
   * @param {object} settings - モジュールの設定
   * @param {GoogleAppsScript.Drive.File[]} inputFiles - 前のモジュールからの入力ファイル
   * @param {object} context - 実行コンテキスト (ログ記録など)
   * @returns {GoogleAppsScript.Drive.File[]} 変換されたファイル
   */
  convertFiles: function(settings, inputFiles, context) {
    const addLog = (message, type = 'info') => context.logs.push({ message, type });
    if (inputFiles.length === 0) {
      addLog("処理対象ファイルがないためスキップします。");
      return [];
    }
    const outputResults = [];

    if (settings.actionType === 'excel_to_sheet') {
      const destFolder = this._getFolderFromIdOrUrl(settings.outputFolderId);
      if (!destFolder) {
        addLog(`出力先フォルダが見つかりません: ${settings.outputFolderId}`, 'error');
        throw new Error(`出力先フォルダが見つかりません: ${settings.outputFolderId}`);
      }
      inputFiles.forEach(file => {
        if (file.getMimeType() !== MimeType.MICROSOFT_EXCEL && file.getMimeType() !== MimeType.MICROSOFT_EXCEL_LEGACY) {
          addLog(`ファイル「${file.getName()}」はExcelファイルではないためスキップします。`, 'warn');
          return;
        }
        const newName = file.getName().replace(/\.xlsx?$/, '');
        const logMsg = `Excel「${file.getName()}」をスプレッドシートに変換し、「${destFolder.getName()}」に保存します。`;
        addLog(context.isDryRun ? `(テスト) ${logMsg}` : logMsg);
        if (!context.isDryRun) {
          try {
            const resource = { title: newName, parents: [{ id: destFolder.getId() }] };
            const newFile = Drive.Files.insert(resource, file.getBlob(), { convert: true });
            outputResults.push(DriveApp.getFileById(newFile.id));
            file.setTrashed(true);
            addLog(`処理済みの元ファイル「${file.getName()}」をゴミ箱に移動しました。`);
          } catch (e) {
            addLog(`Excelファイル「${file.getName()}」の変換中にエラー: ${e.message}`, 'error');
          }
        } else {
          outputResults.push({ getName: () => newName, getId: () => 'dummy_converted_file_id', getLastUpdated: () => new Date() });
        }
      });
      return outputResults;
    } else if (settings.actionType === 'csv_to_sheet') {
      const templateSheet = this._getSpreadsheetFromIdOrUrl(settings.templateSheetId);
      if (!templateSheet) {
        addLog(`テンプレートシートが見つかりません: ${settings.templateSheetId}`, 'error');
        throw new Error(`テンプレートシートが見つかりません: ${settings.templateSheetId}`);
      }

      const csvFiles = inputFiles.filter(f => f.getMimeType() === MimeType.CSV);
      if (csvFiles.length === 0) {
        addLog("CSVファイルが見つからないためスキップします。", 'warn');
        return [];
      }
      const latestFile = csvFiles.sort((a, b) => b.getLastUpdated().getTime() - a.getLastUpdated().getTime())[0];
      const logMsg = `最新CSV「${latestFile.getName()}」をシート(ID: ${settings.templateSheetId})に読み込みます。`;
      addLog(context.isDryRun ? `(テスト) ${logMsg}` : logMsg);

      if (!context.isDryRun) {
        try {
          const csvData = Utilities.parseCsv(latestFile.getBlob().getDataAsString('UTF-8'));
          const sheet = templateSheet.getSheets()[0];
          sheet.clearContents();
          if (csvData.length > 0) sheet.getRange(1, 1, csvData.length, csvData[0].length).setValues(csvData);
          latestFile.setTrashed(true);
          addLog(`処理済みの元CSVファイル「${latestFile.getName()}」をゴミ箱に移動しました。`);
          outputResults.push(templateSheet); // 変換結果としてシートオブジェクトを返す
        } catch (e) {
          addLog(`CSVファイル「${latestFile.getName()}」のシートへの読み込み中にエラー: ${e.message}`, 'error');
        }
      } else {
        outputResults.push(templateSheet); // ドライラン時はダミーを返す
      }
      return outputResults;
    }
    return inputFiles; // 変換タイプが一致しない場合はそのまま返す
  },

  /**
   * ファイルをアーカイブフォルダに整理・保管する。
   * @param {object} settings - モジュールの設定
   * @param {GoogleAppsScript.Drive.File[]} inputFiles - 前のモジュールからの入力ファイル
   * @param {object} context - 実行コンテキスト (ログ記録など)
   * @returns {GoogleAppsScript.Drive.File[]} アーカイブされたファイル
   */
  archiveFiles: function(settings, inputFiles, context) {
    const addLog = (message, type = 'info') => context.logs.push({ message, type });
    if (inputFiles.length === 0) {
      addLog("アーカイブ対象ファイルがないためスキップします。");
      return [];
    }

    const archiveRootFolder = this._getFolderFromIdOrUrl(settings.archiveRootFolderId);
    if (!archiveRootFolder) {
      addLog(`アーカイブ先ルートフォルダが見つかりません: ${settings.archiveRootFolderId}`, 'error');
      throw new Error(`アーカイブ先ルートフォルダが見つかりません: ${settings.archiveRootFolderId}`);
    }

    const archivedFiles = [];

    inputFiles.forEach(file => {
      const fileToProcess = file; // inputFilesは既にFileオブジェクト

      let targetFolder = archiveRootFolder;
      const fileDate = this._extractDateFromFile(fileToProcess);
      if (!fileDate) {
        addLog(`ファイル「${fileToProcess.getName()}」から日付を抽出できませんでした。アーカイブをスキップします。`, 'warn');
        return;
      }

      const year = fileDate.getFullYear().toString();
      const month = (fileDate.getMonth() + 1).toString().padStart(2, '0');

      const yearFolder = this._getOrCreateFolder(archiveRootFolder, year, context.isDryRun);

      if (settings.archiveFormat === 'YYYY/MM') {
        targetFolder = this._getOrCreateFolder(yearFolder, month, context.isDryRun);
      } else {
        targetFolder = yearFolder;
      }

      let finalName = fileToProcess.getName();
      let keywordPrefix = '';
      if (settings.renameKeyword) {
        const keyword = settings.renameKeyword;
        const yyyy = fileDate.getFullYear();
        const mm = (fileDate.getMonth() + 1).toString().padStart(2, '0');
        const dd = fileDate.getDate().toString().padStart(2, '0');
        finalName = `【${keyword}】${yyyy}${mm}${dd}` + fileToProcess.getName().substring(fileToProcess.getName().lastIndexOf('.'));
        keywordPrefix = `【${keyword}】`;
      }

      if (keywordPrefix) {
        const filesInFolder = targetFolder.getFiles();
        let latestExistingFile = null;
        let latestDateStr = '';
        const oldFilesToDelete = [];

        while (filesInFolder.hasNext()) {
          const existingFile = filesInFolder.next();
          const existingFileName = existingFile.getName();
          if (existingFileName.startsWith(keywordPrefix)) {
            oldFilesToDelete.push(existingFile);
            const match = existingFileName.match(/\d{8}/);
            if (match) {
              if (match[0] > latestDateStr) {
                latestDateStr = match[0];
                latestExistingFile = existingFile;
              }
            }
          }
        }

        const newFileDateStrMatch = finalName.match(/\d{8}/);
        const newFileDateStr = newFileDateStrMatch ? newFileDateStrMatch[0] : null;

        if (latestExistingFile && newFileDateStr && newFileDateStr <= latestDateStr) {
          addLog(`ファイル「${finalName}」より新しい、または同じ日付のファイル「${latestExistingFile.getName()}」が既に存在するためスキップします。`);
          if (!context.isDryRun) {
            fileToProcess.setTrashed(true);
            addLog(`処理不要な元ファイル「${fileToProcess.getName()}」をゴミ箱に移動しました。`);
          }
          return; // このファイルをスキップ
        }

        if (oldFilesToDelete.length > 0) {
          addLog(`${oldFilesToDelete.length}件の古いバージョンのファイルを削除します。`);
          if (!context.isDryRun) {
            oldFilesToDelete.forEach(f => f.setTrashed(true));
          }
        }
      }

      const logMsg = `ファイル「${fileToProcess.getName()}」を「${targetFolder.getName()}」へ「${finalName}」としてコピーします。`;
      addLog(context.isDryRun ? `(テスト) ${logMsg}` : logMsg);

      if (!context.isDryRun) {
        const newFile = fileToProcess.makeCopy(finalName, targetFolder);
        archivedFiles.push(newFile);

        // 元ファイルをゴミ箱に移動
        fileToProcess.setTrashed(true);
        addLog(`処理済みの元ファイル「${fileToProcess.getName()}」をゴミ箱に移動しました。`);
      }
    });
    return archivedFiles;
  },

  // ヘルパー関数
  _getFolderFromIdOrUrl: function(idOrUrl) {
    if (!idOrUrl) return null;
    let folderId = idOrUrl;
    if (idOrUrl.startsWith('http')) {
      const match = idOrUrl.match(/folders\/([a-zA-Z0-9_-]+)/);
      if (match && match[1]) {
        folderId = match[1];
      } else {
        LogService.error(`FileOperationService: Invalid Google Drive folder URL: ${idOrUrl}`);
        return null;
      }
    }
    try {
      return DriveApp.getFolderById(folderId);
    } catch (e) {
      LogService.error(`FileOperationService: Could not access folder with ID: ${folderId}. Error: ${e.message}`);
      return null;
    }
  },

  _getSpreadsheetFromIdOrUrl: function(idOrUrl) {
    if (!idOrUrl) return null;
    let ssId = idOrUrl;
    if (idOrUrl.startsWith('http')) {
      const match = idOrUrl.match(/spreadsheets\/d\/([a-zA-Z0-9_-]+)/);
      if (match && match[1]) {
        ssId = match[1];
      } else {
        LogService.error(`FileOperationService: Invalid Google Spreadsheet URL: ${idOrUrl}`);
        return null;
      }
    }
    try {
      return SpreadsheetApp.openById(ssId);
    } catch (e) {
      LogService.error(`FileOperationService: Could not access spreadsheet with ID: ${ssId}. Error: ${e.message}`);
      return null;
    }
  },

  _getOrCreateFolder: function(parentFolder, folderName, isDryRun) {
    const folders = parentFolder.getFoldersByName(folderName);
    if (folders.hasNext()) {
      return folders.next();
    }
    return isDryRun ? parentFolder : parentFolder.createFolder(folderName);
  },

  _isValidDate: function(year, month, day) {
    const y = parseInt(year, 10), m = parseInt(month, 10) - 1, d = parseInt(day, 10);
    const date = new Date(y, m, d);
    return date.getFullYear() === y && date.getMonth() === m && date.getDate() === d;
  },

  _extractDateFromFile: function(file) {
    if (typeof file.getName !== 'function') return new Date();
    const fileName = file.getName();
    let match = fileName.match(/(20\d{2})[-_]?(\d{2})[-_]?(\d{2})/);
    if (match && this._isValidDate(match[1], match[2], match[3])) {
      return new Date(parseInt(match[1], 10), parseInt(match[2], 10) - 1, parseInt(match[3], 10));
    }
    match = fileName.match(/(20\d{2})[-_]?(\d{2})/);
    if (match) {
      const year = parseInt(match[1], 10);
      const month = parseInt(match[2], 10);
      const lastDay = new Date(year, month, 0).getDate();
      if (this._isValidDate(year, month, lastDay)) {
        return new Date(year, month - 1, lastDay);
      }
    }
    if (typeof file.getLastUpdated !== 'function') return new Date();
    return file.getLastUpdated();
  }
};
