var DocsService = {
  createFile: function(settings) {
    const folderId = _extractIdFromUrl(settings.folderUrl);
    if (!folderId) throw new Error('Invalid folder URL.');
    const folder = DriveApp.getFolderById(folderId);
    const doc = DocumentApp.create(settings.fileName);
    const docFile = DriveApp.getFileById(doc.getId());
    folder.addFile(docFile);
    DriveApp.getRootFolder().removeFile(docFile); // Remove from root
    if (settings.content) {
      doc.getBody().setText(settings.content);
    }
    return `Document "${settings.fileName}" created in ${folder.getName()}.`;
  }
};