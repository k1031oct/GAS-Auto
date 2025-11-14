package com.gws.auto.mobile.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.model.SearchHistory
import com.gws.auto.mobile.android.domain.model.Tag
import com.gws.auto.mobile.android.domain.model.Workflow

@Database(entities = [Workflow::class, Tag::class, History::class, Schedule::class, SearchHistory::class], version = 7, exportSchema = false)
@TypeConverters(ListConverter::class, ModuleListConverter::class, DateConverter::class, IntListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workflowDao(): WorkflowDao
    abstract fun tagDao(): TagDao
    abstract fun historyDao(): HistoryDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
