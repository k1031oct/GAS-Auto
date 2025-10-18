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

  runWorkflow: function (payload, executionType = '手動') {
    const startTime = new Date();
    const workflowName = payload.name || '名称未設定のワークフロー';
    const modules = payload.modules || [];

    let logContext = LogService.startLog(workflowName, executionType);
    let status = '成功';
    let lastReturnValue = null; // 前のモジュールからの返り値を保持

    try {
      LogService.addLog(logContext.runId, 'info', `ワークフロー「${workflowName}」の実行を開始します...`);

      for (const moduleConfig of modules) {
        const moduleDef = ModuleService.getModuleById(moduleConfig.id);
        if (!moduleDef) {
          throw new Error(`モジュール「${moduleConfig.id}」の定義が見つかりません。`);
        }
        
        LogService.addLog(logContext.runId, 'system', `[モジュール実行] ${moduleConfig.name} (ID: ${moduleConfig.id})`);

        // モジュールロジックを実行
        const result = this._executeModuleLogic(moduleConfig.id, moduleConfig.settings, lastReturnValue);

        // 返り値があるモジュールの場合、次のモジュールに渡す
        if (moduleDef.returnsValue) {
          lastReturnValue = result;
          const resultInfo = Array.isArray(result) ? `${result.length}件のアイテム` : String(result);
          LogService.addLog(logContext.runId, 'info', `  ↪ 結果: ${resultInfo} を次のモジュールに渡します。`);
        } else {
          lastReturnValue = null; // 返り値をリセット
        }
      }

      LogService.addLog(logContext.runId, 'success', 'ワークフローの実行が正常に完了しました。');

    } catch (e) {
      status = '失敗';
      LogService.addLog(logContext.runId, 'error', `[致命的なエラー] ${e.message} ワークフローの実行を中断しました。`);
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
   * @param {string} moduleId - 実行するモジュールのID
   * @param {object} configs - モジュールの設定
   * @param {*} inputValue - 前のモジュールからの入力値
   * @returns {*} モジュールの処理結果
   */
  _executeModuleLogic: function (moduleId, configs, inputValue) {
    // inputValue は主にファイルIDのリストなどを想定
    const fileIds = Array.isArray(inputValue) ? inputValue : [];

    switch (moduleId) {
      // =============================================
      // オーガナイザー機能 (新しいサービス経由)
      // =============================================
      case 'drive_filter_files':
        return OrganizerService.filterFiles(configs, fileIds);
      
      case 'drive_convert_files':
        return OrganizerService.convertFiles(configs, fileIds);

      case 'drive_archive_files':
        return OrganizerService.archiveFiles(configs, fileIds);

      // =============================================
      // 新しいサービスモジュール
      // =============================================
      case 'drive_move_file':
        return DriveService.moveFile(configs);
      
      case 'drive_copy_file':
        return DriveService.copyFile(configs);

      case 'sheets_update_cell':
        return SheetService.updateCell(configs);

      case 'sheets_append_row':
        return SheetService.appendRow(configs);
      
      case 'gmail_send_email':
        return GmailService.sendEmail(configs);

      case 'calendar_create_event':
        return CalendarService.createEvent(configs);
      
      case 'docs_create_file':
        return DocsService.createFile(configs);

      default:
        Logger.log(`モジュールID「${moduleId}」に対応する処理が見つかりません。スキップします。`);
        return null;
    }
  },

};
