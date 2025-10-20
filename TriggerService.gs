/**
 * @file TriggerService.gs
 * @description Manages the creation, deletion, and retrieval of script triggers for workflows.
 */
var TriggerService = {

  /**
   * Retrieves all triggers associated with a specific workflow.
   * @param {string} workflowName The name of the workflow.
   * @returns {Array<Object>} A list of trigger information objects.
   */
  getTriggersForWorkflow: function(workflowName) {
    const allTriggers = ScriptApp.getProjectTriggers();
    const userProperties = PropertiesService.getUserProperties();
    const workflowTriggers = [];

    allTriggers.forEach(trigger => {
      const triggerUid = trigger.getUniqueId();
      const configStr = userProperties.getProperty('config_' + triggerUid);
      if (configStr) {
        try {
          const config = JSON.parse(configStr);
          if (config.workflowName === workflowName) {
            workflowTriggers.push({
              uid: triggerUid,
              handlerFunction: trigger.getHandlerFunction(),
              triggerSource: trigger.getTriggerSource().toString(),
              eventType: trigger.getEventType().toString(),
            });
          }
        } catch (e) {
          // Ignore configs that can't be parsed
        }
      }
    });

    return workflowTriggers;
  },

  /**
   * Creates a new time-based trigger for a workflow.
   * @param {string} workflowName The name of the workflow to trigger.
   * @param {object} options The trigger options (e.g., {type: 'daily', hour: 9, minute: 0}).
   * @returns {string} A confirmation message.
   */
  createTimeBasedTrigger: function(workflowName, options) {
    if (!workflowName) {
      throw new Error('Workflow name is required to create a trigger.');
    }

    let triggerBuilder = ScriptApp.newTrigger('executeWorkflowByTrigger').timeBased();

    switch (options.type) {
      case 'every_minutes':
        triggerBuilder.everyMinutes(options.interval || 5);
        break;
      case 'every_hours':
        triggerBuilder.everyHours(options.interval || 1);
        break;
      case 'daily':
        triggerBuilder.atHour(options.hour || 9).everyDays(1);
        break;
      case 'weekly':
        triggerBuilder.onWeekDay(options.weekday || ScriptApp.WeekDay.MONDAY).atHour(options.hour || 9);
        break;
      default:
        throw new Error(`Unsupported trigger type: ${options.type}`);
    }

    const trigger = triggerBuilder.build();
    const triggerUid = trigger.getUniqueId();

    const config = {
      workflowName: workflowName
    };
    PropertiesService.getUserProperties().setProperty('config_' + triggerUid, JSON.stringify(config));

    return `トリガー (ID: ${triggerUid}) を作成しました。`;
  },

  /**
   * Deletes a trigger by its unique ID.
   * @param {string} triggerUid The unique ID of the trigger to delete.
   * @returns {string} A confirmation message.
   */
  deleteTrigger: function(triggerUid) {
    const triggers = ScriptApp.getProjectTriggers();
    let found = false;
    for (let i = 0; i < triggers.length; i++) {
      if (triggers[i].getUniqueId() === triggerUid) {
        ScriptApp.deleteTrigger(triggers[i]);
        found = true;
        break;
      }
    }

    if (found) {
      // Clean up the associated property
      PropertiesService.getUserProperties().deleteProperty('config_' + triggerUid);
      return `トリガー (ID: ${triggerUid}) を削除しました。`;
    } else {
      throw new Error(`トリガー (ID: ${triggerUid}) が見つかりませんでした。`);
    }
  },

  /**
   * Creates a trigger for a standalone module configuration.
   * @param {object} moduleConfig The module configuration object.
   */
  createTriggerForModule: function(moduleConfig) {
    if (!moduleConfig || !moduleConfig.trigger || !moduleConfig.handler) {
      throw new Error('Invalid module configuration for trigger creation.');
    }
    
    // If a trigger already exists for this module, delete the old one first.
    if (moduleConfig.triggerUid) {
      this.deleteTrigger(moduleConfig.triggerUid);
    }

    const triggerOptions = moduleConfig.trigger;
    let triggerBuilder = ScriptApp.newTrigger('executeModuleByTrigger').timeBased();

    switch (triggerOptions.frequency) {
      case 'daily':
        triggerBuilder.atHour(triggerOptions.hour || 2).everyDays(1);
        break;
      // Add other frequencies like 'weekly', 'hourly' if needed
      default:
        // Default to daily at 2am
        triggerBuilder.atHour(2).everyDays(1);
        break;
    }

    const trigger = triggerBuilder.build();
    moduleConfig.triggerUid = trigger.getUniqueId(); // Store the new trigger ID

    PropertiesService.getUserProperties().setProperty('module_config_' + trigger.getUniqueId(), JSON.stringify(moduleConfig));
    Logger.log(`Created trigger ${moduleConfig.triggerUid} for module ${moduleConfig.name}.`);
  },

  /**
   * Deletes the trigger associated with a module configuration.
   * @param {object} moduleConfig The module configuration object.
   */
  deleteTriggerForModule: function(moduleConfig) {
    const triggerUid = moduleConfig.triggerUid;
    if (triggerUid) {
      try {
        this.deleteTrigger(triggerUid);
        // The main deleteTrigger also removes the property, but we might have a different property key.
        PropertiesService.getUserProperties().deleteProperty('module_config_' + triggerUid);
        Logger.log(`Deleted trigger ${triggerUid} for module ${moduleConfig.name}.`);
      } catch (e) {
        // Ignore if trigger is already deleted
        Logger.log(`Could not delete trigger ${triggerUid} (may already be gone): ${e.message}`);
      }
    }
  }
};