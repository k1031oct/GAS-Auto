/**
 * @file LogService.gs
 * @description ワークフローの実行履歴をスプレッドシートに記録・管理する。
 */
var LogService = {
  _APP_FOLDER_NAME: 'GAS_Workflow_Automator_AppData', // 変更
  _LOG_FILE_NAME: 'Automator_ExecutionLogs', // 変更
  _SUMMARY_SHEET_NAME: 'Summary',
  _DETAIL_SHEET_NAME: 'Detail',
  _LOG_FILE_ID_KEY: 'AUTOMATOR_APP_LOG_FILE_ID', // 変更
  // ... (以下、元のGAS-Auto-Builder/LogService.gsのコードを継続)

  // NOTE: ここには元のGAS-Auto-Builder/LogService.gsのコードが続きます。
  // ... _getLogSpreadsheet: function () { ... }
  // ... startLog: function (workflowName, type) { ... }
  // ... writeDetailLog: function (runId, workflowName, moduleName, instanceId, status, message) { ... }
  // ... endLog: function (runId, summaryRow, status, executionTime) { ... }
  // ... getSummaryLogs: function (limit) { ... }
  // ... getLogSheetUrl: function () { ... }
  // ... _createDashboardSheet: function (ss) { ... }
};
