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
    const defaultModules = this._getDefaultModules();

    if (!folderId) {
      Logger.log("モジュールフォルダIDが未指定です。デフォルトモジュールのみをロードします。");
      return defaultModules;
    }

    const cache = CacheService.getScriptCache();
    const cacheKey = `modules_cache_${folderId}`;
    const cachedModules = cache.get(cacheKey);

    if (cachedModules) {
      Logger.log('キャッシュからモジュールをロードしました。');
      return JSON.parse(cachedModules);
    }

    // ユーザープロパティにフォルダIDを保存
    PropertiesService.getUserProperties().setProperty(this._DEFAULT_MODULE_FOLDER_KEY, folderId);

    let customModules = [];
    try {
      // DriveApp の代わりに Advanced Drive Service を使用して効率的にファイルを取得
      const query = `'${folderId}' in parents and mimeType='application/json' and trashed=false`;
      Logger.log(`Drive APIクエリ: ${query}`); // デバッグログ
      let pageToken = null;
      let fileCount = 0; // デバッグ用ファイルカウント
      do {
        const response = Drive.Files.list({
          q: query,
          fields: 'nextPageToken, id, name',
          pageToken: pageToken
        });
        Logger.log(`Drive APIレスポンス - ファイル数: ${response.files ? response.files.length : 0}, nextPageToken: ${response.nextPageToken}`); // デバッグログ

        if (response.files) {
          response.files.forEach(file => {
            fileCount++; // ファイルカウント
            try {
              const content = UrlFetchApp.fetch(`https://www.googleapis.com/drive/v3/files/${file.id}?alt=media`, {
                headers: {
                  Authorization: `Bearer ${ScriptApp.getOAuthToken()}`
                },
                muteHttpExceptions: true
              }).getContentText();
              const moduleDef = JSON.parse(content);
              
              if (moduleDef && moduleDef.id && moduleDef.name && moduleDef.settings) {
                customModules.push(moduleDef);
                Logger.log(`  カスタムモジュール追加: ${moduleDef.id} (${file.name})`); // デバッグログ
              } else {
                Logger.log(`警告: ファイル「${file.name}」は有効なモジュールJSON形式ではありません。`);
              }
            } catch (e) {
              Logger.log(`JSONファイル「${file.name}」の解析に失敗しました: ${e.message}`);
            }
          });
        }
        pageToken = response.nextPageToken;
      } while (pageToken);
      Logger.log(`Driveから取得したJSONファイル総数: ${fileCount}`); // デバッグログ

    } catch (e) {
      Logger.log(`指定されたフォルダID「${folderId}」が見つからないか、アクセスできません。エラー: ${e.message}`);
      // フォルダにアクセスできなくても、デフォルトモジュールは返す
    }
    
    Logger.log(`カスタムモジュール (解析成功): ${customModules.length} 個`); // デバッグログ

    // デフォルトモジュールとカスタムモジュールをマージ（カスタムが優先）
    const finalModules = defaultModules.map(defaultModule => {
      const customOverride = customModules.find(customModule => customModule.id === defaultModule.id);
      return customOverride || defaultModule;
    });

    // デフォルトにない完全なカスタムモジュールを追加
    customModules.forEach(customModule => {
      if (!finalModules.some(m => m.id === customModule.id)) {
        finalModules.push(customModule);
      }
    });
    Logger.log(`最終的なモジュール数 (マージ後): ${finalModules.length} 個`); // デバッグログ

    // データをキャッシュに保存（有効期限10分）
    cache.put(cacheKey, JSON.stringify(finalModules), 600); 
    Logger.log(`Driveから ${customModules.length} 個のカスタムモジュールをロードし、合計 ${finalModules.length} 個のモジュールをキャッシュに保存しました。`);
    
    return finalModules;
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
    if (!folderId) {
      throw new Error('フォルダIDが指定されていません。処理を中断しました。');
    }

    const defaultModules = this._getDefaultModules();

    try {
      const folder = DriveApp.getFolderById(folderId);
      let createdCount = 0;
      
      defaultModules.forEach(module => {
        const fileName = `${module.id}.json`;
        const files = folder.getFilesByName(fileName);
        
        // 同じ名前のファイルが存在しない場合のみ作成
        if (!files.hasNext()) {
          folder.createFile(fileName, JSON.stringify(module, null, 2), "application/json");
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
      throw new Error(`初期モジュールの生成に失敗しました: ${e.message}`);
    }
  },

  /**
   * デフォルトで組み込まれているモジュール定義のリストを返す
   * @private
   */
  _getDefaultModules: function() {
    return [
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
        "id": "drive_copy_file",
        "name": "ドライブファイルをコピー",
        "description": "指定されたファイルをGoogle Driveの別の場所にコピーします。",
        "category": "Google Drive",
        "icon": "fa-copy",
        "type": "unit",
        "settings": [
          { "id": "sourceFileUrl", "name": "コピー元ファイルURL", "type": "text", "required": true },
          { "id": "destinationFolderUrl", "name": "コピー先フォルダURL", "type": "text", "required": true },
          { "id": "newFileName", "name": "新しいファイル名（任意）", "type": "text", "required": false }
        ],
        "handler": "DriveService.copyFile"
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
      {
        "id": "sheets_append_row",
        "name": "スプレッドシートに行を追加",
        "description": "指定されたシートの末尾に新しい行を追加します。",
        "category": "Google Sheets",
        "icon": "fa-file-excel",
        "type": "unit",
        "settings": [
          { "id": "spreadsheetUrl", "name": "スプレッドシートURL", "type": "text", "required": true },
          { "id": "sheetName", "name": "シート名", "type": "text", "required": true },
          { "id": "rowData", "name": "追加する行データ（カンマ区切り）", "type": "text", "required": true }
        ],
        "handler": "SheetService.appendRow"
      },
      {
        "id": "gmail_send_email",
        "name": "Gmailでメールを送信",
        "description": "指定された宛先にメールを送信します。",
        "category": "Gmail",
        "icon": "fa-envelope",
        "type": "unit",
        "settings": [
          { "id": "recipient", "name": "宛先メールアドレス", "type": "text", "required": true },
          { "id": "subject", "name": "件名", "type": "text", "required": true },
          { "id": "body", "name": "本文", "type": "textarea", "required": true }
        ],
        "handler": "GmailService.sendEmail"
      },
      {
        "id": "calendar_create_event",
        "name": "カレンダーに予定を作成",
        "description": "Googleカレンダーに新しい予定を作成します。",
        "category": "Google Calendar",
        "icon": "fa-calendar-alt",
        "type": "unit",
        "settings": [
          { "id": "calendarId", "name": "カレンダーID（通常はメールアドレス）", "type": "text", "required": true, "defaultValue": "primary" },
          { "id": "title", "name": "予定のタイトル", "type": "text", "required": true },
          { "id": "startTime", "name": "開始時刻 (例: 2023/10/28 10:00)", "type": "text", "required": true },
          { "id": "endTime", "name": "終了時刻 (例: 2023/10/28 11:00)", "type": "text", "required": true }
        ],
        "handler": "CalendarService.createEvent"
      },
      {
        "id": "docs_create_file",
        "name": "Googleドキュメントを作成",
        "description": "指定されたフォルダに新しいGoogleドキュメントを作成します。",
        "category": "Google Docs",
        "icon": "fa-file-word",
        "type": "unit",
        "settings": [
          { "id": "folderUrl", "name": "作成先フォルダURL", "type": "text", "required": true },
          { "id": "fileName", "name": "ドキュメント名", "type": "text", "required": true },
          { "id": "content", "name": "初期コンテンツ", "type": "textarea", "required": false }
        ],
        "handler": "DocsService.createFile"
      },
      {
        "id": "group_container",
        "name": "グループ化",
        "description": "複数のモジュールをグループ化して、ワークフローを整理します。",
        "category": "構造",
        "icon": "fa-box-open",
        "type": "container",
        "settings": [
          { "id": "groupTitle", "name": "グループタイトル", "type": "text", "defaultValue": "新しいグループ" }
        ],
        "handler": "",
        "branches": [
          { "id": "modules", "name": "モジュール" }
        ]
      },
      {
        "id": "condition_branch",
        "name": "条件分岐",
        "description": "条件に応じて、実行するモジュールを分岐させます。",
        "category": "構造",
        "icon": "fa-code-branch",
        "type": "container",
        "settings": [
          { "id": "condition", "name": "条件", "type": "text", "defaultValue": "" }
        ],
        "handler": "",
        "branches": [
          { "id": "true_modules", "name": "Trueの場合" },
          { "id": "false_modules", "name": "Falseの場合" }
        ]
      }
    ];
  }
};
