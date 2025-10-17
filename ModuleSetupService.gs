/**
 * @file ModuleSetupService.gs
 * @description Handles the initial setup of default modules for the application.
 */
const ModuleSetupService = {
  _APP_DATA_FOLDER_NAME: 'GAS_Workflow_Automator_AppData',
  _MODULES_ROOT_FOLDER_NAME: 'Modules',
  _STANDARD_MODULES_FOLDER_NAME: 'StandardModules',

  /**
   * Gets or creates the root folder for application data.
   * @private
   * @returns {GoogleAppsScript.Drive.Folder} The application data folder.
   */
  _getAppDataFolder: function () {
    const folders = DriveApp.getFoldersByName(this._APP_DATA_FOLDER_NAME);
    if (folders.hasNext()) {
      return folders.next();
    }
    return DriveApp.getRootFolder().createFolder(this._APP_DATA_FOLDER_NAME);
  },

  /**
   * Gets or creates the root folder for all modules.
   * @private
   * @returns {GoogleAppsScript.Drive.Folder} The modules root folder.
   */
  _getModulesRootFolder: function () {
    const appDataFolder = this._getAppDataFolder();
    const folders = appDataFolder.getFoldersByName(this._MODULES_ROOT_FOLDER_NAME);
    if (folders.hasNext()) {
      return folders.next();
    }
    return appDataFolder.createFolder(this._MODULES_ROOT_FOLDER_NAME);
  },

  /**
   * Gets or creates the folder for standard modules.
   * @private
   * @returns {GoogleAppsScript.Drive.Folder} The standard modules folder.
   */
  _getStandardModulesFolder: function () {
    const modulesRootFolder = this._getModulesRootFolder();
    const folders = modulesRootFolder.getFoldersByName(this._STANDARD_MODULES_FOLDER_NAME);
    if (folders.hasNext()) {
      return folders.next();
    }
    return modulesRootFolder.createFolder(this._STANDARD_MODULES_FOLDER_NAME);
  },

  /**
   * Sets up the default modules if they haven't been created yet.
   * @returns {string} The ID of the standard modules folder.
   */
  setupDefaultModules: function () {
    const standardModulesFolder = this._getStandardModulesFolder();
    const folderId = standardModulesFolder.getId();

    const defaultModules = [
      this._getDriveFilterFilesModule(),
      this._getDriveConvertFilesModule(),
      this._getDriveArchiveFilesModule(),
    ];

    defaultModules.forEach(moduleDef => {
      const fileName = `${moduleDef.id}.json`;
      const files = standardModulesFolder.getFilesByName(fileName);
      if (!files.hasNext()) {
        standardModulesFolder.createFile(fileName, JSON.stringify(moduleDef, null, 2), MimeType.JSON);
      }
    });
    
    // Save the folder ID to user properties so ModuleService can find it.
    PropertiesService.getUserProperties().setProperty(ModuleService._DEFAULT_MODULE_FOLDER_KEY, folderId);

    return folderId;
  },

  /**
   * Defines the drive_filter_files module.
   * @private
   */
  _getDriveFilterFilesModule: function() {
    return {
      "id": "drive_filter_files",
      "name": "1. ファイルの絞り込み",
      "description": "指定フォルダ内のファイルを種類やキーワードで絞り込み、後続のモジュールに渡します。",
      "returnsValue": true,
      "settings": [
        { "id": "sourceFolderId", "label": "対象フォルダID", "type": "string", "required": true, "placeholder": "ファイルを絞り込むGoogle DriveのフォルダID" },
        { "id": "filterFileType", "label": "ファイル種別", "type": "dropdown", "options": ["any", "excel", "csv", "pdf", "spreadsheet"], "defaultValue": "any" },
        { "id": "filterKeyword", "label": "キーワード", "type": "string", "placeholder": "(任意) ファイル名に含まれるキーワード" },
        { "id": "filterCondition", "label": "キーワード条件", "type": "dropdown", "options": ["contains", "not_contains", "starts_with"], "defaultValue": "contains" },
        { "id": "moveToFolderId", "label": "中間フォルダID", "type": "string", "placeholder": "(任意) 絞り込んだファイルの移動先フォルダID" }
      ]
    };
  },

  /**
   * Defines the drive_convert_files module.
   * @private
   */
  _getDriveConvertFilesModule: function() {
    return {
      "id": "drive_convert_files",
      "name": "2. ファイルの変換",
      "description": "ExcelやCSVファイルをGoogleスプレッドシートに変換します。",
      "returnsValue": true,
      "settings": [
        { "id": "actionType", "label": "変換タイプ", "type": "dropdown", "options": ["excel_to_sheet", "csv_to_sheet"], "required": true },
        { "id": "outputFolderId", "label": "出力先フォルダID", "type": "string", "placeholder": "(Excel→Sheet変換時) 変換後のファイルの保存先" },
        { "id": "templateSheetUrl", "label": "テンプレートシートURL", "type": "string", "placeholder": "(CSV→Sheet読込時) 読み込み先のスプレッドシートURL" }
      ]
    };
  },

  /**
   * Defines the drive_archive_files module.
   * @private
   */
  _getDriveArchiveFilesModule: function() {
    return {
      "id": "drive_archive_files",
      "name": "3. ファイルのアーカイブ",
      "description": "ファイルを指定のルールに基づき、アーカイブフォルダに整理・保存します。",
      "returnsValue": true,
      "settings": [
        { "id": "archiveRootFolderId", "label": "アーカイブ先ルートフォルダID", "type": "string", "required": true },
        { "id": "archiveFormat", "label": "フォルダ形式", "type": "dropdown", "options": ["YYYY/MM", "YYYY"], "defaultValue": "YYYY/MM" },
        { "id": "renameKeyword", "label": "リネーム用キーワード", "type": "string", "placeholder": "(任意) ファイル名の先頭に追加するキーワード" },
        { "id": "overwriteAction", "label": "同名ファイルの処理", "type": "dropdown", "options": ["skip", "overwrite", "add_sequence"], "defaultValue": "skip" }
      ]
    };
  }
};
