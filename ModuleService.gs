/**
 * @file ModuleService.gs
 * @description Google Driveからモジュール定義 (JSON) を動的にロードする。
 * API管理機能はオミット。
 */
var ModuleService = {
  _APP_DATA_FOLDER_NAME: 'GAS_Workflow_Automator_AppData',
  _DEFAULT_MODULE_FOLDER_KEY: 'MODULE_JSON_FOLDER_ID',
  _DEFAULT_MODULES: [
    // 組み込みモジュールとして、必ず必要な制御系モジュールを最低限定義
    { id: 'control_set_variable', name: '変数を設定', icon: 'fa-tag', category: '制御', type: 'unit', returnsValue: true, settings: [] },
    { id: 'control_if_else', name: '条件分岐', icon: 'fa-code-branch', category: '制御', type: 'container', settings: [] },
    { id: 'drive_create_folder', name: 'フォルダを新規作成', icon: 'fa-folder-plus', category: 'ドライブ操作', type: 'unit', returnsValue: true, settings: [] },
    { id: 'sheets_set_value', name: 'セルに値を設定', icon: 'fa-table', category: 'スプレッドシート', type: 'unit', settings: [] },
    // ファイル整理機能の一部をモジュールとして統合 (旧 Organizerのsort, convert, archiveを分割・抽象化)
    { id: 'drive_file_sort', name: 'ファイル絞込・移動', icon: 'fa-filter', category: 'ドライブ操作', type: 'unit', settings: [] },
    { id: 'drive_file_convert', name: 'ファイル変換 (Excel→Sheet)', icon: 'fa-file-excel', category: 'ドライブ操作', type: 'unit', returnsValue: true, settings: [] },
    { id: 'drive_file_archive', name: 'ファイルアーカイブ', icon: 'fa-box-archive', category: 'ドライブ操作', type: 'unit', settings: [] }
  ],

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
      Logger.log("モジュールフォルダIDが未指定です。デフォルトモジュールのみを返します。");
      return this._DEFAULT_MODULES;
    }
    
    // ユーザープロパティにフォルダIDを保存
    PropertiesService.getUserProperties().setProperty(this._DEFAULT_MODULE_FOLDER_KEY, folderId);

    const loadedModules = [...this._DEFAULT_MODULES];
    let folder;
    
    try {
      folder = DriveApp.getFolderById(folderId);
    } catch (e) {
      Logger.log(`指定されたフォルダID「${folderId}」が見つからないか、アクセスできません。エラー: ${e.message}`);
      return this._DEFAULT_MODULES;
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

    Logger.log(`Driveから ${loadedModules.length - this._DEFAULT_MODULES.length} 個のカスタムモジュールをロードしました。`);
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
  }
};
