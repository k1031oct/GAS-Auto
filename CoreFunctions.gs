/**
 * @file CoreFunctions.gs
 * @description 新しい実行エンジンで利用される、再利用可能なコア関数を定義します。
 */
var CoreService = {
  // --- ユーティリティ ---

  /**
   * Google DriveのURLからファイル/フォルダIDを抽出します。
   * @param {{url: string}} args - URLを含むオブジェクト。
   * @returns {string} 抽出されたID。
   */
  extractIdFromUrl: function(args) {
    if (!args.url) throw new Error("extractIdFromUrl: 'url' is required.");
    const match = args.url.match(/[-\w]{25,}/);
    if (match) {
      return match[0];
    }
    // URLでない場合、IDが直接渡されたとみなす
    return args.url;
  },

  /**
   * ログを出力します。
   * @param {{message: string}} args - ログメッセージを含むオブジェクト。
   * @returns {string} ログメッセージ。
   */
  log: function(args) {
    console.log(args.message);
    // LogServiceにも記録する（将来的な拡張）
    return args.message;
  },

  /**
   * 文字列を指定された区切り文字で分割し、配列を返します。
   * @param {{text: string, delimiter: string}} args - 分割する文字列と区切り文字。
   * @returns {Array<string>} 分割された文字列の配列。
   */
  splitString: function(args) {
    if (!args.text) return [];
    const delimiter = args.delimiter || ',';
    return args.text.split(delimiter).map(item => item.trim());
  },

  // --- Google Drive ---

  /**
   * IDを使用してDrive上のファイルを取得します。
   * @param {{fileId: string}} args - ファイルIDを含むオブジェクト。
   * @returns {GoogleAppsScript.Drive.File} 取得したファイルオブジェクト。
   */
  driveGetFileById: function(args) {
    if (!args.fileId) throw new Error("driveGetFileById: 'fileId' is required.");
    return DriveApp.getFileById(args.fileId);
  },

  /**
   * IDを使用してDrive上のフォルダを取得します。
   * @param {{folderId: string}} args - フォルダIDを含むオブジェクト。
   * @returns {GoogleAppsScript.Drive.Folder} 取得したフォルダオブジェクト。
   */
  driveGetFolderById: function(args) {
    if (!args.folderId) throw new Error("driveGetFolderById: 'folderId' is required.");
    return DriveApp.getFolderById(args.folderId);
  },

  /**
   * ファイルを別のフォルダに移動します。
   * @param {{file: GoogleAppsScript.Drive.File, folder: GoogleAppsScript.Drive.Folder}} args - ファイルとフォルダオブジェクト。
   * @returns {string} 実行結果メッセージ。
   */
  driveMoveFile: function(args) {
    if (!args.file) throw new Error("driveMoveFile: 'file' object is required.");
    if (!args.folder) throw new Error("driveMoveFile: 'folder' object is required.");
    const fileName = args.file.getName();
    const folderName = args.folder.getName();
    args.file.moveTo(args.folder);
    return `File "${fileName}" moved to "${folderName}".`;
  },

  /**
   * ファイルをコピーします。
   * @param {{file: GoogleAppsScript.Drive.File, folder: GoogleAppsScript.Drive.Folder, newFileName: string}} args
   * @returns {GoogleAppsScript.Drive.File} コピーされた新しいファイルオブジェクト。
   */
  driveCopyFile: function(args) {
    if (!args.file) throw new Error("driveCopyFile: 'file' object is required.");
    if (!args.folder) throw new Error("driveCopyFile: 'folder' object is required.");
    const newFileName = args.newFileName || args.file.getName();
    return args.file.makeCopy(newFileName, args.folder);
  },

  // --- Gmail ---

  /**
   * メールを送信します。
   * @param {{recipient: string, subject: string, body: string}} args - メール情報。
   * @returns {string} 実行結果メッセージ。
   */
  gmailSendEmail: function(args) {
    if (!args.recipient) throw new Error("gmailSendEmail: 'recipient' is required.");
    if (!args.subject) throw new Error("gmailSendEmail: 'subject' is required.");
    if (!args.body) throw new Error("gmailSendEmail: 'body' is required.");
    GmailApp.sendEmail(args.recipient, args.subject, args.body);
    return `Email sent to ${args.recipient}.`;
  },

  // --- Sheets ---

  /**
   * IDを使用してスプレッドシートを開きます。
   * @param {{spreadsheetId: string}} args - スプレッドシートIDを含むオブジェクト。
   * @returns {GoogleAppsScript.Spreadsheet.Spreadsheet} スプレッドシートオブジェクト。
   */
  sheetsOpenById: function(args) {
    if (!args.spreadsheetId) throw new Error("sheetsOpenById: 'spreadsheetId' is required.");
    return SpreadsheetApp.openById(args.spreadsheetId);
  },

  /**
   * スプレッドシートに行を追加します。
   * @param {{spreadsheet: GoogleAppsScript.Spreadsheet.Spreadsheet, sheetName: string, rowData: Array<any>}} args
   * @returns {void}
   */
  sheetsAppendRow: function(args) {
    if (!args.spreadsheet) throw new Error("sheetsAppendRow: 'spreadsheet' object is required.");
    if (!args.rowData) throw new Error("sheetsAppendRow: 'rowData' array is required.");
    const sheet = args.sheetName ? args.spreadsheet.getSheetByName(args.sheetName) : args.spreadsheet.getActiveSheet();
    sheet.appendRow(args.rowData);
  },

  /**
   * スプレッドシートのセルを更新します。
   * @param {{spreadsheet: GoogleAppsScript.Spreadsheet.Spreadsheet, sheetName: string, cell: string, value: any}} args
   * @returns {void}
   */
  sheetsUpdateCell: function(args) {
    if (!args.spreadsheet) throw new Error("sheetsUpdateCell: 'spreadsheet' object is required.");
    if (!args.cell) throw new Error("sheetsUpdateCell: 'cell' (e.g., 'A1') is required.");
    const sheet = args.sheetName ? args.spreadsheet.getSheetByName(args.sheetName) : args.spreadsheet.getActiveSheet();
    sheet.getRange(args.cell).setValue(args.value);
  },

  // --- Docs ---

  /**
   * Googleドキュメントを作成します。
   * @param {{name: string, content: string, folder: GoogleAppsScript.Drive.Folder}} args
   * @returns {GoogleAppsScript.Document.Document} 作成されたドキュメントオブジェクト。
   */
  docsCreateFile: function(args) {
    if (!args.name) throw new Error("docsCreateFile: 'name' is required.");
    const doc = DocumentApp.create(args.name);
    if (args.content) {
      doc.getBody().setText(args.content);
    }
    if (args.folder) {
      const file = DriveApp.getFileById(doc.getId());
      args.folder.addFile(file);
      DriveApp.getRootFolder().removeFile(file); // prevent file from being in root
    }
    return doc;
  },

  // --- Calendar ---

  /**
   * Googleカレンダーにイベントを作成します。
   * @param {{calendarId: string, title: string, startTime: Date, endTime: Date, options: object}} args
   * @returns {GoogleAppsScript.Calendar.CalendarEvent} 作成されたイベントオブジェクト。
   */
  calendarCreateEvent: function(args) {
    if (!args.title || !args.startTime || !args.endTime) {
      throw new Error("calendarCreateEvent: 'title', 'startTime', and 'endTime' are required.");
    }
    const calendarId = args.calendarId || 'primary';
    const calendar = CalendarApp.getCalendarById(calendarId);
    if (!calendar) {
        throw new Error(`Calendar with ID "${calendarId}" not found.`);
    }
    return calendar.createEvent(args.title, new Date(args.startTime), new Date(args.endTime), args.options);
  }
};