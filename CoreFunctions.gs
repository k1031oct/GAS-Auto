/**
 * @file CoreFunctions.gs
 * @description 新しい実行エンジンで利用される、再利用可能なコア関数を定義します。
 */
var CoreService = {
  // --- ユーティリティ ---

  extractIdFromUrl: function(args) {
    if (!args.url) throw new Error("extractIdFromUrl: 'url' is required.");
    const match = args.url.match(/[-\w]{25,}/);
    if (match) return match[0];
    return args.url;
  },

  log: function(args) {
    console.log(args.message);
    return args.message;
  },

  splitString: function(args) {
    if (!args.text) return [];
    const delimiter = args.delimiter || ',';
    return args.text.split(delimiter).map(item => item.trim());
  },

  dateManipulation: function(params) {
      const baseDate = params.baseDate ? new Date(params.baseDate.replace(' ', 'T')) : new Date();
      if (isNaN(baseDate.getTime())) throw new Error(`基準日の形式が無効です: ${params.baseDate}`);
      let resultDate;
      const operation = params.operation || 'add_subtract';
      if (operation === 'add_subtract') {
          const amount = parseInt(params.amount, 10);
          const mode = params.calculationMode || 'add';
          const direction = (mode === 'subtract') ? -1 : 1;
          const unit = params.unit || 'days';
          resultDate = new Date(baseDate.getTime());
          switch (unit) {
              case 'days': resultDate.setDate(resultDate.getDate() + (amount * direction)); break;
              case 'weeks': resultDate.setDate(resultDate.getDate() + (amount * 7 * direction)); break;
              case 'months': resultDate.setMonth(resultDate.getMonth() + (amount * direction)); break;
              case 'years': resultDate.setFullYear(resultDate.getFullYear() + (amount * direction)); break;
              case 'business_days': resultDate = this._addBusinessDays(baseDate, amount * direction); break;
              default: throw new Error(`不明な単位です: ${unit}`);
          }
      } else if (operation === 'find_nth_weekday') {
          const dateForNthWeekday = new Date(baseDate.getTime());
          dateForNthWeekday.setHours(0, 0, 0, 0);
          const targetMonth = dateForNthWeekday.getMonth();
          const targetYear = dateForNthWeekday.getFullYear();
          const n = params.nthWeekdayN;
          const dayOfWeek = parseInt(params.nthWeekdayDay, 10);
          if (n === 'last') {
              const lastDay = new Date(targetYear, targetMonth + 1, 0);
              resultDate = new Date(lastDay.getTime());
              while (resultDate.getDay() !== dayOfWeek) {
                  resultDate.setDate(resultDate.getDate() - 1);
              }
          } else {
              const firstDay = new Date(targetYear, targetMonth, 1);
              resultDate = new Date(firstDay.getTime());
              let count = 0;
              while (resultDate.getMonth() === targetMonth) {
                  if (resultDate.getDay() === dayOfWeek) {
                      count++;
                      if (count == parseInt(n, 10)) break;
                  }
                  resultDate.setDate(resultDate.getDate() + 1);
              }
          }
      } else {
          throw new Error(`不明な操作です: ${operation}`);
      }
      const outputFormat = params.outputFormat || 'yyyy-MM-dd HH:mm:ss';
      return Utilities.formatDate(resultDate, Session.getScriptTimeZone(), outputFormat);
  },

  _addBusinessDays: function(startDate, days) {
      let date = new Date(startDate.getTime());
      const direction = days > 0 ? 1 : -1;
      let daysToAdd = Math.abs(days);
      const startYear = startDate.getFullYear();
      const endYearEstimate = new Date(startDate.getTime() + days * 1.5 * 24 * 60 * 60 * 1000).getFullYear();
      let holidays = [];
      for (let year = Math.min(startYear, endYearEstimate); year <= Math.max(startYear, endYearEstimate); year++) {
          holidays.push(...this._getJapaneseHolidays(year));
      }
      const holidaySet = new Set(holidays.map(h => h.getTime()));
      while (daysToAdd > 0) {
          date.setDate(date.getDate() + direction);
          const day = date.getDay();
          if (day !== 0 && day !== 6 && !holidaySet.has(date.getTime())) {
              daysToAdd--;
          }
      }
      return date;
  },

  _getJapaneseHolidays: function(year) {
      try {
          const calendar = CalendarApp.getCalendarById('ja.japanese#holiday@group.v.calendar.google.com');
          const events = calendar.getEvents(new Date(year, 0, 1), new Date(year, 11, 31));
          return events.map(event => {
              const d = event.getAllDayStartDate();
              d.setHours(0, 0, 0, 0);
              return d;
          });
      } catch (e) {
          Logger.log(`祝日カレンダーの取得に失敗しました: ${e.toString()}`);
          return [];
      }
  },

  // --- Google Drive ---

  driveGetFileById: function(args) {
    if (!args.fileId) throw new Error("driveGetFileById: 'fileId' is required.");
    return DriveApp.getFileById(args.fileId);
  },

  driveGetFolderById: function(args) {
    if (!args.folderId) throw new Error("driveGetFolderById: 'folderId' is required.");
    return DriveApp.getFolderById(args.folderId);
  },

  driveCreateFolder: function(args) {
    if (!args.newFolderName) throw new Error("driveCreateFolder: 'newFolderName' is required.");
    let parentFolder = args.parentFolderId ? DriveApp.getFolderById(args.parentFolderId) : DriveApp.getRootFolder();
    const newFolder = parentFolder.createFolder(args.newFolderName);
    return newFolder.getUrl();
  },

  driveMoveFile: function(args) {
    if (!args.file) throw new Error("driveMoveFile: 'file' object is required.");
    if (!args.folder) throw new Error("driveMoveFile: 'folder' object is required.");
    const fileName = args.file.getName();
    const folderName = args.folder.getName();
    args.file.moveTo(args.folder);
    return `File "${fileName}" moved to "${folderName}".`;
  },

  driveCopyFile: function(args) {
    if (!args.sourceFileId) throw new Error("driveCopyFile: 'sourceFileId' is required.");
    if (!args.destFolderId) throw new Error("driveCopyFile: 'destFolderId' is required.");
    const sourceFile = DriveApp.getFileById(args.sourceFileId);
    const destFolder = DriveApp.getFolderById(args.destFolderId);
    let finalFileName = args.newFileName || sourceFile.getName();
    if (args.fileNameSourceType === 'cell') {
        if (!args.fileNameSourceUrl || !args.fileNameSourceSheet || !args.fileNameSourceCell) throw new Error("File name source from cell is not fully specified.");
        const sheet = SpreadsheetApp.openByUrl(args.fileNameSourceUrl).getSheetByName(args.fileNameSourceSheet);
        if (!sheet) throw new Error(`Sheet "${args.fileNameSourceSheet}" not found.`);
        finalFileName = sheet.getRange(args.fileNameSourceCell).getValue();
    }
    const newFile = sourceFile.makeCopy(finalFileName, destFolder);
    return newFile.getUrl();
  },

  driveListFiles: function(args) {
    if (!args.folderId) throw new Error("driveListFiles: 'folderId' is required.");
    const folder = DriveApp.getFolderById(args.folderId);
    const list = [];
    const files = folder.getFiles();
    while (files.hasNext()) {
      const file = files.next();
      list.push([file.getName(), file.getUrl()]);
    }
    const childFolders = folder.getFolders();
    while (childFolders.hasNext()) {
      const childFolder = childFolders.next();
      list.push([`[Folder] ${childFolder.getName()}`, childFolder.getUrl()]);
    }
    return list;
  },

  driveDeleteFilesByType: function(args) {
    if (!args.folderId) throw new Error("driveDeleteFilesByType: 'folderId' is required.");
    if (!args.mimeType) throw new Error("driveDeleteFilesByType: 'mimeType' is required.");
    const folder = DriveApp.getFolderById(args.folderId);
    const files = folder.getFilesByType(args.mimeType);
    let count = 0;
    while (files.hasNext()) {
      files.next().setTrashed(true);
      count++;
    }
    return `${count}個のファイルをゴミ箱に移動しました。`;
  },

  driveTrashAllFilesinFolder: function(args) {
    if (!args.folderId) throw new Error("driveTrashAllFilesinFolder: 'folderId' is required.");
    const folder = DriveApp.getFolderById(args.folderId);
    const files = folder.getFiles();
    let count = 0;
    while (files.hasNext()) {
      files.next().setTrashed(true);
      count++;
    }
    return `${count}個のファイルをゴミ箱に移動しました。`;
  },

  driveConvertExcelToSheets: function(args) {
    const sourceFolder = DriveApp.getFolderById(args.sourceFolderId);
    const destFolder = DriveApp.getFolderById(args.destFolderId);
    const files = sourceFolder.getFilesByType(MimeType.MICROSOFT_EXCEL);
    if (!files.hasNext()) return null;
    const file = files.next(); // Process only the first file found
    const resource = { title: file.getName().replace(/\.xlsx?$/, ''), parents: [{ id: destFolder.getId() }] };
    const newFile = Drive.Files.insert(resource, file.getBlob(), { convert: true });
    return newFile.alternateLink;
  },

  // --- Gmail ---

  gmailSendEmail: function(args) {
    const options = { cc: args.cc || '', bcc: args.bcc || '' };
    if (args.attachmentFolderId) {
      const folder = DriveApp.getFolderById(args.attachmentFolderId);
      const files = folder.getFiles();
      const attachments = [];
      while (files.hasNext()) { attachments.push(files.next().getBlob()); }
      options.attachments = attachments;
    }
    GmailApp.sendEmail(args.to, args.subject, args.body, options);
    return `Email sent to ${args.to}.`;
  },

  gmailCreateDraft: function(args) {
    const options = { cc: args.cc || '', bcc: args.bcc || '' };
    if (args.attachmentFolderId) {
      const folder = DriveApp.getFolderById(args.attachmentFolderId);
      const files = folder.getFiles();
      const attachments = [];
      while (files.hasNext()) { attachments.push(files.next().getBlob()); }
      options.attachments = attachments;
    }
    GmailApp.createDraft(args.to, args.subject, args.body, options);
    return `Draft created for ${args.to}.`;
  },

  gmailFetchAttachments: function(params) {
    let searchQuery = '';
    if (params.queryMode === 'advanced') {
        searchQuery = params.searchQuery || '';
    } else {
        const queryParts = [];
        if (params.from) queryParts.push(`from:${params.from}`);
        if (params.subject) queryParts.push(`subject:(${params.subject})`);
        if (params.includes) queryParts.push(params.includes);
        if (params['has:attachment']) queryParts.push('has:attachment');
        if (params['is:unread']) queryParts.push('is:unread');
        if (params['is:starred']) queryParts.push('is:starred');
        searchQuery = queryParts.join(' ');
    }

    if (!searchQuery.trim()) {
        return "検索クエリが空のため、処理をスキップしました。";
    }

    const folder = DriveApp.getFolderById(params.destFolderId);
    const threads = GmailApp.search(searchQuery);
    const messages = GmailApp.getMessagesForThreads(threads);
    let count = 0;
    for (var thread of messages) {
      for (var message of thread) {
        if (params.markAsRead) message.markRead();
        if (params.removeStar) message.unstar();
        var attachments = message.getAttachments();
        for (var attachment of attachments) {
          folder.createFile(attachment);
          count++;
        }
      }
    }
    return `${count}個の添付ファイルを取得しました。`;
  },

  // --- Sheets ---

  sheetsOpenById: function(args) {
    if (!args.spreadsheetId) throw new Error("sheetsOpenById: 'spreadsheetId' is required.");
    return SpreadsheetApp.openById(args.spreadsheetId);
  },

  sheetsGetSheet: function(args) {
    if (!args.spreadsheet) throw new Error("sheetsGetSheet: 'spreadsheet' object is required.");
    if (!args.sheetName) return args.spreadsheet.getActiveSheet();
    const sheet = args.spreadsheet.getSheetByName(args.sheetName);
    if (!sheet) throw new Error(`Sheet with name "${args.sheetName}" not found.`);
    return sheet;
  },

  sheetsGetValue: function(args) {
    if (!args.sheet) throw new Error("sheetsGetValue: 'sheet' object is required.");
    if (!args.cell) throw new Error("sheetsGetValue: 'cell' is required.");
    return args.sheet.getRange(args.cell).getValue();
  },

  sheetsSetValues: function(args) {
    if (!args.sheet) throw new Error("sheetsSetValues: 'sheet' object is required.");
    if (!args.values) throw new Error("sheetsSetValues: 'values' (2D array) is required.");
    const startCell = args.startCell || 'A1';
    if (args.values.length === 0) return;
    const range = args.sheet.getRange(startCell).offset(0, 0, args.values.length, args.values[0].length);
    range.setValues(args.values);
  },

  sheetsAppendRow: function(args) {
    if (!args.spreadsheet) throw new Error("sheetsAppendRow: 'spreadsheet' object is required.");
    if (!args.rowData) throw new Error("sheetsAppendRow: 'rowData' array is required.");
    const sheet = args.sheetName ? args.spreadsheet.getSheetByName(args.sheetName) : args.spreadsheet.getActiveSheet();
    sheet.appendRow(args.rowData);
  },

  sheetsUpdateCell: function(args) {
    if (!args.spreadsheet) throw new Error("sheetsUpdateCell: 'spreadsheet' object is required.");
    if (!args.cell) throw new Error("sheetsUpdateCell: 'cell' (e.g., 'A1') is required.");
    const sheet = args.sheetName ? args.spreadsheet.getSheetByName(args.sheetName) : args.spreadsheet.getActiveSheet();
    sheet.getRange(args.cell).setValue(args.value);
  },

  sheetsCreateNew: function(args) {
    let finalFileName = args.newFileName;
    if (args.fileNameSourceType === 'cell') {
        if (!args.fileNameSourceUrl || !args.fileNameSourceSheet || !args.fileNameSourceCell) {
            throw new Error("File name source from cell is not fully specified.");
        }
        const sheet = SpreadsheetApp.openByUrl(args.fileNameSourceUrl).getSheetByName(args.fileNameSourceSheet);
        if (!sheet) throw new Error(`Sheet "${args.fileNameSourceSheet}" not found.`);
        finalFileName = sheet.getRange(args.fileNameSourceCell).getValue();
    }
    if (!finalFileName) throw new Error("File name could not be determined.");

    const newSs = SpreadsheetApp.create(finalFileName);
    const newFile = DriveApp.getFileById(newSs.getId());
    if (args.destFolderId) {
      const destFolder = DriveApp.getFolderById(args.destFolderId);
      newFile.moveTo(destFolder);
    }
    return newFile.getUrl();
  },

  sheetsToPdf: function(params) {
    const ss = SpreadsheetApp.openByUrl(params.spreadsheetUrl);
    const sheet = ss.getSheetByName(params.sheetName);
    if (!sheet) throw new Error(`シート「${params.sheetName}」が見つかりません。`);
    const sheetId = sheet.getSheetId();
    const url = `https://docs.google.com/spreadsheets/d/${ss.getId()}/export?`;
    let options = 'exportFormat=pdf&format=pdf&gid=' + sheetId;
    if (params.pdfSettingsMode === 'advanced') {
        options += '&portrait=' + (params.pdfOrientation || 'false') + '&size=' + (params.pdfSize || 'A4') + '&scale=' + (params.pdfScale || '4') + '&printtitle=' + (params.pdfPrintTitle || false) + '&sheetnames=' + (params.pdfSheetNames || false) + '&gridlines=' + (params.pdfGridlines || false);
        if (params.pdfRange) options += '&range=' + encodeURIComponent(params.pdfRange);
    } else {
        options += '&portrait=true&size=A4&fitw=true&gridlines=false';
    }
    const response = UrlFetchApp.fetch(url + options, { headers: { 'Authorization': 'Bearer ' + ScriptApp.getOAuthToken() } });
    const blob = response.getBlob().setName(`${params.newFileName}.pdf`);
    const pdfFile = DriveApp.getFolderById(params.destFolderId).createFile(blob);
    return pdfFile.getUrl();
  },

  sheetsToExcel: function(params) {
    const ssId = this.extractIdFromUrl({url: params.spreadsheetUrl});
    const url = `https://docs.google.com/spreadsheets/d/${ssId}/export?format=xlsx`;
    const response = UrlFetchApp.fetch(url, { headers: { 'Authorization': 'Bearer ' + ScriptApp.getOAuthToken() } });
    const blob = response.getBlob().setName(`${params.newFileName}.xlsx`);
    const excelFile = DriveApp.getFolderById(params.destFolderId).createFile(blob);
    return excelFile.getUrl();
  },

  sheetsCopyValues: function(params) {
    const sourceSheet = SpreadsheetApp.openByUrl(params.sourceUrl).getSheetByName(params.sourceSheet);
    const targetSheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheet);
    const sourceRange = sourceSheet.getRange(params.sourceRange);
    const targetCell = targetSheet.getRange(params.targetStartCell);
    if (params.copyValues) sourceRange.copyTo(targetCell, {contentsOnly: true});
    if (params.copyFormats) sourceRange.copyTo(targetCell, {formatOnly: true});
    return `Copied from ${params.sourceRange} to ${params.targetStartCell}`;
  },

  sheetsAppendBlock: function(params) {
    const values = JSON.parse(params.jsonData || '[]');
    const sheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheet);
    const refColumn = sheet.getRange(`${params.referenceColumn}1`);
    const lastRow = sheet.getRange(sheet.getMaxRows(), refColumn.getColumn()).getNextDataCell(SpreadsheetApp.Direction.UP).getRow();
    sheet.getRange(lastRow + 1, sheet.getRange(`${params.pasteStartColumn}1`).getColumn(), values.length, values[0].length).setValues(values);
    return `Appended data to ${params.targetSheet}`;
  },

  sheetsClearValues: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    sheet.getRange(args.targetRange).clearContent();
    return `Cleared range ${args.targetRange}`;
  },

  sheetsSetValue: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    const range = sheet.getRange(args.targetCell);
    if (String(args.valueToSet).startsWith('=')) range.setFormula(args.valueToSet); else range.setValue(args.valueToSet);
    return `Set value in ${args.targetCell}`;
  },

  sheetsImportCsv: function(params) {
    const folder = DriveApp.getFolderById(params.folderId);
    const files = folder.getFilesByType(MimeType.CSV);
    if (!files.hasNext()) return "No CSV file found";
    const file = files.next();
    const csvData = file.getBlob().getDataAsString(params.charset || 'UTF-8');
    const values = Utilities.parseCsv(csvData);
    const sheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheet);
    sheet.getRange(1, 1, values.length, values[0].length).setValues(values);
    return `Imported CSV data from ${file.getName()}`;
  },

  sheetsInsertImage: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    const blob = DriveApp.getFileById(args.fileId).getBlob();
    const img = SpreadsheetApp.newCellImage().setSourceUrl(`data:${blob.getContentType()};base64,${Utilities.base64Encode(blob.getBytes())}`).build();
    sheet.getRange(args.targetCell).setValue(img);
    return `Image inserted into ${args.targetCell}`;
  },

  sheetsInsertRows: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    if(args.after) sheet.insertRowsAfter(parseInt(args.startRow), parseInt(args.numRows)); else sheet.insertRowsBefore(parseInt(args.startRow), parseInt(args.numRows));
    return `${args.numRows} rows inserted.`;
  },

  sheetsDeleteRows: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    sheet.deleteRows(parseInt(args.startRow), parseInt(args.numRows));
    return `${args.numRows} rows deleted.`;
  },

  sheetsInsertCols: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    if(args.after) sheet.insertColumnsAfter(parseInt(args.startCol), parseInt(args.numCols)); else sheet.insertColumnsBefore(parseInt(args.startCol), parseInt(args.numCols));
    return `${args.numCols} columns inserted.`;
  },

  sheetsDeleteCols: function(args) {
    const sheet = SpreadsheetApp.openByUrl(args.targetUrl).getSheetByName(args.targetSheet);
    sheet.deleteColumns(parseInt(args.startCol), parseInt(args.numCols));
    return `${args.numCols} columns deleted.`;
  },

  sheetsHideRowsByValue: function(params) {
    const sheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheet);
    const values = sheet.getRange(params.column + "1:" + params.column + sheet.getLastRow()).getValues();
    let count = 0;
    for (var i = 0; i < values.length; i++) {
      if (values[i][0] == params.valueToHide) {
        sheet.hideRows(i + 1);
        count++;
      }
    }
    return `${count} rows hidden.`;
  },

  sheetsUnhideRows: function(params) {
    const sheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheet);
    sheet.unhideRow(sheet.getRange(params.targetRange));
    return `Rows in range ${params.targetRange} unhidden.`;
  },

  sheetsGetMonthlyData: function(params) {
    const rootFolder = DriveApp.getFolderById(params.rootFolderId);
    const yearFolders = rootFolder.getFoldersByName(String(params.targetYear));
    if (!yearFolders.hasNext()) throw new Error(`Year folder not found.`);
    const monthFolders = yearFolders.next().getFoldersByName(String(params.targetMonth).padStart(2, '0'));
    if (!monthFolders.hasNext()) throw new Error(`Month folder not found.`);
    const files = monthFolders.next().getFilesByType(MimeType.GOOGLE_SHEETS);
    let targetFile, latestUpdateTime = new Date(0);
    while (files.hasNext()) {
        const file = files.next();
        if (file.getLastUpdated() > latestUpdateTime) {
            latestUpdateTime = file.getLastUpdated();
            targetFile = file;
        }
    }
    if (!targetFile) throw new Error(`No spreadsheet found.`);
    const sourceSheet = SpreadsheetApp.openById(targetFile.getId()).getSheetByName(params.sourceSheetName);
    const data = sourceSheet.getRange(params.sourceRangeNotation).getValues();
    const destinationSheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheetName);
    destinationSheet.getRange(1, 1, data.length, data[0].length).setValues(data);
    return `Copied data from ${targetFile.getName()}`;
  },

  // --- Docs ---

  docsCreateFile: function(args) {
    if (!args.name) throw new Error("docsCreateFile: 'name' is required.");
    const doc = DocumentApp.create(args.name);
    if (args.content) doc.getBody().setText(args.content);
    if (args.folder) {
      const file = DriveApp.getFileById(doc.getId());
      args.folder.addFile(file);
      DriveApp.getRootFolder().removeFile(file);
    }
    return doc;
  },

  // --- Calendar ---

  calendarCreateEvent: function(args) {
    if (!args.title || !args.startTime || !args.endTime) throw new Error("calendarCreateEvent: 'title', 'startTime', and 'endTime' are required.");
    const calendar = CalendarApp.getCalendarById(args.calendarId || 'primary');
    if (!calendar) throw new Error(`Calendar with ID "${args.calendarId}" not found.`);
    return calendar.createEvent(args.title, new Date(args.startTime), new Date(args.endTime), args.options);
  },

  calendarGetHolidays: function(params) {
    const events = CalendarApp.getCalendarById('ja.japanese#holiday@group.v.calendar.google.com').getEvents(new Date(params.startDate), new Date(params.endDate));
    const holidayList = events.map((event, i) => [(i + 1), event.getAllDayStartDate(), event.getTitle()]);
    const sheet = SpreadsheetApp.openByUrl(params.targetUrl).getSheetByName(params.targetSheet);
    sheet.getRange(2, 1, holidayList.length, 3).setValues(holidayList);
    return `${holidayList.length} holidays written.`;
  },

  // --- Other Services ---

  slidesRefreshCharts: function(params) {
    const presentation = SlidesApp.openById(this.extractIdFromUrl({url: params.slidesUrl}));
    const charts = presentation.getSlides().flatMap(slide => slide.getSheetsCharts());
    charts.forEach(chart => chart.refresh());
    return `${charts.length} charts refreshed.`;
  },

  chatPostMessage: function(params) {
    const message = { 'text': params.message };
    const options = { 'method': 'POST', 'contentType': 'application/json; charset=UTF-8', 'payload': JSON.stringify(message) };
    UrlFetchApp.fetch(params.webhookUrl, options);
    return 'Message posted to Chat.';
  },

  createOneTimeTrigger: function(args) {
    const workflowName = args.workflowToRun;
    if (!workflowName) throw new Error("Workflow name is not specified.");

    let executionTimeValue;
    const source = args.executionTimeSource || 'manual';

    switch (source) {
        case 'manual':
            executionTimeValue = args.executionTimeManual;
            break;
        case 'fromCell':
            if (!args.sourceUrl || !args.sourceSheet || !args.sourceCell) throw new Error("Cell information for execution time is missing.");
            const sheet = SpreadsheetApp.openByUrl(args.sourceUrl).getSheetByName(args.sourceSheet);
            if (!sheet) throw new Error(`Sheet "${args.sourceSheet}" not found.`);
            executionTimeValue = sheet.getRange(args.sourceCell).getValue();
            break;
        case 'fromModuleResult':
            executionTimeValue = args.executionTimeFromModule; // This value is pre-resolved by ExecutionService
            break;
        default:
            throw new Error(`Unknown execution time source: ${source}`);
    }

    if (!executionTimeValue) throw new Error("Execution time could not be determined.");
    const executionDate = new Date(executionTimeValue);
    if (isNaN(executionDate.getTime())) throw new Error(`Invalid date format: ${executionTimeValue}`);
    if (executionDate.getTime() <= new Date().getTime()) throw new Error("Execution time must be in the future.");

    return TriggerService.createOneTimeTrigger(workflowName, executionDate);
  }
};