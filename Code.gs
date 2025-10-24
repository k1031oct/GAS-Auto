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

function logInfo(message, moduleName) {
  LogService.info(message, moduleName);
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
// 6. New Feature Functions (Related Files / Templates)
//================================================================

/**
 * Gets the URL of the folder containing the specified workflow.
 * @param {string} workflowName The name of the workflow.
 * @returns {string} The URL of the folder.
 */
function getWorkflowFolderUrl(workflowName) {
  return WorkflowService.getWorkflowFolderUrl(workflowName);
}

/**
 * Gets the URL of the template folder, creating it if it doesn't exist.
 * @returns {string} The URL of the template folder.
 */
function getTemplateFolderUrl() {
  const templateFolderName = 'GAS-Auto Templates';
  let folder;
  const folders = DriveApp.getRootFolder().getFoldersByName(templateFolderName);
  if (folders.hasNext()) {
    folder = folders.next();
  } else {
    folder = DriveApp.getRootFolder().createFolder(templateFolderName);
  }
  return folder.getUrl();
}

/**
 * Gets a list of available templates for the given file type.
 * @param {string} fileType The type of file ('document' or 'sheet').
 * @returns {Array<{id: string, name: string}>} A list of templates.
 */
function getTemplates(fileType) {
  const templateFolderName = 'GAS-Auto Templates';
  const folders = DriveApp.getRootFolder().getFoldersByName(templateFolderName);
  if (!folders.hasNext()) {
    return [];
  }
  const templateFolder = folders.next();
  const templates = [];
  const files = templateFolder.getFiles();
  while (files.hasNext()) {
    const file = files.next();
    const mimeType = file.getMimeType();
    if (fileType === 'document' && mimeType === MimeType.GOOGLE_DOCS) {
      templates.push({ id: file.getId(), name: file.getName() });
    } else if (fileType === 'sheet' && mimeType === MimeType.GOOGLE_SHEETS) {
      templates.push({ id: file.getId(), name: file.getName() });
    }
  }
  return templates;
}

/**
 * Creates a new file (document or spreadsheet) in the workflow's folder.
 * @param {object} options The options for creating the file.
 * @returns {{message: string, url: string}} The result of the operation.
 */
function createFileInWorkflowFolder(options) {
  const { workflowName, fileName, fileType, creationType, templateId } = options;

  if (!workflowName) {
    throw new Error('ワークフローが指定されていません。');
  }

  const workflowFolderUrl = WorkflowService.getWorkflowFolderUrl(workflowName);
  if (!workflowFolderUrl) {
    throw new Error('ワークフローのフォルダが見つかりません。');
  }
  const workflowFolder = DriveApp.getFolderById(_extractIdFromUrl(workflowFolderUrl));

  let newFile;

  if (creationType === 'template') {
    if (!templateId) {
      throw new Error('テンプレートが選択されていません。');
    }
    const templateFile = DriveApp.getFileById(templateId);
    newFile = templateFile.makeCopy(fileName, workflowFolder);
  } else {
    if (fileType === 'document') {
      newFile = DocumentApp.create(fileName);
      DriveApp.getFileById(newFile.getId()).moveTo(workflowFolder);
    } else if (fileType === 'sheet') {
      newFile = SpreadsheetApp.create(fileName);
      DriveApp.getFileById(newFile.getId()).moveTo(workflowFolder);
    }
  }

  return {
    message: `ファイル「${fileName}」を作成しました。`,
    url: newFile.getUrl(),
  };
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

    try {
      FunctionRegistry.run(handlerName, settings);
      Logger.log(`Successfully executed module handler "${handlerName}" via trigger.`);
    } catch (e) {
      Logger.log(`Module trigger error: Handler function "${handlerName}" not found or failed to execute. Error: ${e.toString()}`);
    }
  } catch (error) {
    Logger.log(`Module trigger error: Failed to execute module for trigger ID "${triggerUid}". Error: ${error.toString()}`);
  }
}

//================================================================
// 6. 新機能：Excel自動アーカイブ
//================================================================

/**
 * Excel自動アーカイブ設定のHTMLコンテンツを取得する
 * @returns {object} HTMLコンテンツとタイトル
 */
