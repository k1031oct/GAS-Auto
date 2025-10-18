var DriveService = {
  moveFile: function(settings) {
    const sourceFileId = _extractIdFromUrl(settings.sourceFileUrl);
    const destFolderId = _extractIdFromUrl(settings.destinationFolderUrl);
    if (!sourceFileId || !destFolderId) {
      throw new Error('Invalid source or destination URL.');
    }
    const file = DriveApp.getFileById(sourceFileId);
    const folder = DriveApp.getFolderById(destFolderId);
    file.moveTo(folder);
    return `File ${file.getName()} moved to ${folder.getName()}.`;
  },
  copyFile: function(settings) {
    const sourceFileId = _extractIdFromUrl(settings.sourceFileUrl);
    const destFolderId = _extractIdFromUrl(settings.destinationFolderUrl);
    if (!sourceFileId || !destFolderId) {
      throw new Error('Invalid source or destination URL.');
    }
    const file = DriveApp.getFileById(sourceFileId);
    const folder = DriveApp.getFolderById(destFolderId);
    const newFileName = settings.newFileName || file.getName();
    const newFile = file.makeCopy(newFileName, folder);
    return `File copied to ${newFile.getName()} in ${folder.getName()}.`;
  }
};