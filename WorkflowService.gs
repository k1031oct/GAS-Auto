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
    const workflowName = payload.name || '名称未設定のワークフロー';
    const lock = LockService.getScriptLock();
    const lockAcquired = lock.tryLock(30000); // 30秒待機

    if (!lockAcquired) {
      LogService.logSkippedExecution(workflowName, executionType);
      // クライアントに返すためのログメッセージを作成
      return [{ level: 'system', message: '他のプロセスが実行中のため、実行はスキップされました。' }];
    }

    const startTime = new Date();
    const modules = payload.modules || [];
    let logContext = LogService.startLog(workflowName, executionType);
    let status = '成功';
    let lastReturnValue = null;

    try {
      // Recursive execution function
      const executeModulesRecursive = (moduleList) => {
        for (const moduleConfig of moduleList) {
          if (moduleConfig.enabled === false) {
            LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, 'スキップ', `モジュール「${moduleConfig.name}」は無効化されています。`);
            continue;
          }
  
          const moduleDef = ModuleService.getModuleById(moduleConfig.id);
          if (!moduleDef) {
            throw new Error(`モジュール「${moduleConfig.id}」の定義が見つかりません。`);
          }
          
          LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `モジュール「${moduleConfig.name}」を実行します。`);
  
          if (moduleDef.type === 'container') {
            if (moduleDef.id === 'condition_branch') {
              const { operand, operator, value } = moduleConfig.settings;
              LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `条件評価: 「${operand}」 ${operator} 「${value}」`);
              
              let conditionResult = false;
              // Basic evaluation logic
              switch (operator) {
                case 'eq':
                  conditionResult = (operand == value);
                  break;
                case 'neq':
                  conditionResult = (operand != value);
                  break;
                case 'gt':
                  // Basic numeric comparison
                  conditionResult = (Number(operand) > Number(value));
                  break;
                // Add other operators as needed
                default:
                  LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '警告', `未知の比較演算子: ${operator}`);
              }
  
              if (conditionResult) {
                LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `条件がTrueのため、Trueブランチを実行します。`);
                if (moduleConfig.true_modules && moduleConfig.true_modules.length > 0) {
                  executeModulesRecursive(moduleConfig.true_modules);
                }
              } else {
                LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `条件がFalseのため、Falseブランチを実行します。`);
                if (moduleConfig.false_modules && moduleConfig.false_modules.length > 0) {
                  executeModulesRecursive(moduleConfig.false_modules);
                }
              }
            } else { // This handles generic groups
              const groupName = moduleConfig.settings.groupName || moduleConfig.name;
              LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `グループ「${groupName}」内のモジュールを実行します...`);
              if (moduleConfig.modules && moduleConfig.modules.length > 0) {
                executeModulesRecursive(moduleConfig.modules);
              }
              LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `グループ「${groupName}」の実行が完了しました。`);
            }
          } else {
            const result = this._executeModuleLogic(moduleDef, moduleConfig.settings, lastReturnValue);
            if (moduleDef.returnsValue) {
              lastReturnValue = result;
              const resultInfo = Array.isArray(result) ? `${result.length}件のアイテム` : String(result);
              LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `結果: ${resultInfo} を次のモジュールに渡します。`);
            } else {
              lastReturnValue = null;
            }
          }
        }
      };

      LogService.writeDetailLog(logContext.runId, workflowName, 'Workflow Engine', '', '情報', `ワークフロー「${workflowName}」の実行を開始します...`);
      executeModulesRecursive(modules);
      LogService.writeDetailLog(logContext.runId, workflowName, 'Workflow Engine', '', '成功', 'ワークフローの実行が正常に完了しました。');
    } catch (e) {
      status = '失敗';
      LogService.writeDetailLog(logContext.runId, workflowName, 'Workflow Engine', '', '失敗', `[致命的なエラー] ${e.message} ワークフローの実行を中断しました。`);
      Logger.log(e.stack);
    } finally {
      const endTime = new Date();
      const executionTime = (endTime.getTime() - startTime.getTime()) / 1000;
      LogService.endLog(logContext.runId, logContext.summaryRow, status, executionTime);
      lock.releaseLock();
    }
    // Return the logs for this run to the client
    return LogService.getDetailLogsByRunId(logContext.runId);
  },

  /**
   * モジュール定義に基づいて実際の処理を実行する
   * @private
   * @param {object} moduleDef - モジュールの定義オブジェクト (JSON)
   * @param {object} moduleSettings - ユーザーがUIで設定した値
   * @param {*} inputValue - 前のモジュールからの入力値 (後方互換性のために維持)
   * @returns {*} モジュールの処理結果
   */
  _executeModuleLogic: function (moduleDef, moduleSettings, inputValue) {
    // 新アーキテクチャ: 'logic' キーが存在する場合、インタープリタで実行
    if (moduleDef.logic && Array.isArray(moduleDef.logic)) {
      return ExecutionService.execute(moduleDef.logic, moduleSettings);
    }

    // 従来アーキテクチャ: 'handler' キーが存在する場合、FunctionRegistry経由で実行
    if (moduleDef.handler) {
      try {
        return FunctionRegistry.run(moduleDef.handler, moduleSettings, inputValue);
      } catch (e) {
        throw new Error(`Handler '${moduleDef.handler}' execution failed: ${e.message}`);
      }
    }

    // 実行可能なロジックが見つからない場合
    throw new Error(`No valid 'logic' or 'handler' found in module definition for ID: ${moduleDef.id}`);
  },

  /**
   * 単体のモジュールを実行し、ログを返す
   * @param {string} workflowName - 現在のワークフロー名
   * @param {object} moduleConfig - 実行するモジュールの設定
   * @param {string} moduleFolderId - モジュール定義が保存されているフォルダID
   * @returns {Array<object>} 実行ログの配列
   */
  runSingleModule: function (workflowName, moduleConfig, moduleFolderId) {
    const lock = LockService.getScriptLock();
    const lockAcquired = lock.tryLock(30000); // 30秒待機

    if (!lockAcquired) {
      LogService.logSkippedExecution(workflowName, '単体実行');
      return [{ level: 'system', message: '他のプロセスが実行中のため、実行はスキップされました。' }];
    }

    // モジュール定義をロード
    if (moduleFolderId) {
      ModuleService.loadModuleDefinitions(moduleFolderId);
    }

    const startTime = new Date();
    const executionType = '単体実行';
    let logContext = LogService.startLog(workflowName, executionType);
    let status = '成功';

    try {
      const moduleDef = ModuleService.getModuleById(moduleConfig.id);
      if (!moduleDef) {
        throw new Error(`モジュール「${moduleConfig.id}」の定義が見つかりません。`);
      }

      LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `単体実行: モジュール「${moduleConfig.name}」を開始します...`);

      // モジュールロジックを実行（入力値はnull）
      const result = this._executeModuleLogic(moduleDef, moduleConfig.settings, null);

      if (moduleDef.returnsValue) {
        const resultInfo = Array.isArray(result) ? `${result.length}件のアイテム` : String(result);
        LogService.writeDetailLog(logContext.runId, workflowName, moduleConfig.name, moduleConfig.instanceId, '情報', `結果: ${resultInfo}`);
      }

      LogService.writeDetailLog(logContext.runId, workflowName, 'Workflow Engine', '', '成功', 'モジュールの単体実行が正常に完了しました。');

    } catch (e) {
      status = '失敗';
      LogService.writeDetailLog(logContext.runId, workflowName, 'Workflow Engine', '', '失敗', `[致命的なエラー] ${e.message} モジュールの実行を中断しました。`);
      Logger.log(e.stack);
    } finally {
      const endTime = new Date();
      const executionTime = (endTime.getTime() - startTime.getTime()) / 1000;
      LogService.endLog(logContext.runId, logContext.summaryRow, status, executionTime);
      lock.releaseLock();
    }
    
    // ログを返却
    return LogService.getDetailLogsByRunId(logContext.runId);
  },
};
