// GasT - Google Apps Script Testing Framework

function mock(obj, functionName, returnValue) {
  var originalFunction = obj[functionName];
  obj[functionName] = function() {
    return returnValue;
  };
  return originalFunction;
}


GasT.describe('WorkflowService', function() {

  GasT.it('should create a draft', function() {
    var mockDriveApp = {
      createFile: function(name, content) {
        return {
          getId: function() {
            return 'mock-file-id';
          }
        };
      }
    };

    var originalDriveApp = mock(this, 'DriveApp', mockDriveApp);

    var result = WorkflowService.createDraft('test subject', 'test body');

    GasT.assert(result.getId(), 'mock-file-id', 'createDraft should return a file with a mock id');

    this['DriveApp'] = originalDriveApp;
  });

  GasT.it('should send a draft', function() {
    var mockGmailApp = {
      sendEmail: function(recipient, subject, body) {
        // Do nothing
      }
    };

    var originalGmailApp = mock(this, 'GmailApp', mockGmailApp);

    WorkflowService.sendDraft('mock-draft-id');

    this['GmailApp'] = originalGmailApp;
  });
});


GasT.describe('ModuleService', function() {

  var FOLDER_ID = 'test-folder-id';
  var mockDriveApp, mockCacheService, originalDriveApp, originalCacheService;
  var mockCache;

  // モックファイルデータ
  var mockFiles = [
    { name: 'module1.json', content: '{"id":"m1","name":"Module 1","settings":[]}' },
    { name: 'module2.json', content: '{"id":"m2","name":"Module 2","settings":[]}' }
  ];

  GasT.beforeEach(function() {
    // DriveAppのモック
    var fileIterator = {
      files: mockFiles.slice(), // コピーを渡す
      hasNext: function() { return this.files.length > 0; },
      next: function() { return {
        getBlob: () => ({
          getDataAsString: () => this.files.shift().content
        }),
        getName: () => 'mockName.json'
      }; }
    };

    mockDriveApp = {
      getFolderById: function(folderId) {
        if (folderId === FOLDER_ID) {
          return {
            getFilesByType: function(mimeType) {
              if (mimeType === "application/json") {
                return fileIterator;
              }
            }
          };
        }
        return null;
      }
    };

    // CacheServiceのモック
    var cacheStore = {};
    mockCache = {
      get: function(key) { return cacheStore[key] || null; },
      put: function(key, value, timeout) { cacheStore[key] = value; },
      store: cacheStore // テスト用に内部ストアを公開
    };
    mockCacheService = {
      getScriptCache: function() { return mockCache; }
    };
    
    // グローバルオブジェクトをモックに差し替え
    originalDriveApp = mock(this, 'DriveApp', mockDriveApp);
    originalCacheService = mock(this, 'CacheService', mockCacheService);
    
    // PropertiesServiceの基本的なモックも用意
    mock(PropertiesService, 'getUserProperties', {
      getProperty: function() { return FOLDER_ID; },
      setProperty: function() {} // Do nothing
    });
  });

  GasT.afterEach(function() {
    // モックを元に戻す
    this['DriveApp'] = originalDriveApp;
    this['CacheService'] = originalCacheService;
  });

  GasT.it('should load modules from Drive and set cache when cache is empty', function() {
    // 実行
    var modules = ModuleService.loadModuleDefinitions(FOLDER_ID);

    // 検証
    GasT.assert(modules.length, 2, 'should load two modules from Drive');
    GasT.assert(modules[0].id, 'm1', 'first module ID should be m1');
    GasT.assert(modules[1].name, 'Module 2', 'second module name should be "Module 2"');

    // キャッシュが正しく設定されたか検証
    var cached = mockCache.get('modules_cache_' + FOLDER_ID);
    GasT.assert(cached !== null, true, 'modules should be cached');
    GasT.assert(JSON.parse(cached).length, 2, 'cached data should contain two modules');
  });

  GasT.it('should load modules from cache when cache is available', function() {
    // 事前にキャッシュを設定
    var cachedModules = [{id: 'cached_m1', name: 'Cached Module 1', settings:[]}];
    mockCache.put('modules_cache_' + FOLDER_ID, JSON.stringify(cachedModules));

    // DriveApp.getFolderByIdが呼ばれないことを確認するためのスパイ
    var driveSpy = { called: false };
    mockDriveApp.getFolderById = function() {
      driveSpy.called = true;
      return null; // 本来のモックは呼ばれないはず
    };
    
    // 実行
    var modules = ModuleService.loadModuleDefinitions(FOLDER_ID);

    // 検証
    GasT.assert(driveSpy.called, false, 'DriveApp.getFolderById should not be called when cache is hit');
    GasT.assert(modules.length, 1, 'should load one module from cache');
    GasT.assert(modules[0].id, 'cached_m1', 'should return the cached module');
  });
});


