var SheetService = {
  updateCell: function(settings) {
    const spreadsheetId = _extractIdFromUrl(settings.spreadsheetUrl);
    if (!spreadsheetId) throw new Error('Invalid Spreadsheet URL.');
    const ss = SpreadsheetApp.openById(spreadsheetId);
    const sheet = ss.getSheetByName(settings.sheetName);
    if (!sheet) throw new Error(`Sheet "${settings.sheetName}" not found.`);
    sheet.getRange(settings.cell).setValue(settings.value);
    return `Cell ${settings.cell} in ${settings.sheetName} updated.`;
  },
  appendRow: function(settings) {
    const spreadsheetId = _extractIdFromUrl(settings.spreadsheetUrl);
    if (!spreadsheetId) throw new Error('Invalid Spreadsheet URL.');
    const ss = SpreadsheetApp.openById(spreadsheetId);
    const sheet = ss.getSheetByName(settings.sheetName);
    if (!sheet) throw new Error(`Sheet "${settings.sheetName}" not found.`);
    const rowData = settings.rowData.split(',').map(item => item.trim());
    sheet.appendRow(rowData);
    return `Row appended to ${settings.sheetName}.`;
  }
};