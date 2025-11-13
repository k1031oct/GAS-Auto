package com.gws.auto.mobile.android.ui.history

import com.gws.auto.mobile.android.domain.model.History

sealed class HistoryListItem {
    abstract val id: String

    data class HeaderItem(val history: History, val isExpanded: Boolean) : HistoryListItem() {
        override val id: String = history.id.toString()
    }
    data class LogItem(val log: String, val parentId: String) : HistoryListItem() {
        // Create a stable, unique ID for the log item
        override val id: String = parentId + "_" + log
    }
}
