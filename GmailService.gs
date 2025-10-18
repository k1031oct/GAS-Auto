var GmailService = {
  sendEmail: function(settings) {
    GmailApp.sendEmail(settings.recipient, settings.subject, settings.body);
    return `Email sent to ${settings.recipient}.`;
  }
};