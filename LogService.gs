/**
 * @file LogService.gs
 * @description ワークフローの実行履歴をスプレッドシートに記録・管理する。
 */
var LogService = {
  _APP_FOLDER_NAME: 'GAS_Workflow_Automator_AppData',
  _LOG_FILE_NAME: 'Automator_ExecutionLogs',
  _SUMMARY_SHEET_NAME: 'Summary',
  _DETAIL_SHEET_NAME: 'Detail',
  _LOG_FILE_ID_KEY: 'AUTOMATOR_APP_LOG_FILE_ID',
  _workflowNames: {},

  _getLogSpreadsheet: function () {
    const userProperties = PropertiesService.getUserProperties();
    let fileId = userProperties.getProperty(this._LOG_FILE_ID_KEY);
    if (fileId) {
      try {
        const file = DriveApp.getFileById(fileId);
        if (!file.isTrashed()) {
          return SpreadsheetApp.openById(fileId);
        }
      } catch (e) {
        /* IDが無効な場合は新規作成フローへ */
      }
    }
    const folders = DriveApp.getFoldersByName(this._APP_FOLDER_NAME);
    const appFolder = folders.hasNext() ? folders.next() : DriveApp.getRootFolder().createFolder(this._APP_FOLDER_NAME);
    const files = appFolder.getFilesByName(this._LOG_FILE_NAME);
    if (files.hasNext()) {
      const file = files.next();
      userProperties.setProperty(this._LOG_FILE_ID_KEY, file.getId());
      return SpreadsheetApp.openById(file.getId());
    }
    const ss = SpreadsheetApp.create(this._LOG_FILE_NAME);
    const file = DriveApp.getFileById(ss.getId());
    appFolder.addFile(file);
    DriveApp.getRootFolder().removeFile(file);
    userProperties.setProperty(this._LOG_FILE_ID_KEY, ss.getId());

    const summarySheet = ss.getSheetByName('シート1');
    summarySheet.setName(this._SUMMARY_SHEET_NAME);
    summarySheet.appendRow(['実行ID (親ID)', '実行日時', 'ワークフロー名', '実行種別', 'ステータス', '処理時間(秒)', '詳細ログへのリンク']);
    summarySheet.setFrozenRows(1);
    const summaryHeader = summarySheet.getRange('A1:G1');
    summaryHeader.setBackground('#f3f3f3').setFontWeight('bold').setHorizontalAlignment('center');
    summarySheet.setColumnWidth(1, 180);
    summarySheet.setColumnWidth(2, 150);
    summarySheet.setColumnWidth(3, 250);
    summarySheet.setColumnWidth(4, 100);
    summarySheet.setColumnWidth(5, 100);
    summarySheet.setColumnWidth(6, 120);
    summarySheet.setColumnWidth(7, 150);
    const summaryStatusRange = summarySheet.getRange('E2:E');
    const summaryRules = [
      SpreadsheetApp.newConditionalFormatRule().whenTextEqualTo('成功').setBackground('#d9ead3').setRanges([summaryStatusRange]).build(),
      SpreadsheetApp.newConditionalFormatRule().whenTextEqualTo('失敗').setBackground('#f4cccc').setRanges([summaryStatusRange]).build()
    ];
    summarySheet.setConditionalFormatRules(summaryRules);

    const detailSheet = ss.insertSheet(this._DETAIL_SHEET_NAME);
    detailSheet.appendRow(['実行ID (親ID)', 'ワークフロー名', 'タイムスタンプ', 'モジュール名', 'モジュールインスタンスID', 'ステータス', '詳細']);
    detailSheet.setFrozenRows(1);
    const detailHeader = detailSheet.getRange('A1:G1');
    detailHeader.setBackground('#f3f3f3').setFontWeight('bold').setHorizontalAlignment('center');
    detailSheet.setColumnWidth(1, 180);
    detailSheet.setColumnWidth(2, 200);
    detailSheet.setColumnWidth(3, 150);
    detailSheet.setColumnWidth(4, 250);
    detailSheet.setColumnWidth(5, 180);
    detailSheet.setColumnWidth(6, 100);
    detailSheet.setColumnWidth(7, 400);
    const detailStatusRange = detailSheet.getRange('F2:F');
    const detailRules = [
      SpreadsheetApp.newConditionalFormatRule().whenTextEqualTo('成功').setBackground('#d9ead3').setRanges([detailStatusRange]).build(),
      SpreadsheetApp.newConditionalFormatRule().whenTextEqualTo('失敗').setBackground('#f4cccc').setRanges([detailStatusRange]).build(),
      SpreadsheetApp.newConditionalFormatRule().whenTextEqualTo('スキップ').setBackground('#efefef').setRanges([detailStatusRange]).build()
    ];
    detailSheet.setConditionalFormatRules(detailRules);
    this._createDashboardSheet(ss);
    return ss;
  },

  startLog: function (workflowName, type) {
    const ss = this._getLogSpreadsheet();
    const summarySheet = ss.getSheetByName(this._SUMMARY_SHEET_NAME);
    const runId = `run-${new Date().getTime()}-${Math.random().toString(36).substring(2, 8)}`;
    this._workflowNames[runId] = workflowName;
    const startTime = new Date();
    summarySheet.appendRow([runId, startTime, workflowName, type, '実行中...', '', '']);
    const summaryRow = summarySheet.getLastRow();
    return { runId: runId, summaryRow: summaryRow };
  },

  addLog: function (runId, level, message) {
    const workflowName = this._workflowNames[runId] || '不明';
    const statusMap = {
      'system': '情報',
      'info': '情報',
      'success': '成功',
      'error': '失敗',
      'skip': 'スキップ'
    };
    const status = statusMap[level] || '情報';
    this.writeDetailLog(runId, workflowName, 'Workflow Engine', '', status, message);
  },

  writeDetailLog: function (runId, workflowName, moduleName, instanceId, status, message) {
    const ss = this._getLogSpreadsheet();
    const detailSheet = ss.getSheetByName(this._DETAIL_SHEET_NAME);
    const timestamp = new Date();
    detailSheet.appendRow([runId, workflowName, timestamp, moduleName, instanceId, status, message]);
  },

  endLog: function (runId, summaryRow, status, executionTime) {
    const ss = this._getLogSpreadsheet();
    const summarySheet = ss.getSheetByName(this._SUMMARY_SHEET_NAME);
    const detailSheet = ss.getSheetByName(this._DETAIL_SHEET_NAME);
    const detailSheetGid = detailSheet.getSheetId();
    const spreadsheetUrl = ss.getUrl();
    const detailLinkUrl = `${spreadsheetUrl}#gid=${detailSheetGid}`;
    const linkFormula = `=HYPERLINK("${detailLinkUrl}", "詳細表示")`;
    const range = summarySheet.getRange(summaryRow, 5, 1, 3);
    range.setValues([[status, executionTime, linkFormula]]);
    delete this._workflowNames[runId]; // Clean up
  },

  logSkippedExecution: function (workflowName, type) {
    const ss = this._getLogSpreadsheet();
    const summarySheet = ss.getSheetByName(this._SUMMARY_SHEET_NAME);
    const startTime = new Date();
    const runId = `skip-${startTime.getTime()}`;
    const status = 'スキップ';
    const message = '他のプロセスが実行中のため、この実行はスキップされました。';
    summarySheet.appendRow([runId, startTime, workflowName, type, status, 0, message]);
  },

  /**
   * Writes a general informational log message without a runId.
   * @param {string} message The log message.
   * @param {string} [moduleName='System'] The name of the module logging the message.
   */
  info: function(message, moduleName = 'System') {
    this.writeDetailLog('N/A', 'N/A', moduleName, '', '情報', message);
  },

  /**
   * Writes a general error log message without a runId.
   * @param {string} message The error message.
   * @param {string} [moduleName='System'] The name of the module logging the message.
   */
  error: function(message, moduleName = 'System') {
    this.writeDetailLog('N/A', 'N/A', moduleName, '', '失敗', message);
  },

  getSummaryLogs: function (limit) {
    const ss = this._getLogSpreadsheet();
    const summarySheet = ss.getSheetByName(this._SUMMARY_SHEET_NAME);
    const lastRow = summarySheet.getLastRow();
    if (lastRow < 2) {
      return [];
    }
    const startRow = Math.max(2, lastRow - limit + 1);
    const numRows = lastRow - startRow + 1;
    const range = summarySheet.getRange(startRow, 1, numRows, 7);
    const values = range.getValues();
    const formulas = range.getFormulas();
    const logs = values.map((row, index) => {
      const detailLinkFormula = formulas[index][6];
      let detailLink = '';
      if (detailLinkFormula) {
        const urlMatch = detailLinkFormula.match(/HYPERLINK\("([^"]+)"/);
        if (urlMatch && urlMatch[1]) {
          detailLink = urlMatch[1];
        }
      }
      return {
        runId: row[0],
        startTime: row[1],
        workflowName: row[2],
        type: row[3],
        status: row[4],
        executionTime: row[5],
        detailLink: detailLink
      };
    });
    return logs.reverse();
  },

  getLogSheetUrl: function () {
    const ss = this._getLogSpreadsheet();
    return ss.getUrl();
  },

  _createDashboardSheet: function (ss) {
    try {
      const oldSheet = ss.getSheetByName('ダッシュボード');
      if (oldSheet) {
        ss.deleteSheet(oldSheet);
      }
      const sheet = ss.insertSheet('ダッシュボード', 0);
      sheet.setFrozenRows(4);
      const titleRange = sheet.getRange('A1:J1');
      titleRange.merge();
      titleRange.setValue('実行ログ ダッシュボード');
      titleRange.setFontWeight('bold');
      titleRange.setFontSize(16);
      titleRange.setHorizontalAlignment('center');
      titleRange.setVerticalAlignment('middle');
      sheet.setRowHeight(1, 40);
      sheet.getRange('A3').setValue('■ 日別パフォーマンス').setFontWeight('bold').setFontSize(12);
      sheet.getRange('A4:D4').setFontWeight('bold').setBackground('#f3f3f3').setHorizontalAlignment('center');
      const dailySummaryFormula = `=IFERROR(QUERY({ARRAYFORMULA(IF(ISBLANK(Summary!B2:B),,INT(Summary!B2:B))), Summary!F2:F}, "SELECT Col1, COUNT(Col1), SUM(Col2), AVG(Col2) WHERE Col1 IS NOT NULL GROUP BY Col1 ORDER BY Col1 DESC LABEL Col1 '日付' , COUNT(Col1) '総実行数' , SUM(Col2) '総実行時間(秒)' , AVG(Col2) '平均実行時間(秒)'"), " ログデータがありません ")`;
      sheet.getRange('A4').setFormula(dailySummaryFormula);
      sheet.getRange('F3').setValue('■ モジュール別 安定性').setFontWeight('bold').setFontSize(12);
      sheet.getRange('F4:J4').setValues([['モジュール名', '総実行数', '成功', '失敗', 'エラー率']]).setFontWeight('bold').setBackground('#f3f3f3').setHorizontalAlignment('center');
      const moduleSummaryFormula = `=IFERROR(LET(modules, UNIQUE(FILTER(Detail!D2:D, Detail!D2:D<>"", Detail!D2:D<>"Workflow Engine")), ARRAYFORMULA(IF(ISBLANK(modules),"", {modules, COUNTIF(Detail!D2:D, modules), COUNTIFS(Detail!D2:D, modules, Detail!F2:F, "成功"), COUNTIFS(Detail!D2:D, modules, Detail!F2:F, "失敗")}))), "")`;
      sheet.getRange('F5').setFormula(moduleSummaryFormula);
      const errorRateFormula = `=ARRAYFORMULA(IF(ISBLANK(G5:G),, IFERROR(I5:I / G5:G, 0)))`;
      sheet.getRange('J5').setFormula(errorRateFormula);
      sheet.getRange('A5:A').setNumberFormat('yyyy-mm-dd');
      sheet.getRange('C5:D').setNumberFormat('0.00');
      sheet.getRange('G5:I').setNumberFormat('#,##0');
      sheet.getRange('J5:J').setNumberFormat('0.00%');
      sheet.setColumnWidth(1, 120);
      sheet.setColumnWidths(2, 3, 130);
      sheet.setColumnWidth(5, 30);
      sheet.setColumnWidth(6, 250);
      sheet.setColumnWidths(7, 4, 100);
      const summarySheet = ss.getSheetByName('Summary');
      if (summarySheet) {
        summarySheet.getRange('B2:B').setNumberFormat('yyyy-mm-dd hh:mm:ss');
        summarySheet.getRange('F2:F').setNumberFormat('0.00');
      }
      const detailSheet = ss.getSheetByName('Detail');
      if (detailSheet) {
        detailSheet.getRange('C2:C').setNumberFormat('yyyy-mm-dd hh:mm:ss');
      }
    } catch (e) {
      Logger.log('ダッシュボードシートの作成に失敗しました: ' + e.message);
    }
  },

  getDetailLogsByRunId: function(runId) {
    const ss = this._getLogSpreadsheet();
    const detailSheet = ss.getSheetByName(this._DETAIL_SHEET_NAME);
    const data = detailSheet.getDataRange().getValues();
    
    const logs = [];
    // ヘッダー行をスキップしてループ
    for (let i = 1; i < data.length; i++) {
      if (data[i][0] === runId) { // 実行IDが一致するかチェック
        logs.push({
          // フロントエンドで使いやすいようにオブジェクトに変換
          level: data[i][5] === '成功' ? 'success' : (data[i][5] === '失敗' ? 'error' : 'info'),
          message: `[${data[i][3]}] ${data[i][6]}` // [モジュール名] 詳細
        });
      }
    }
    return logs;
  }
};