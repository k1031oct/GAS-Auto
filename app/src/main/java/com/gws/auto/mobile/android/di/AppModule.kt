package com.gws.auto.mobile.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.local.db.AppDatabase
import com.gws.auto.mobile.android.data.local.db.HistoryDao
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao
import com.gws.auto.mobile.android.data.local.db.TagDao
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl
import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository
import com.gws.auto.mobile.android.data.repository.TagRepository
import com.gws.auto.mobile.android.data.repository.UserPreferencesRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth? {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        historyDaoProvider: Provider<HistoryDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "gws-auto.db"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert dummy data on creation
                CoroutineScope(Dispatchers.IO).launch {
                    val historyDao = historyDaoProvider.get()
                    historyDao.insertHistory(
                        History(
                            workflowId = "wf-1",
                            workflowName = "Daily Report",
                            executedAt = Date(),
                            status = "Success",
                            logs = "Execution started...\nStep 1 completed.\nStep 2 completed.\nExecution finished.",
                            durationMs = 1200
                        )
                    )
                    historyDao.insertHistory(
                        History(
                            workflowId = "wf-2",
                            workflowName = "File Cleanup",
                            executedAt = Date(System.currentTimeMillis() - 86400000), // 1 day ago
                            status = "Failure",
                            logs = "Execution started...\nFailed at Step 2: File not found.",
                            durationMs = 500,
                            isBookmarked = true
                        )
                    )
                }
            }
        }).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWorkflowDao(appDatabase: AppDatabase): WorkflowDao {
        return appDatabase.workflowDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(appDatabase: AppDatabase): TagDao {
        return appDatabase.tagDao()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(appDatabase: AppDatabase): ScheduleDao {
        return appDatabase.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideGoogleApiAuthorizer(@ApplicationContext context: Context): GoogleApiAuthorizer {
        return GoogleApiAuthorizer(context)
    }

    @Provides
    @Singleton
    fun provideWorkflowRepository(
        workflowDao: WorkflowDao
    ): WorkflowRepository {
        return WorkflowRepository(workflowDao)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        scheduleDao: ScheduleDao,
        googleApiAuthorizer: GoogleApiAuthorizer
    ): ScheduleRepository {
        return ScheduleRepositoryImpl(scheduleDao, googleApiAuthorizer)
    }

    @Provides
    @Singleton
    fun provideTagRepository(tagDao: TagDao): TagRepository {
        return TagRepository(tagDao)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(historyDao: HistoryDao): HistoryRepository {
        return HistoryRepository(historyDao)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(searchHistoryDao: SearchHistoryDao): SearchHistoryRepository {
        return SearchHistoryRepository(searchHistoryDao)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(prefs: SharedPreferences): UserPreferencesRepository {
        return UserPreferencesRepository(prefs)
    }
}
