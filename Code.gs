/**
 * @file Code.gs
 * @description Webアプリのメインファイル。各サービスへの橋渡しを行う。
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
  const regex = /(?:\/d\/|\/folders\/|id=)([a-zA-Z0-9_-]{20,})/;
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

function getInitialData() {
  const workflows = WorkflowService.listWorkflows();
  const settings = getSettings();
  return { workflows: workflows, moduleFolderId: settings.moduleFolderId, settings: settings };
}

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

function runSingleModule(workflowName, moduleConfig, moduleFolderId) {
  try {
    return WorkflowService.runSingleModule(workflowName, moduleConfig, moduleFolderId);
  } catch (e) {
    Logger.log(e.stack);
    throw new Error(`単体実行エラー: ${e.message}.`);
  }
}

function getExecutionLogUrl() {
  return LogService.getLogSheetUrl();
}

/**
 * 指定されたフォルダIDに初期モジュールを生成する
 * @param {string} folderId
 */
function createInitialModules(folderId) {
  if (!folderId) {
    throw new Error('フォルダIDが指定されていません。');
  }
  return ModuleService.createInitialModules(folderId);
}

//================================================================
// 4. トリガーサービスの公開関数
//================================================================

function getTriggersForWorkflow(workflowName) {
  return TriggerService.getTriggersForWorkflow(workflowName);
}

function createTimeBasedTrigger(workflowName, options) {
  return TriggerService.createTimeBasedTrigger(workflowName, options);
}

function deleteTrigger(triggerUid) {
  return TriggerService.deleteTrigger(triggerUid);
}

//================================================================
// 5. アプリケーション設定の管理
//================================================================

/**
 * ユーザー設定を保存する
 * @param {object} settings 保存する設定オブジェクト
 */
function saveSettings(settings) {
  try {
    PropertiesService.getUserProperties().setProperty('appSettings', JSON.stringify(settings));
    Logger.log(`設定を保存しました: ${JSON.stringify(settings)}`);
    return settings; // 保存した設定を返す
  } catch (e) {
    Logger.log(`設定の保存に失敗しました: ${e.message}`);
    throw new Error('設定の保存中にエラーが発生しました。');
  }
}

/**
 * ユーザー設定を読み込む
 * @returns {object} 読み込んだ設定オブジェクト
 */
