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

  GasT.it('should create a module', function() {
    var mockPropertiesService = {
      getScriptProperties: function() {
        return {
          setProperty: function(key, value) {
            // Do nothing
          }
        };
      }
    };

    var originalPropertiesService = mock(this, 'PropertiesService', mockPropertiesService);

    var result = ModuleService.createModule('test module', 'test description');

    GasT.assert(result, 'test module', 'createModule should return the module name');

    this['PropertiesService'] = originalPropertiesService;
  });

  GasT.it('should update a module', function() {
    var mockPropertiesService = {
      getScriptProperties: function() {
        return {
          setProperty: function(key, value) {
            // Do nothing
          },
          getProperty: function(key) {
            return '{"name":"test module","description":"old description"}';
          }
        };
      }
    };

    var originalPropertiesService = mock(this, 'PropertiesService', mockPropertiesService);

    var result = ModuleService.updateModule('test module', 'new description');

    GasT.assert(result.description, 'new description', 'updateModule should return the updated module');

    this['PropertiesService'] = originalPropertiesService;
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
    }, /Security Error: Function "unregistered.function" is not registered or not found./, 'should throw a security error');
  });
});

GasT.describe('WorkflowService._executeModuleLogic', function() {
  var originalExecutionServiceExecute;
  var originalFunctionRegistryRun;

  GasT.beforeEach(function() {
    originalExecutionServiceExecute = ExecutionService.execute;
    originalFunctionRegistryRun = FunctionRegistry.run;
  });

  GasT.afterEach(function() {
    ExecutionService.execute = originalExecutionServiceExecute;
    FunctionRegistry.run = originalFunctionRegistryRun;
  });

  GasT.it('should delegate to ExecutionService for logic-based modules', function() {
    let executed = false;
    ExecutionService.execute = function(logic, context) {
      executed = true;
      GasT.assert(logic.length, 1, 'should receive the logic array');
      GasT.assert(context.setting1, 'value1', 'should receive the initial context');
      return 'logic_executed';
    };

    const moduleDef = { id: 'test-logic-module', logic: [{ func: 'any.func' }] };
    const moduleSettings = { setting1: 'value1' };
    const result = WorkflowService._executeModuleLogic(moduleDef, moduleSettings, null);

    GasT.assert(executed, true, 'ExecutionService.execute should have been called');
    GasT.assert(result, 'logic_executed', 'should return the result from ExecutionService');
  });

  GasT.it('should use FunctionRegistry for handler-based modules', function() {
    let executed = false;
    FunctionRegistry.run = function(handler, settings, inputValue) {
      executed = true;
      GasT.assert(handler, 'OldService.doWork', 'should receive the correct handler');
      GasT.assert(settings.param, 'test', 'should receive the module settings');
      GasT.assert(inputValue, 'input', 'should receive the input value');
      return 'handler_executed';
    };

    const moduleDef = { id: 'test-handler-module', handler: 'OldService.doWork' };
    const moduleSettings = { param: 'test' };
    const result = WorkflowService._executeModuleLogic(moduleDef, moduleSettings, 'input');

    GasT.assert(executed, true, 'FunctionRegistry.run should have been called');
    GasT.assert(result, 'handler_executed', 'should return the result from FunctionRegistry');
  });

  GasT.it('should throw an error if no logic or handler is found', function() {
    const moduleDef = { id: 'invalid-module' }; // No 'logic' or 'handler'
    
    GasT.assertThrows(function() {
      WorkflowService._executeModuleLogic(moduleDef, {}, null);
    }, /No valid 'logic' or 'handler' found/, 'should throw an error for invalid module definition');
  });
});
