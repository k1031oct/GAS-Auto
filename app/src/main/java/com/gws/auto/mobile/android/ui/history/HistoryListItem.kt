package com.gws.auto.mobile.android.ui.history

import com.gws.auto.mobile.android.domain.model.History

sealed class HistoryListItem {
    data class HeaderItem(val history: History, var isExpanded: Boolean = false) : HistoryListItem()
    data class LogItem(val log: String) : HistoryListItem()
}
