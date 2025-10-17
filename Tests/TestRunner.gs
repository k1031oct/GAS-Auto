/**
 * @OnlyCurrentDoc
 *
 * The above comment directs Apps Script to limit the scope of file
 * access for this add-on. It specifies that the add-on will only
 * attempt to read or modify the files in which it is used, and not
 * any other files in the user's Drive. Putting this comment in the
 * script file forces handlers to exist in the same file.
 */


/**
 *
 * @param e
 * @returns {*}
 */
function doGet(e) {

  if (e.parameter.page === 'TestRunner') {
    return GasT.run();
  }

  return HtmlService.createTemplateFromFile('index').evaluate();
}


/**
 *
 */
function runAllTests() {
  GasT.run();
}

/**
 *
 * @returns {string}
 */
function getTestData() {
  const testData = {
    'WorkflowService': {
      'createDraft': ['arg1', 'arg2'],
      'sendDraft': ['arg1']
    },
    'ModuleService': {
      'createModule': ['arg1', 'arg2'],
      'updateModule': ['arg1']
    }
  };
  return JSON.stringify(testData);
}
