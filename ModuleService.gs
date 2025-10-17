/**
 * @file ModuleService.gs
 * @description Google Driveからモジュール定義 (JSON) を動的にロードする。
 */
var ModuleService = {
  _APP_DATA_FOLDER_NAME: 'GAS_Workflow_Automator_AppData',
  // ユーザープロパティに最後に使用したモジュールフォルダIDを保存するためのキー
  _DEFAULT_MODULE_FOLDER_KEY: 'MODULE_JSON_FOLDER_ID',

  /**
   * モジュールJSONファイルが格納されているフォルダIDを取得する
   * @private
   */
  _getDefaultModuleFolderId: function () {
    return PropertiesService.getUserProperties().getProperty(this._DEFAULT_MODULE_FOLDER_KEY);
  },

  /**
   * Google Driveの指定されたフォルダから、モジュール定義のJSONファイルを読み込む
   * @param {string} folderId - モジュールJSONファイルが格納されているGoogle DriveのフォルダID
   * @returns {Array<object>} ロードされたモジュール定義の配列
   */
  loadModuleDefinitions: function (folderId) {
    if (!folderId) {
      Logger.log("モジュールフォルダIDが未指定です。カスタムモジュールはロードされません。");
      return [];
    }
    
    // ユーザープロパティにフォルダIDを保存
    PropertiesService.getUserProperties().setProperty(this._DEFAULT_MODULE_FOLDER_KEY, folderId);

    const loadedModules = [];
    let folder;
    
    try {
      folder = DriveApp.getFolderById(folderId);
    } catch (e) {
      Logger.log(`指定されたフォルダID「${folderId}」が見つからないか、アクセスできません。エラー: ${e.message}`);
      return [];
    }

    const jsonFiles = folder.getFilesByType(MimeType.JSON);
    
    while (jsonFiles.hasNext()) {
      const file = jsonFiles.next();
      try {
        const content = file.getBlob().getDataAsString();
        const moduleDef = JSON.parse(content);
        
        // 必須フィールドのチェック（最低限のバリデーション）
        if (moduleDef && moduleDef.id && moduleDef.name && moduleDef.settings) {
          // 既存のデフォルトモジュールIDと衝突しないかチェック
          if (loadedModules.findIndex(m => m.id === moduleDef.id) === -1) {
            loadedModules.push(moduleDef);
          } else {
             Logger.log(`警告: モジュールID「${moduleDef.id}」は既に存在するためスキップされました。`);
          }
        } else {
          Logger.log(`警告: ファイル「${file.getName()}」は有効なモジュールJSON形式ではありません。`);
        }
      } catch (e) {
        Logger.log(`JSONファイル「${file.getName()}」の解析に失敗しました: ${e.message}`);
      }
    }

    Logger.log(`Driveから ${loadedModules.length} 個のカスタムモジュールをロードしました。`);
    return loadedModules;
  },
  
  /**
   * IDからモジュール定義を取得する (サーバーサイド用)
   * @param {string} moduleId
   */
  getModuleById: function (moduleId) {
    const folderId = this._getDefaultModuleFolderId();
    const allModules = this.loadModuleDefinitions(folderId);
    return allModules.find(m => m.id === moduleId);
  },

  // 統合後の getInitialData() から ModuleService を呼び出すための関数
  getModules: function () {
    return this.loadModuleDefinitions(this._getDefaultModuleFolderId());
  },

  /**
   * 指定されたフォルダに初期モジュールJSONファイルを生成する
   * @param {string} folderId - ファイルを生成するGoogle DriveのフォルダID
   * @returns {string} 生成結果のメッセージ
   */
  createInitialModules: function (folderId) {
    const defaultModules = [
      // Existing modules
      {
        "id": "drive_move_file",
        "name": "ドライブファイルを移動",
        "description": "指定されたファイルをGoogle Driveの別のフォルダに移動します。",
        "category": "Google Drive",
        "icon": "fa-folder-open",
        "type": "unit",
        "settings": [
          { "id": "sourceFileUrl", "name": "移動元ファイルURL", "type": "text", "required": true, "description": "移動するファイルのURLを入力します。" },
          { "id": "destinationFolderUrl", "name": "移動先フォルダURL", "type": "text", "required": true, "description": "移動先のフォルダのURLを入力します。" }
        ],
        "handler": "DriveService.moveFile"
      },
      {
        "id": "sheets_update_cell",
        "name": "スプレッドシートのセルを更新",
        "description": "指定されたスプレッドシートのセルに値を書き込みます。",
        "category": "Google Sheets",
        "icon": "fa-file-excel",
        "type": "unit",
        "settings": [
          { "id": "spreadsheetUrl", "name": "スプレッドシートURL", "type": "text", "required": true },
          { "id": "sheetName", "name": "シート名", "type": "text", "required": true },
          { "id": "cell", "name": "セル（例: A1）", "type": "text", "required": true },
          { "id": "value", "name": "書き込む値", "type": "text", "required": true }
        ],
        "handler": "SheetService.updateCell"
      },
      // New Organizer Modules
      {
        "id": "drive_filter_files",
        "name": "ファイル絞り込み",
        "description": "指定フォルダ内のファイルを種類やキーワードで絞り込みます。",
        "category": "Google Drive",
        "icon": "fa-filter",
        "type": "unit",
        "returnsValue": true,
        "settings": [
          { "id": "sourceFolderId", "name": "対象フォルダID", "type": "text", "required": true },
          { "id": "filterFileType", "name": "ファイル種別", "type": "select", "required": true, "options": ["any", "excel", "csv", "pdf"], "defaultValue": "any" },
          { "id": "filterKeyword", "name": "キーワード", "type": "text", "required": false },
          { "id": "filterCondition", "name": "キーワード条件", "type": "select", "required": false, "options": ["contains", "does_not_contain", "starts_with", "ends_with"], "defaultValue": "contains" },
          { "id": "moveToFolderId", "name": "中間フォルダID (任意)", "type": "text", "required": false }
        ]
      },
      {
        "id": "drive_convert_files",
        "name": "ファイル変換",
        "description": "ファイルをGoogleスプレッドシートなどの形式に変換します。",
        "category": "Google Drive",
        "icon": "fa-sync-alt",
        "type": "unit",
        "returnsValue": true,
        "settings": [
          { "id": "actionType", "name": "変換タイプ", "type": "select", "required": true, "options": ["excel_to_sheet", "csv_to_sheet"] },
          { "id": "outputFolderId", "name": "出力先フォルダID (Excel→Sheet)", "type": "text", "required": false },
          { "id": "templateSheetUrl", "name": "読込先シートURL (CSV→Sheet)", "type": "text", "required": false }
        ]
      },
      {
        "id": "drive_archive_files",
        "name": "ファイルアーカイブ",
        "description": "ファイルを指定のルールでアーカイブフォルダに整理します。",
        "category": "Google Drive",
        "icon": "fa-archive",
        "type": "unit",
        "returnsValue": true,
        "settings": [
          { "id": "archiveRootFolderId", "name": "アーカイブ先ルートフォルダID", "type": "text", "required": true },
          { "id": "archiveFormat", "name": "フォルダ形式", "type": "select", "required": false, "options": ["YYYY/MM", "YYYY"], "defaultValue": "YYYY/MM" },
          { "id": "renameKeyword", "name": "リネームキーワード (任意)", "type": "text", "required": false },
          { "id": "overwriteAction", "name": "上書き処理", "type": "select", "required": false, "options": ["skip", "overwrite", "add_sequence"], "defaultValue": "skip" }
        ]
      }
    ];

    try {
      const folder = DriveApp.getFolderById(folderId);
      let createdCount = 0;
      
      defaultModules.forEach(module => {
        const fileName = `${module.id}.json`;
        const files = folder.getFilesByName(fileName);
        
        // 同じ名前のファイルが存在しない場合のみ作成
        if (!files.hasNext()) {
          folder.createFile(fileName, JSON.stringify(module, null, 2), MimeType.JSON);
          createdCount++;
        }
      });

      if (createdCount > 0) {
        return `${createdCount}個の初期モジュールを生成しました。`;
      } else {
        return '初期モジュールは既に存在するため、生成をスキップしました。';
      }
    } catch (e) {
      Logger.log(`初期モジュールの生成に失敗しました: ${e.message}`);
      throw new Error(`初期モジュールの生成に失敗しました。フォルダIDが正しいか、アクセス権があるか確認してください。`);
    }
  }
};
