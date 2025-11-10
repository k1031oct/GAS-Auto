package com.gws.auto.mobile.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.domain.model.Workflow

@Database(entities = [Workflow::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class, ModuleListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workflowDao(): WorkflowDao
}