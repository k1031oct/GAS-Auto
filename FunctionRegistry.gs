/**
 * @file FunctionRegistry.gs
 * @description A central registry for all executable module functions.
 * This follows an interpreter pattern, where function handlers are registered
 * with a string key and can be invoked dynamically.
 */
const FunctionRegistry = {
  _registry: {},

  /**
   * Registers a function with a given key.
   * @param {string} key - The unique identifier for the function (e.g., "DriveService.moveFile").
   * @param {Function} func - The function to register.
   */
  register: function (key, func) {
    if (typeof func !== 'function') {
      throw new Error(`Invalid function provided for key: ${key}`);
    }
    if (this._registry[key]) {
      Logger.log(`Warning: Overwriting function for key: ${key}`);
    }
    this._registry[key] = func;
  },

  /**
   * Retrieves a function from the registry.
   * @param {string} key - The key of the function to retrieve.
   * @returns {Function} The requested function.
   */
  get: function (key) {
    const func = this._registry[key];
    if (!func) {
      throw new Error(`No function registered for key: ${key}`);
    }
    return func;
  },

  /**
   * Executes a registered function by its key.
   * @param {string} key - The key of the function to execute.
   * @param {Array<any>} args - An array of arguments to pass to the function.
   * @returns {*} The return value of the executed function.
   */
  run: function (key, ...args) {
    const func = this.get(key);
    // The 'this' context for the called function will be null.
    // Service methods should not rely on 'this'.
    return func.apply(null, args);
  },

  /**
   * Registers an entire service object.
   * @param {string} serviceName - The name of the service (e.g., "DriveService").
   * @param {object} serviceObject - The service object containing the methods.
   */
  registerService: function (serviceName, serviceObject) {
    for (const methodName in serviceObject) {
      if (Object.prototype.hasOwnProperty.call(serviceObject, methodName) && typeof serviceObject[methodName] === 'function') {
        const key = `${serviceName}.${methodName}`;
        this.register(key, serviceObject[methodName].bind(serviceObject));
      }
    }
  }
};
