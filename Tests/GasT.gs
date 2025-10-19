/**
 * GasT - Google Apps Script Testing Framework
 *
 * GasT is a TAP-based testing framework for Google Apps Script.
 *
 * @author <zixia@zixia.net> (https://zixia.net)
 * @license MIT
 *
 * @see {@link https://github.com/huan/gast} for the home page of GasT.
 * @see {@link http://www.slideshare.net/zixia/gast-google-apps-script-testing-framework} for the introduction slides of GasT.
 */
var GasT = (function (gast) {
  'use strict'

  var VERSION = '2.1.2'

  var totalCaseNum = 0
  var passCaseNum = 0

  var describeTotalCaseNum = 0
  var describePassCaseNum = 0

  var describeTitle = ''


  gast.assert = assert

  gast.describe = describe
  gast.it = it

  gast.funcDoGet = doGet

  gast.version = version

  /////////////////////////////////////////////////////

  function version () {
    return VERSION
  }

  function doGet (e) {
    if (e && e.parameter && e.parameter.output === 'tap') {
      return run(e.parameter.describe, e.parameter.it)
    }

    return HtmlService.createHtmlOutputFromFile('gast-js')
        .setSandboxMode(HtmlService.SandboxMode.IFRAME)
  }

  function run (runDescribe, runIt) {
    var output = ''

    gast.initializers.forEach(function (initializer) {
      initializer()
    })

    var describeRegex, itRegex

    if (runDescribe) {
      describeRegex = new RegExp(runDescribe)
    }
    if (runIt) {
      itRegex = new RegExp(runIt)
    }

    gast.describers.forEach(function (describer) {
      if (describeRegex && !describeRegex.test(describer.title)) {
        return
      }

      var itRunCount = 0

      describer.its.forEach(function (it) {
        if (itRegex && !itRegex.test(it.title)) {
          return
        }
        itRunCount++
        it.func()
      })

      if (itRunCount > 0) {
        output += describer.output
      }

    })

    output += '1..' + totalCaseNum + '\n'

    // Logger.log(output)
    var result = ContentService.createTextOutput(output)
    result.setMimeType(ContentService.MimeType.TEXT)
    return result
  }


  function describe (title, func) {
    if (typeof title !== 'string' || typeof func !== 'function' ) {
      throw new Error('describe(title, func) error!')
    }

    var describer = {
      title: title
      , func: func
      , its: []
      , output: ''
    }

    gast.describers.push(describer)

    function it (title, func) {
      if (typeof title !== 'string' || typeof func !== 'function' ) {
        throw new Error('it(title, func) error!')
      }
      var it = {
        title: title
        , func: func
      }
      describer.its.push(it)
    }

    describeTitle = title

    describeTotalCaseNum = 0
    describePassCaseNum = 0

    func(it) // Call describe func with it func inside.

    if (describeTotalCaseNum > 0) {
      describer.output = '\n# ' + describeTitle + ' ('
                          + describePassCaseNum + '/'
                          + describeTotalCaseNum
                          + ' pass)'
                          + '\n'
    }

  }

  function it (title, func) {
    describe('it', function (it) {
      it(title, func)
    })
  }

  function assert (expression, message, extra) {
    totalCaseNum++
    describeTotalCaseNum++

    var backTrace = getBackTrace()
    var line = backTrace[0]
    var file = backTrace[1]

    if (expression) {
      passCaseNum++
      describePassCaseNum++

      return Logger.log('ok ' + totalCaseNum + ' - ' + message
                + ' - ' + file + ':' + line)

    }

    Logger.log('not ok ' + totalCaseNum + ' - ' + message
              + ' - ' + file + ':' + line)

    if (extra) {
      Logger.log(
        '\n---\n'
        + 'extra: ' + extra + '\n'
        + '...'
      )
    }
  }

  function getBackTrace () {
    var err = new Error()

    if (!err.stack) {
      return []
    }
    /**
     *
     * err.stack sample in v8 engine
     *
     * Error
     * at getBackTrace (gast-debus.js:183:13)
     * at assert (gast-debus.js:154:19)
     * at it_func (test.js:16:11)
     * at describe (gast-debus.js:115:11)
     * at Object.gastTestRunner [as run] (test.js:14:5)
     * at doGet (gast-debus.js:49:15)
     *
     *
     *
     * err.stack sample in Rhino engine
     *
     * Error()@:1
     * getBackTrace(gast.gs:186)
     * assert(gast.gs:157)
     * @(test.gs:22)
     * gastTestRunner(test.gs:21)
     * doGet(Code:6)
     *
     */
    var stack = err.stack.split('\n')
    // Logger.log(stack)

    var callStack = ''
    var file, line

    if (/gast/.test(stack[1])) { // is v8
      callStack = stack[3]
      // Logger.log('v8 stack:' + callStack)
      var matched = callStack.match(
                      /\s\(([^:]+):(\d+):\d+\)$/
                    )
      if (matched) {
        file = matched[1]
        line = matched[2]
      }
    } else { // is Rhino
      callStack = stack[3]
      // Logger.log('Rhino stack:' + callStack)

      // @(test.gs:22)
      var matched = callStack.match(
                      /@\(([^:]+):(\d+)\)$/
                    )
      if (matched) {
        file = matched[1]
        line = matched[2]
      }
    }
    // Logger.log([line, file])
    return [line, file]
  }

  return gast
}(GasT || {
  describers: []
  , initializers: []
}))

