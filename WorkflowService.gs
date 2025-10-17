/**
 * @file WorkflowService.gs
 * @description ワークフローの保存、読み込み、実行を担当する。モジュールフォルダIDをワークフローの一部として扱う。
 */
var WorkflowService = {
  _APP_DATA_FOLDER_NAME: 'GAS_Workflow_Automator_AppData',
  _WORKFLOWS_ROOT_FOLDER_NAME: 'Workflows',

  /**
   * アプリケーションデータ用のルートフォルダを取得または作成する。
   * @private
   */
  _getAppDataFolder: function () {
    const folders = DriveApp.getFoldersByName(this._APP_DATA_FOLDER_NAME);
    if (folders.hasNext()) {
      return folders.next();
    }
    return DriveApp.getRootFolder().createFolder(this._APP_DATA_FOLDER_NAME);
  },


  /**
   * ワークフロー保存用のルートフォルダを取得または作成する。
   * @private
   */
  _getWorkflowsRootFolder: function () {
    const appDataFolder = this._getAppDataFolder();
    const folders = appDataFolder.getFoldersByName(this._WORKFLOWS_ROOT_FOLDER_NAME);
    if (folders.hasNext()) {
      return folders.next();
    }
    return appDataFolder.createFolder(this._WORKFLOWS_ROOT_FOLDER_NAME);
  },


  /**
   * 指定されたワークフロー名の専用フォルダを取得または作成する。
   * @private
   * @param {string} workflowName - ワークフロー名
   * @returns {GoogleAppsScript.Drive.Folder} ワークフロー専用フォルダ
   */
  _getWorkflowFolder: function (workflowName) {
    const workflowsRootFolder = this._getWorkflowsRootFolder();
    const workflowFolders = workflowsRootFolder.getFoldersByName(workflowName);
    if (workflowFolders.hasNext()) {
      return workflowFolders.next();
    }
    return workflowsRootFolder.createFolder(workflowName);
  },


  /**
   * 保存されている全ワークフローのリストを取得する。
   */
  listWorkflows: function () {
    const workflowsRootFolder = this._getWorkflowsRootFolder();
    const workflowNames = new Set();

    const subFolders = workflowsRootFolder.getFolders();
    while (subFolders.hasNext()) {
      const workflowFolder = subFolders.next();
      const files = workflowFolder.getFilesByName('workflow.json');
      if (files.hasNext()) {
        workflowNames.add(workflowFolder.getName());
      }
    }
    
    // 旧フォーマットのファイルも検索（互換性維持のため）
    const rootFiles = workflowsRootFolder.getFilesByType(MimeType.PLAIN_TEXT);
    while (rootFiles.hasNext()) {
      let file = rootFiles.next();
      const fileName = file.getName();
      if (fileName.endsWith('.json') && fileName !== 'workflow.json') {
        workflowNames.add(fileName.replace('.json', ''));
      }
    }

    return Array.from(workflowNames).sort();
  },


  /**
   * 指定されたワークフローの定義(JSON)を読み込む。
   * @param {string} workflowName
   */
  loadWorkflow: function (workflowName) {
    const workflowsRootFolder = this._getWorkflowsRootFolder();
    
    const workflowFolders = workflowsRootFolder.getFoldersByName(workflowName);
    if (workflowFolders.hasNext()) {
      const workflowFolder = workflowFolders.next();
      const files = workflowFolder.getFilesByName('workflow.json');
      if (files.hasNext()) {
        const content = files.next().getBlob().getDataAsString();
        const data = JSON.parse(content);
        
        // 互換性のため、トップレベルのデータ構造をチェック
        if (!data.moduleFolderId && !data.modules) {
             // 旧Organizerフォーマットの可能性あり。そのまま返す
             return { name: workflowName, moduleFolderId: ModuleService._getDefaultModuleFolderId() || '', modules: data.rules || data };
        }
        return data;
      }
    }

    // 旧フォーマットのファイル検索
    const oldFormatFiles = workflowsRootFolder.getFilesByName(`${workflowName}.json`);
    if (oldFormatFiles.hasNext()) {
      const content = oldFormatFiles.next().getBlob().getDataAsString();
      // 旧フォーマットはフォルダIDを持たないため、デフォルトを設定
      return { name: workflowName, moduleFolderId: ModuleService._getDefaultModuleFolderId() || '', modules: JSON.parse(content) };
    }

    return null;
  },


  /**
   * ワークフローを専用フォルダ内に 'workflow.json' として保存する。
   * @param {string} workflowName
   * @param {object} workflowData - {name, moduleFolderId, modules}
   */
  saveWorkflow: function (workflowName, workflowData) {
    if (!workflowName) {
      throw new Error('ワークフロー名が指定されていません。');
    }
    const workflowFolder = this._getWorkflowFolder(workflowName);
    const fileName = 'workflow.json';
    
    // ワークフローのトップレベル構造を定義
    const dataToSave = {
        name: workflowName,
        moduleFolderId: workflowData.moduleFolderId || '',
        modules: workflowData.modules || []
    };
    
    const content = JSON.stringify(dataToSave, null, 2);

    const files = workflowFolder.getFilesByName(fileName);

    if (files.hasNext()) {
      const existingFile = files.next();
      // 既存ファイルをアーカイブ
      let archiveFolder;
      const archiveFolders = workflowFolder.getFoldersByName('archive');
      if (archiveFolders.hasNext()) {
        archiveFolder = archiveFolders.next();
      } else {
        archiveFolder = workflowFolder.createFolder('archive');
      }

      const timestamp = Utilities.formatDate(new Date(), 'JST', 'yyyyMMdd_HHmmss');
      existingFile.makeCopy(`workflow_${timestamp}.json`, archiveFolder);

      // 内容を上書き
      existingFile.setContent(content);

    } else {
      // 新規ファイルを作成
      workflowFolder.createFile(fileName, content, MimeType.PLAIN_TEXT);
    }
    
    // モジュールフォルダIDが保存された場合、ユーザープロパティにも保存（次回ロード時の初期値として使う）
    if (workflowData.moduleFolderId) {
        PropertiesService.getUserProperties().setProperty(ModuleService._DEFAULT_MODULE_FOLDER_KEY, workflowData.moduleFolderId);
    }
    
    return `ワークフロー「${workflowName}」を専用フォルダに保存しました。`;
  },


  /**
   * ワークフローを削除する。
   */
  deleteWorkflow: function (workflowName) {
    const workflowsRootFolder = this._getWorkflowsRootFolder();
    let deleted = false;

    const folders = workflowsRootFolder.getFoldersByName(workflowName);
    if (folders.hasNext()) {
      folders.next().setTrashed(true);
      deleted = true;
    }

    const oldFormatFiles = workflowsRootFolder.getFilesByName(`${workflowName}.json`);
    if (oldFormatFiles.hasNext()) {
      oldFormatFiles.next().setTrashed(true);
      deleted = true;
    }

    if (deleted) {
      return `ワークフロー「${workflowName}」を削除しました。`;
    } else {
      throw new Error(`ワークフロー「${workflowName}」は見つかりませんでした。`);
    }
  },

  // ワークフローの実行ロジック
  runWorkflow: function (payload, executionType = '手動') {
    const startTime = new Date();
    const workflowName = payload.name || '名称未設定のワークフロー';
    const workflowData = payload.modules;

    let logContext = LogService.startLog(workflowName, executionType);
    let status = '成功';
    let lastReturnValue = null;

    try {
      logContext.addLog('ワークフローの実行を開始します...');
      
      for (const moduleConfig of workflowData) {
        const moduleDef = ModuleService.getModuleById(moduleConfig.id, payload.moduleFolderId);
        if (!moduleDef) {
          throw new Error(`モジュール「${moduleConfig.id}」の定義が見つかりません。`);
        }
        
        logContext.addLog(`[実行] ${moduleDef.name}`, 'system');
        
        const result = this._executeModuleLogic(moduleConfig, lastReturnValue);

        if (moduleDef.returnsValue) {
          lastReturnValue = result;
        } else {
          lastReturnValue = null;
        }
        logContext.addLog(`[完了] ${moduleDef.name} (戻り値: ${JSON.stringify(lastReturnValue)})`);
      }
      
      logContext.addLog('ワークフローの実行が正常に完了しました。', 'success');

    } catch (e) {
      status = '失敗';
      logContext.addLog(`[致命的なエラー] ${e.message} ワークフローの実行を中断しました。`, 'error');
      Logger.log(e.stack);
    } finally {
      const endTime = new Date();
      const executionTime = (endTime.getTime() - startTime.getTime()) / 1000;
      LogService.endLog(logContext.runId, logContext.summaryRow, status, executionTime);
    }
  },

  /**
   * モジュールIDに基づいて実際の処理を実行する
   * @private
   */
  _executeModuleLogic: function (moduleConfig, lastReturnValue) {
    const { id, settings } = moduleConfig;
    
    // TODO: ここに各モジュールの実行ロジックを記述する
    switch (id) {
      // =============================================
      // Organizer Modules
      // =============================================
      case 'drive_filter_files':
        return OrganizerFunctions.filterFiles(settings, lastReturnValue);
      case 'drive_convert_files':
        return OrganizerFunctions.convertFiles(settings, lastReturnValue);
      case 'drive_archive_files':
        return OrganizerFunctions.archiveFiles(settings, lastReturnValue);

      // =============================================
      // Example Modules
      // =============================================
      case 'log_message':
        Logger.log(settings.message || 'No message set.');
        return null;
        
      case 'create_document':
        const doc = DocumentApp.create(settings.docName || 'New Document');
        doc.getBody().appendParagraph(settings.content || '');
        if (settings.folderId) {
            DriveApp.getFolderById(settings.folderId).addFile(DriveApp.getFileById(doc.getId()));
        }
        return doc.getId();

      default:
        Logger.log(`モジュール「${id}」に対応する実行ロジックが見つかりません。`);
        // throw new Error(`モジュール「${id}」に対応する実行ロジックが見つかりません。`);
        return null;
    }
  },
};