function getSettings() {
  try {
    const settingsJson = PropertiesService.getUserProperties().getProperty('appSettings');
    if (settingsJson) {
      return JSON.parse(settingsJson);
    }
    return {}; // 設定がない場合は空のオブジェクトを返す
  } catch (e) {
    Logger.log(`設定の読み込みに失敗しました: ${e.message}`);
    throw new Error('設定の読み込み中にエラーが発生しました。');
  }
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

/**
 * Executes a single module based on a trigger event.
 * @param {GoogleAppsScript.Events.TimeDriven} e The trigger event object.
 */
function executeModuleByTrigger(e) {
  const triggerUid = e.triggerUid;
  const properties = PropertiesService.getUserProperties();
  const moduleConfigStr = properties.getProperty(`module_config_${triggerUid}`);

  if (!moduleConfigStr) {
    Logger.log(`Module trigger error: Could not find config for trigger ID "${triggerUid}".`);
    return;
  }

  try {
    const moduleConfig = JSON.parse(moduleConfigStr);
    const handlerName = moduleConfig.handler;
    const settings = moduleConfig.settings.reduce((acc, setting) => {
      acc[setting.id] = setting.value;
      return acc;
    }, {});

    if (typeof this[handlerName] === 'function') {
      this[handlerName](settings);
      Logger.log(`Successfully executed module handler "${handlerName}" via trigger.`);
    } else {
      Logger.log(`Module trigger error: Handler function "${handlerName}" not found.`);
    }
  } catch (error) {
    Logger.log(`Module trigger error: Failed to execute module for trigger ID "${triggerUid}". Error: ${error.toString()}`);
  }
}

//================================================================
// 6. 新機能：Excel自動アーカイブ
//================================================================

/**
 * Excel自動アーカイブ設定ウィザードのHTMLコンテンツを取得する
 * @returns {string} HTMLコンテンツ
 */
function showArchiveWizard() {
  const html = HtmlService.createTemplateFromFile('ArchiveWizard').evaluate().getContent();
  return { title: 'Excel自動アーカイブ設定', html: html };
}

function showArchiveManagement() {
  const html = HtmlService.createTemplateFromFile('ArchiveManagement').evaluate().getContent();
  return { title: '自動保管設定の管理', html: html };
}


/**
 * Google Picker APIを使用してフォルダ選択ダイアログを作成・表示する
 * @returns {object} Picker Builder
 */
function createPicker() {
  const apiKey = PropertiesService.getScriptProperties().getProperty('GCP_API_KEY');
  if (!apiKey) {
    throw new Error('GCP API Key is not set. Please ask the administrator to set the "GCP_API_KEY" in the script properties.');
  }
  const oauthToken = ScriptApp.getOAuthToken();
  const view = new google.picker.View(google.picker.ViewId.FOLDERS);
  view.setMimeTypes("application/vnd.google-apps.folder");
  
  const picker = google.picker.PickerBuilder()
      .enableFeature(google.picker.Feature.NAV_HIDDEN)
      .setAppId(ScriptApp.getProjectKey()) // Apps Script Project Key
      .setOAuthToken(oauthToken)
      .addView(view)
      .setDeveloperKey(apiKey); // GCP API Key
  
  return picker;
}

/**
 * 新しいモジュール定義をJSONファイルとして保存する
 * @param {object} moduleDefinition - 保存するモジュールの定義オブジェクト
 */
function saveArchiveConfig(moduleDefinition) {
  const configs = getArchiveConfigs();
  
  // Check if a config with the same ID already exists to update it
  const existingIndex = configs.findIndex(c => c.id === moduleDefinition.id);

  if (existingIndex > -1) {
    // Update existing config
    configs[existingIndex] = moduleDefinition;
  } else {
    // Add new config
    configs.push(moduleDefinition);
  }

  try {
    PropertiesService.getUserProperties().setProperty('archiveConfigs', JSON.stringify(configs));
    // Also, set up the trigger for this specific module
    TriggerService.createTriggerForModule(moduleDefinition);
    Logger.log(`Saved archive configurations.`);
    return configs;
  } catch (e) {
    Logger.log(`Failed to save archive configurations: ${e.message}`);
    throw new Error(`Failed to save archive configurations: ${e.message}`);
  }
}

/**
 * Retrieves all archive configurations from PropertiesService.
 * @returns {Array<object>} An array of configuration objects.
 */
function getArchiveConfigs() {
  try {
    const configsJson = PropertiesService.getUserProperties().getProperty('archiveConfigs');
    return configsJson ? JSON.parse(configsJson) : [];
  } catch (e) {
    Logger.log(`Failed to retrieve archive configurations: ${e.message}`);
    return [];
  }
}

/**
 * Deletes an archive configuration by its ID.
 * @param {string} configId The ID of the configuration to delete.
 * @returns {Array<object>} The remaining configuration objects.
 */
function deleteArchiveConfig(configId) {
  let configs = getArchiveConfigs();
  const configToDelete = configs.find(c => c.id === configId);
  
  if (!configToDelete) {
    throw new Error(`Configuration with ID ${configId} not found.`);
  }

  // Delete the associated trigger first
  TriggerService.deleteTriggerForModule(configToDelete);

  const remainingConfigs = configs.filter(c => c.id !== configId);

  try {
    PropertiesService.getUserProperties().setProperty('archiveConfigs', JSON.stringify(remainingConfigs));
    Logger.log(`Deleted archive configuration with ID: ${configId}`);
    return remainingConfigs;
  } catch (e) {
    Logger.log(`Failed to delete archive configuration: ${e.message}`);
    throw new Error(`Failed to delete archive configuration: ${e.message}`);
  }
}
