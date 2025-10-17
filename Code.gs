/**
 * @file Code.gs
 * @description Webアプリのメインファイル。各サービスへの橋渡しを行う。
 * API管理機能はオミットし、モジュール定義の動的ロードに対応。
 */

//================================================================
// 1. Webアプリのメインエントリーポイント
//================================================================

function doGet(e) {
  return HtmlService.createTemplateFromFile('index')
    .evaluate()
    .setTitle('GAS File & Workflow Automator')
    .addMetaTag('viewport', 'width=device-width, initial-scale=1');
}

/**
 * モジュール定義をDriveから再読み込みする
 * @param {string} folderId 
 */
function loadModuleDefinitions(folderId) {
  try {
    const modules = ModuleService.loadModuleDefinitions(folderId);
    return modules;
  } catch (e) {
    Logger.log(`モジュールのロードに失敗しました: ${e.message}`);
    throw new Error(`モジュールのロードに失敗しました: ${e.message}`);
  }
}


function include(filename) {
  return HtmlService.createHtmlOutputFromFile(filename).getContent();
}

//================================================================
// 2. 内部ヘルパー関数 (既存のHelper関数を統合・整理)
//================================================================

function _extractIdFromUrl(url) {
  if (!url) return null;
  const regex = /(?:\/d\/|\/folders\/|id=)([a-zA-Z0-9-_]{20,})/;
  const match = url.match(regex);
  if (match && match[1]) {
    return match[1];
  }
  const regex2 = /\/file\/d\/(.*?)\//;
  const match2 = url.match(regex2);
  if (match2 && match2[1]) {
    return match2[1];
  }
  return null;
}

function _findModuleRecursive(modules, condition) {
  // Containerモジュール（if, foreachなど）内のモジュールを再帰的に検索するロジック
  for (const module of modules) {
    if (condition(module)) {
      return module;
    }
    if (module.type === 'container' && module.settings) {
      for (const key in module.settings) {
        if (key.endsWith('Modules') && Array.isArray(module.settings[key])) {
          const found = _findModuleRecursive(module.settings[key], condition);
          if (found) {
            return found;
          }
        }
      }
    }
  }
  return null;
}

//================================================================
// 3. クライアント(JavaScript)から呼び出される公開関数
//================================================================

function getSheetNamesFromUrl(url) {
  if (!url) {
    throw new Error("URLが入力されていません。");
  }
  try {
    const spreadsheetId = _extractIdFromUrl(url);
    if (!spreadsheetId) {
      throw new Error("URLから有効なIDを抽出できませんでした。");
    }
    const spreadsheet = SpreadsheetApp.openById(spreadsheetId);
    const sheets = spreadsheet.getSheets();
    return sheets.map(sheet => sheet.getSheetName());
  } catch (e) {
    Logger.log(`シート名の取得に失敗: ${e.message}`);
    throw new Error(`シート名の取得に失敗しました。URLが正しいか、アクセス権があるか確認してください。`);
  }
}

function listWorkflows() {
  return WorkflowService.listWorkflows();
}

function loadWorkflow(workflowName) {
  return WorkflowService.loadWorkflow(workflowName);
}

function saveWorkflow(workflowName, workflowData) {
  return WorkflowService.saveWorkflow(workflowName, workflowData);
}

function deleteWorkflow(workflowName) {
  return WorkflowService.deleteWorkflow(workflowName);
}

function runWorkflow(payload) {
  try {
    // ワークフロー実行前に、最新のモジュール定義をロードしてModuleServiceに設定する
    const folderId = payload.moduleFolderId || ModuleService._getDefaultModuleFolderId();
    if (folderId) {
        ModuleService.loadModuleDefinitions(folderId);
    }
    
    // runWorkflowのロジックを呼び出す
    return WorkflowService.runWorkflow(payload);
  } catch (e) {
    Logger.log(e.stack);
    throw new Error(`サーバーサイドエラー: ${e.message}.`);
  }
}

function getExecutionLogUrl() {
  return LogService.getLogSheetUrl();
}

//================================================================
// 4. トリガーから直接呼び出されるグローバル関数
//================================================================

function executeWorkflowByTrigger(e) {
  const triggerUid = e.triggerUid;
  const properties = PropertiesService.getUserProperties();
  const configStr = properties.getProperty(`config_${triggerUid}`);
  
  if (!configStr) {
    Logger.log(`トリガー実行エラー: トリガーID「${triggerUid}」に対応する設定が見つかりませんでした。`);
    return;
  }
  
  const config = JSON.parse(configStr);
  const workflowName = config.workflowName;

  if (!workflowName) {
    Logger.log(`トリガー実行エラー: 設定内にワークフロー名が見つかりませんでした。(ID: ${triggerUid})`);
    return;
  }

  // ワークフローデータとモジュール定義を読み込む
  const workflowData = WorkflowService.loadWorkflow(workflowName);
  
  if (workflowData) {
    const payload = {
      name: workflowName,
      moduleFolderId: workflowData.moduleFolderId, // 保存されたフォルダIDを使用
      modules: workflowData.modules
    };

    // 実行前にモジュール定義をロード
    if (payload.moduleFolderId) {
        ModuleService.loadModuleDefinitions(payload.moduleFolderId);
    }
    
    // イベントトリガーのチェックロジック（省略）

    try {
      WorkflowService.runWorkflow(payload, 'トリガー');
    } catch (err) {
      Logger.log(`トリガー実行中にエラーが発生しました: ${err.toString()}`);
    }
  } else {
    Logger.log(`トリガー実行エラー: 実行対象のワークフロー「${workflowName}」が見つかりませんでした。`);
  }
}
