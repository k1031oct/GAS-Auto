/**
 * @file BookmarkService.gs
 * @description Manages workflow-specific and global URL bookmarks.
 */

const BookmarkService = (function() {

  const GLOBAL_BOOKMARKS_JSON = 'GLOBAL_BOOKMARKS_JSON';

  /**
   * Generates a unique ID for a bookmark.
   * @returns {string} A unique identifier.
   */
  function _generateUniqueId() {
    return new Date().getTime().toString(36) + Math.random().toString(36).slice(2);
  }

  /**
   * Gets the folder for a specific workflow.
   * @param {string} workflowName - The name of the workflow.
   * @returns {GoogleAppsScript.Drive.Folder} The workflow folder.
   */
  function _getWorkflowFolder(workflowName) {
    return WorkflowService.getWorkflowFolder(workflowName);
  }

  /**
   * Gets the bookmarks.json file for a workflow, optionally creating it.
   * @param {string} workflowName - The name of the workflow.
   * @param {boolean} createIfNotExists - Whether to create the file if it doesn't exist.
   * @returns {GoogleAppsScript.Drive.File|null} The bookmarks.json file object or null.
   */
  function _getBookmarkFile(workflowName, createIfNotExists = false) {
    try {
      const workflowFolder = _getWorkflowFolder(workflowName);
      if (!workflowFolder) {
        console.error(`Workflow folder not found for: ${workflowName}`);
        return null;
      }
      const files = workflowFolder.getFilesByName('bookmarks.json');
      if (files.hasNext()) {
        return files.next();
      } else if (createIfNotExists) {
        return workflowFolder.createFile('bookmarks.json', '[]');
      }
      return null;
    } catch (error) {
      console.error(`Error getting bookmark file for workflow ${workflowName}: ${error.toString()}`);
      LogService.log(`Error getting bookmark file for workflow ${workflowName}: ${error.toString()}`);
      return null;
    }
  }

  // --- Workflow Bookmarks ---

  /**
   * Gets all bookmarks for a specific workflow.
   * @param {string} workflowName - The name of the workflow.
   * @returns {Array<Object>} An array of bookmark objects.
   */
  function getBookmarksForWorkflow(workflowName) {
    const bookmarkFile = _getBookmarkFile(workflowName);
    if (!bookmarkFile) {
      return [];
    }
    try {
      const content = bookmarkFile.getBlob().getDataAsString();
      return content ? JSON.parse(content) : [];
    } catch (error) {
      console.error(`Error parsing bookmarks for workflow ${workflowName}: ${error.toString()}`);
      LogService.log(`Error parsing bookmarks for workflow ${workflowName}: ${error.toString()}`);
      return [];
    }
  }

  /**
   * Adds a bookmark to a workflow.
   * @param {string} workflowName - The name of the workflow.
   * @param {Object} bookmarkData - The bookmark data {name, url, description}.
   * @returns {Array<Object>} The updated list of bookmarks.
   */
  function addBookmarkToWorkflow(workflowName, bookmarkData) {
    const bookmarks = getBookmarksForWorkflow(workflowName);
    const newBookmark = {
      id: _generateUniqueId(),
      name: bookmarkData.name,
      url: bookmarkData.url,
      description: bookmarkData.description || ''
    };
    bookmarks.push(newBookmark);
    const bookmarkFile = _getBookmarkFile(workflowName, true);
    bookmarkFile.setContent(JSON.stringify(bookmarks, null, 2));
    return bookmarks;
  }

  /**
   * Updates a bookmark in a workflow.
   * @param {string} workflowName - The name of the workflow.
   * @param {string} bookmarkId - The ID of the bookmark to update.
   * @param {Object} updatedData - The updated bookmark data {name, url, description}.
   * @returns {Array<Object>} The updated list of bookmarks.
   */
  function updateBookmarkInWorkflow(workflowName, bookmarkId, updatedData) {
    let bookmarks = getBookmarksForWorkflow(workflowName);
    const index = bookmarks.findIndex(b => b.id === bookmarkId);
    if (index !== -1) {
      bookmarks[index] = { ...bookmarks[index], ...updatedData };
      const bookmarkFile = _getBookmarkFile(workflowName, false);
      if (bookmarkFile) {
        bookmarkFile.setContent(JSON.stringify(bookmarks, null, 2));
      }
    }
    return bookmarks;
  }

  /**
   * Deletes a bookmark from a workflow.
   * @param {string} workflowName - The name of the workflow.
   * @param {string} bookmarkId - The ID of the bookmark to delete.
   * @returns {Array<Object>} The updated list of bookmarks.
   */
  function deleteBookmarkFromWorkflow(workflowName, bookmarkId) {
    let bookmarks = getBookmarksForWorkflow(workflowName);
    bookmarks = bookmarks.filter(b => b.id !== bookmarkId);
    const bookmarkFile = _getBookmarkFile(workflowName, false);
    if (bookmarkFile) {
      bookmarkFile.setContent(JSON.stringify(bookmarks, null, 2));
    }
    return bookmarks;
  }

  // --- Global Bookmarks ---

  /**
   * Gets all global bookmarks.
   * @returns {Array<Object>} An array of global bookmark objects.
   */
  function getGlobalBookmarks() {
    try {
      const properties = PropertiesService.getUserProperties();
      const jsonString = properties.getProperty(GLOBAL_BOOKMARKS_JSON);
      return jsonString ? JSON.parse(jsonString) : [];
    } catch (error) {
      console.error(`Error getting global bookmarks: ${error.toString()}`);
      LogService.log(`Error getting global bookmarks: ${error.toString()}`);
      return [];
    }
  }

  /**
   * Saves the global bookmarks to PropertiesService.
   * @param {Array<Object>} bookmarksArray - The array of bookmarks to save.
   */
  function _saveGlobalBookmarks(bookmarksArray) {
    try {
      const properties = PropertiesService.getUserProperties();
      properties.setProperty(GLOBAL_BOOKMARKS_JSON, JSON.stringify(bookmarksArray));
    } catch (error) {
      console.error(`Error saving global bookmarks: ${error.toString()}`);
      LogService.log(`Error saving global bookmarks: ${error.toString()}`);
    }
  }

  /**
   * Adds a global bookmark.
   * @param {Object} bookmarkData - The bookmark data {name, url, description}.
   * @returns {Array<Object>} The updated list of global bookmarks.
   */
  function addGlobalBookmark(bookmarkData) {
    const bookmarks = getGlobalBookmarks();
    const newBookmark = {
      id: _generateUniqueId(),
      name: bookmarkData.name,
      url: bookmarkData.url,
      description: bookmarkData.description || ''
    };
    bookmarks.push(newBookmark);
    _saveGlobalBookmarks(bookmarks);
    return bookmarks;
  }

  /**
   * Updates a global bookmark.
   * @param {string} bookmarkId - The ID of the bookmark to update.
   * @param {Object} updatedData - The updated bookmark data {name, url, description}.
   * @returns {Array<Object>} The updated list of global bookmarks.
   */
  function updateGlobalBookmark(bookmarkId, updatedData) {
    let bookmarks = getGlobalBookmarks();
    const index = bookmarks.findIndex(b => b.id === bookmarkId);
    if (index !== -1) {
      bookmarks[index] = { ...bookmarks[index], ...updatedData };
      _saveGlobalBookmarks(bookmarks);
    }
    return bookmarks;
  }

  /**
   * Deletes a global bookmark.
   * @param {string} bookmarkId - The ID of the bookmark to delete.
   * @returns {Array<Object>} The updated list of global bookmarks.
   */
  function deleteGlobalBookmark(bookmarkId) {
    let bookmarks = getGlobalBookmarks();
    bookmarks = bookmarks.filter(b => b.id !== bookmarkId);
    _saveGlobalBookmarks(bookmarks);
    return bookmarks;
  }

  return {
    _getBookmarkFile, // Exposed for WorkflowService
    getBookmarksForWorkflow,
    addBookmarkToWorkflow,
    updateBookmarkInWorkflow,
    deleteBookmarkFromWorkflow,
    getGlobalBookmarks,
    addGlobalBookmark,
    updateGlobalBookmark,
    deleteGlobalBookmark
  };

})();
