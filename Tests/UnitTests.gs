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
