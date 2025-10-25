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
  },

  exportWorkflowSpecification: function(workflowData) {
    const workflowName = workflowData.name;
    if (!workflowName) {
      throw new Error('Workflow data must include a name.');
    }

    // 1. Get the workflow folder
    const workflowFolder = WorkflowService.getWorkflowFolder(workflowName);
    const docName = workflowName + '仕様書';
    let doc;
    let file;

    // 2. Find or create the document
    const existingFiles = workflowFolder.getFilesByName(docName);
    if (existingFiles.hasNext()) {
      file = existingFiles.next();
      doc = DocumentApp.openById(file.getId());
      doc.getBody().clear();
    } else {
      doc = DocumentApp.create(docName);
      file = DriveApp.getFileById(doc.getId());
      workflowFolder.addFile(file);
      DriveApp.getRootFolder().removeFile(file);
    }

    const body = doc.getBody();

    // 3. Generate and write content
    body.appendParagraph(workflowName + ' 仕様書').setHeading(DocumentApp.ParagraphHeading.TITLE);
    body.appendParagraph('生成日時: ' + new Date().toLocaleString('ja-JP'));
    body.appendHorizontalRule();

    if (!workflowData.modules || workflowData.modules.length === 0) {
      body.appendParagraph('このワークフローにはモジュールがありません。');
    } else {
      body.appendParagraph('モジュール一覧').setHeading(DocumentApp.ParagraphHeading.HEADING1);
      
      const generateModuleSpec = (module, level) => {
        const heading = level === 1 ? DocumentApp.ParagraphHeading.HEADING2 : DocumentApp.ParagraphHeading.HEADING3;
        body.appendParagraph(module.name).setHeading(heading);

        const moduleDef = ModuleService.getModuleById(module.id); // Fetch definition for description
        if (moduleDef && moduleDef.description) {
          body.appendParagraph(moduleDef.description).setItalic(true);
        }

        const settingsList = [];
        for (const key in module.settings) {
          if (Object.prototype.hasOwnProperty.call(module.settings, key)) {
            settingsList.push(`「${key}」: ${module.settings[key]}`);
          }
        }

        if (settingsList.length > 0) {
          const listItem = body.appendListItem('設定:');
          settingsList.forEach(settingText => {
             body.appendListItem(settingText).setListId(listItem.getListId()).setNestedLevel(1);
          });
        } else {
           body.appendListItem('設定: なし');
        }
        
        // Handle containers
        if (module.type === 'container' && module.modules && module.modules.length > 0) {
           body.appendParagraph('内包するモジュール:').setBold(true);
           module.modules.forEach(nestedModule => generateModuleSpec(nestedModule, level + 1));
        }
      };

      workflowData.modules.forEach(module => generateModuleSpec(module, 1));
    }
    
    doc.saveAndClose();

    return {
      message: `仕様書「${docName}」を生成しました。`,
      url: doc.getUrl()
    };
  }
};