GasT.describe('LogService', function() {
  GasT.it('should add a detail log via addLog', function() {
    let capturedArgs = null;
    const originalWriteDetailLog = LogService.writeDetailLog;
    LogService.writeDetailLog = function(...args) {
      capturedArgs = args;
    };

    LogService._workflowNames = {}; // Reset state

    const workflowName = 'Test Workflow';
    const logContext = LogService.startLog(workflowName, 'Manual');
    const runId = logContext.runId;
    const message = 'This is a test message';

    LogService.addLog(runId, 'info', message);

    GasT.assert(capturedArgs !== null, true, 'writeDetailLog should have been called.');
    if (capturedArgs) {
      GasT.assert(capturedArgs[0], runId, 'Argument 1 (runId) should match.');
      GasT.assert(capturedArgs[1], workflowName, 'Argument 2 (workflowName) should match.');
      GasT.assert(capturedArgs[2], 'Workflow Engine', 'Argument 3 (moduleName) should be "Workflow Engine".');
      GasT.assert(capturedArgs[3], '', 'Argument 4 (instanceId) should be empty.');
      GasT.assert(capturedArgs[4], '情報', 'Argument 5 (status) should be "情報".');
      GasT.assert(capturedArgs[5], message, 'Argument 6 (message) should match.');
    }

    // Cleanup
    LogService.writeDetailLog = originalWriteDetailLog;
    delete LogService._workflowNames[runId];
  });
});

GasT.describe('ExecutionService', function() {
  var originalFunctionRegistryGet;

  GasT.beforeEach(function() {
    // Mock FunctionRegistry.get before each test
    originalFunctionRegistryGet = FunctionRegistry.get;
  });

  GasT.afterEach(function() {
    // Restore original FunctionRegistry.get after each test
    FunctionRegistry.get = originalFunctionRegistryGet;
  });

  GasT.it('should execute a simple logic step', function() {
    FunctionRegistry.get = function(key) {
      if (key === 'test.add') {
        return function(args) { return args.a + args.b; };
      }
      return null;
    };
    
    const logic = [{ func: 'test.add', args: { a: 5, b: 10 } }];
    const result = ExecutionService.execute(logic, {});
    
    GasT.assert(result, 15, 'should return the sum of the arguments');
  });

  GasT.it('should resolve placeholders from initial context', function() {
    let calledWith = null;
    FunctionRegistry.get = function(key) {
      if (key === 'test.echo') {
        return function(args) { calledWith = args.message; return args.message; };
      }
      return null;
    };

    const logic = [{ func: 'test.echo', args: { message: '{{greeting}}' } }];
    const initialContext = { greeting: 'Hello World' };
    ExecutionService.execute(logic, initialContext);

    GasT.assert(calledWith, 'Hello World', 'should have been called with the resolved placeholder value');
  });

  GasT.it('should pass outputs between steps', function() {
    FunctionRegistry.get = function(key) {
      if (key === 'test.getValue') {
        return function() { return 'important_value'; };
      }
      if (key === 'test.processValue') {
        return function(args) { return `processed_${args.data}`; };
      }
      return null;
    };

    const logic = [
      { func: 'test.getValue', resultTo: 'firstResult' },
      { func: 'test.processValue', args: { data: '{{outputs.firstResult}}' } }
    ];
    const result = ExecutionService.execute(logic, {});

    GasT.assert(result, 'processed_important_value', 'should correctly pass output from the first step to the second');
  });

  GasT.it('should throw a security error for unregistered functions', function() {
    FunctionRegistry.get = function(key) { return null; }; // Simulate function not found

    const logic = [{ func: 'unregistered.function', args: {} }];
    
    GasT.assertThrows(function() {
      ExecutionService.execute(logic, {});
    }, /No valid 'logic' or 'handler' found/, 'should throw an error for invalid module definition');
  });
});

