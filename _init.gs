/**
 * @file _init.gs
 * @description Initializes the application by registering all services with the FunctionRegistry.
 * This ensures that all module handlers are available for dynamic invocation.
 */
function _initializeRegistry() {
  FunctionRegistry.registerService('DriveService', DriveService);
  FunctionRegistry.registerService('SheetService', SheetService);
  FunctionRegistry.registerService('GmailService', GmailService);
  FunctionRegistry.registerService('CalendarService', CalendarService);
  FunctionRegistry.registerService('DocsService', DocsService);
  // Add other services here as they are created.
}

// Run initialization when the script is loaded.
_initializeRegistry();
