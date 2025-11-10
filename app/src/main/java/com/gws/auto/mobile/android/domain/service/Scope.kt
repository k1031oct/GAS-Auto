package com.gws.auto.mobile.android.domain.service

import com.google.api.services.calendar.CalendarScopes

sealed class Scope(val scopeUri: String) {
    object CalendarReadOnly : Scope(CalendarScopes.CALENDAR_READONLY)
}