GasT.describe('ArchiveService', function() {

  // Mocks for various services
  var mockDriveApp, mockDrive, mockLogService;
  var originalDriveApp, originalDrive, originalLogService;
  var createdFolders, createdFiles, trashedFiles;

  // Helper to create a mock file object
  const createMockFile = (id, name, dateCreated) => ({
    getId: () => id,
    getName: () => name,
    getBlob: () => ({}),
    moveTo: function(targetFolder) {
      const fileIndex = createdFiles.findIndex(f => f.id === this.getId());
      if (fileIndex > -1) {
        createdFiles[fileIndex].parentId = targetFolder.getId();
      }
    },
    getDateCreated: () => dateCreated || new Date(),
    setTrashed: function(trashed) {
      if (trashed) {
        trashedFiles.push(this.getId());
      }
    }
  });

  // Helper to create a mock folder object
  const createMockFolder = (id, name) => ({
    id: id,
    name: name,
    files: [],
    folders: [],
    getId: function() { return this.id; },
    getName: function() { return this.name; },
    getFiles: function() {
      const folderFiles = createdFiles.filter(f => f.parentId === this.id);
      let index = 0;
      return {
        hasNext: () => index < folderFiles.length,
        next: () => folderFiles[index++]
      };
    },
    getFoldersByName: function(folderName) {
      const foundFolders = this.folders.filter(f => f.name === folderName);
      let index = 0;
      return {
        hasNext: () => index < foundFolders.length,
        next: () => foundFolders[index++]
      };
    },
    createFolder: function(folderName) {
      const newFolder = createMockFolder(`${this.id}-${folderName}`, folderName);
      this.folders.push(newFolder);
      createdFolders.push(newFolder);
      return newFolder;
    },
    searchFiles: function(query) {
      // Simplified mock search
      const searchTerm = query.match(/title contains '(.*?)'/)[1];
      const matchingFiles = createdFiles.filter(f => f.name.includes(searchTerm));
      let index = 0;
      return {
        hasNext: () => index < matchingFiles.length,
        next: () => matchingFiles[index++]
      };
    }
  });

  GasT.beforeEach(function() {
    createdFolders = [];
    createdFiles = [];
    trashedFiles = [];

    const rootFolder = createMockFolder('root', 'Root');
    
    mockDriveApp = {
      getRootFolder: () => rootFolder,
      getFolderById: (id) => {
        if (id === 'root') return rootFolder;
        return createdFolders.find(f => f.id === id);
      },
      getFileById: (id) => createdFiles.find(f => f.id === id)
    };

    mockDrive = {
      Files: {
        insert: function(resource, blob, options) {
          const newFile = { id: `converted_${Date.now()}`, name: resource.title };
          createdFiles.push(createMockFile(newFile.id, newFile.name));
          return newFile;
        }
      }
    };

    mockLogService = {
      log: function(msg) { console.log('LOG:', msg); },
      error: function(msg) { console.log('ERROR:', msg); }
    };

    originalDriveApp = mock(this, 'DriveApp', mockDriveApp);
    originalDrive = mock(this, 'Drive', mockDrive);
    originalLogService = mock(this, 'LogService', mockLogService);
  });

  GasT.afterEach(function() {
    this['DriveApp'] = originalDriveApp;
    this['Drive'] = originalDrive;
    this['LogService'] = originalLogService;
  });

  GasT.it('getFileDate should extract date from yyyy-mm-dd format', function() {
    const filename = 'Report-2023-10-25.xlsx';
    const result = getFileDate(filename);
    GasT.assert(result.getFullYear(), 2023, 'Year should be 2023');
    GasT.assert(result.getMonth(), 9, 'Month should be 9 (October)');
    GasT.assert(result.getDate(), 25, 'Day should be 25');
  });

  GasT.it('getFileDate should return null for filenames without a date', function() {
    const filename = 'Monthly_Report.xlsx';
    const result = getFileDate(filename);
    GasT.assert(result, null, 'Should return null for no date');
  });

  GasT.it('getOrCreateArchiveFolder should create nested folders correctly', function() {
    const rootFolderName = 'TestArchive';
    const date = new Date(2023, 9, 25); // Oct 25, 2023
    
    const monthFolder = getOrCreateArchiveFolder(rootFolderName, date);

    GasT.assert(monthFolder.getName(), '10', 'Month folder should be named "10"');
    
    const yearFolder = createdFolders.find(f => f.name === '2023');
    GasT.assert(yearFolder !== undefined, true, 'Year folder "2023" should be created');
    
    const rootArchiveFolder = createdFolders.find(f => f.name === rootFolderName);
    GasT.assert(rootArchiveFolder !== undefined, true, 'Root archive folder should be created');
  });

  GasT.it('processAndArchiveFile should archive a new file correctly', function() {
    const excelFile = createMockFile('excel1', 'Report-2023-10-25.xlsx');
    createdFiles.push(excelFile);

    processAndArchiveFile(excelFile, 'MyArchive');

    const convertedFile = createdFiles.find(f => f.id.startsWith('converted_'));
    GasT.assert(convertedFile !== undefined, true, 'File should have been converted');
    
    const monthFolder = createdFolders.find(f => f.name === '10');
    GasT.assert(monthFolder !== undefined, true, 'Month folder should exist');
    GasT.assert(convertedFile.parentId, monthFolder.getId(), 'Converted file should be in the month folder');
  });

  GasT.it('processAndArchiveFile should replace an older file', function() {
    const archiveName = 'MyArchive';
    const date = new Date(2023, 9, 15);
    
    // 1. Create an "existing" file in the archive
    const existingFile = createMockFile('existing1', 'Old-Report-2023-10-15.xlsx', date);
    const monthFolder = getOrCreateArchiveFolder(archiveName, date);
    existingFile.parentId = monthFolder.getId();
    createdFiles.push(existingFile);

    // 2. Process a newer file
    const newExcelFile = createMockFile('excelNew', 'New-Report-2023-10-20.xlsx');
    createdFiles.push(newExcelFile);
    processAndArchiveFile(newExcelFile, archiveName);

    // 3. Assertions
    const convertedFile = createdFiles.find(f => f.id.startsWith('converted_'));
    GasT.assert(trashedFiles.length, 1, 'One file should be trashed');
    GasT.assert(trashedFiles[0], 'existing1', 'The old existing file should be the one trashed');
    GasT.assert(convertedFile.parentId, monthFolder.getId(), 'The new converted file should be in the month folder');
  });

  GasT.it('processAndArchiveFile should discard a new file if its older', function() {
    const archiveName = 'MyArchive';
    const date = new Date(2023, 9, 25);
    
    // 1. Create an "existing" file in the archive
    const existingFile = createMockFile('existing2', 'Newer-Report-2023-10-25.xlsx', date);
    const monthFolder = getOrCreateArchiveFolder(archiveName, date);
    existingFile.parentId = monthFolder.getId();
    createdFiles.push(existingFile);

    // 2. Process an older file
    const oldExcelFile = createMockFile('excelOld', 'Older-Report-2023-10-10.xlsx');
    createdFiles.push(oldExcelFile);
    processAndArchiveFile(oldExcelFile, archiveName);

    // 3. Assertions
    const convertedFile = createdFiles.find(f => f.id.startsWith('converted_'));
    GasT.assert(trashedFiles.length, 1, 'One file should be trashed');
    GasT.assert(trashedFiles[0], convertedFile.getId(), 'The newly converted file should be the one trashed');
    GasT.assert(createdFiles.find(f => f.id === 'existing2').parentId, monthFolder.getId(), 'The existing file should remain in the folder');
  });
});
