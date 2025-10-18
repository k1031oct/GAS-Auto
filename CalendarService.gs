var CalendarService = {
  createEvent: function(settings) {
    const calendar = CalendarApp.getCalendarById(settings.calendarId);
    if (!calendar) throw new Error(`Calendar with ID "${settings.calendarId}" not found.`);
    const startTime = new Date(settings.startTime);
    const endTime = new Date(settings.endTime);
    if (isNaN(startTime.getTime()) || isNaN(endTime.getTime())) {
      throw new Error('Invalid start or end time format.');
    }
    calendar.createEvent(settings.title, startTime, endTime);
    return `Event "${settings.title}" created.`;
  }
};