function showArchiveSettings() {
  LogService.info('showArchiveSettings: Called from client.', 'ArchiveFeature');
  try {
    const html = HtmlService.createTemplateFromFile('ArchiveSettings').evaluate().getContent();
    LogService.info(`showArchiveSettings: Returning HTML for settings (length: ${html.length})`, 'ArchiveFeature');
    return { title: 'Excel自動アーカイブ設定', html: html };
  } catch (e) {
    LogService.error(`showArchiveSettings: Error: ${e.toString()}`, 'ArchiveFeature');
    throw e;
  }
}

function showArchiveManagement() {
  Logger.log('showArchiveManagement: LogService is ' + (typeof LogService === 'undefined' ? 'undefined' : 'defined'));
  LogService.info('showArchiveManagement: Called', 'ArchiveFeature');
  try {
    const html = HtmlService.createTemplateFromFile('ArchiveManagement').evaluate().getContent();
    return { title: '自動保管設定の管理', html: html };
  } catch (e) {
    LogService.error(`showArchiveManagement: Error: ${e.toString()}`, 'ArchiveFeature');
    throw e;
  }
}


/**
 * Google Picker APIを使用してフォルダ選択ダイアログを作成・表示する
 * @returns {object} Picker Builder
 */
function createPicker() {
  // ... (This function seems fine, no logs needed for now)
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
  LogService.info(`saveArchiveConfig: Called with: ${JSON.stringify(moduleDefinition)}`, 'ArchiveFeature');
  if (!moduleDefinition || !moduleDefinition.name) {
    throw new Error('Invalid module definition or name is missing.');
  }
  const workflowName = moduleDefinition.name;
  const workflowData = {
    name: workflowName,
    moduleFolderId: '', 
    modules: [moduleDefinition]
  };
  try {
    TriggerService.deleteTriggerByWorkflowName(workflowName);
    const result = WorkflowService.saveWorkflow(workflowName, workflowData);
    LogService.info(`saveArchiveConfig: WorkflowService.saveWorkflow result: ${result}`, 'ArchiveFeature');
    TriggerService.createTriggerForModule(moduleDefinition);
    return result;
  } catch (e) {
    LogService.error(`saveArchiveConfig: Error: ${e.toString()}`, 'ArchiveFeature');
    throw new Error(`Failed to save workflow: ${e.message}`);
  }
}

/**
 * Retrieves all archive configurations by listing and loading workflows.
 * @returns {Array<object>} An array of configuration objects, including the workflow name.
 */
function getArchiveConfigs() {
  LogService.info('getArchiveConfigs: Called', 'ArchiveFeature');
  try {
    const allWorkflows = WorkflowService.listWorkflows();
    const archiveWorkflowNames = allWorkflows.filter(name => name.startsWith('AutoArchive:'));
    LogService.info(`getArchiveConfigs: Found archive workflows: ${JSON.stringify(archiveWorkflowNames)}`, 'ArchiveFeature');
    
    const configs = archiveWorkflowNames.map(name => {
      const workflowData = WorkflowService.loadWorkflow(name);
      if (workflowData && workflowData.modules && workflowData.modules.length > 0) {
        return {
          workflowName: name,
          module: workflowData.modules[0] 
        };
      }
      return null;
    }).filter(Boolean);
    
    LogService.info(`getArchiveConfigs: Returning configs: ${JSON.stringify(configs)}`, 'ArchiveFeature');
    return configs;
  } catch (e) {
    LogService.error(`getArchiveConfigs: Error: ${e.toString()}`, 'ArchiveFeature');
    return []; // Return empty array on error to prevent UI freeze
  }
}

/**
 * Deletes an archive configuration workflow and its associated trigger.
 * @param {string} workflowName The name of the workflow to delete.
 * @returns {string} The result from the delete operation.
 */
function deleteArchiveConfig(workflowName) {
  LogService.info(`deleteArchiveConfig: Called for: ${workflowName}`, 'ArchiveFeature');
  if (!workflowName) {
    throw new Error('Workflow name is required to delete the configuration.');
  }

  const workflowData = WorkflowService.loadWorkflow(workflowName);
  if (workflowData && workflowData.modules && workflowData.modules.length > 0) {
    const moduleConfig = workflowData.modules[0];
    TriggerService.deleteTriggerForModule(moduleConfig);
  } else {
    LogService.info(`deleteArchiveConfig: Could not find workflow data for "${workflowName}" to delete its trigger.`, 'ArchiveFeature');
  }

  return WorkflowService.deleteWorkflow(workflowName);
}
