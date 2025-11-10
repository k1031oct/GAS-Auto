package com.gws.auto.mobile.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.local.db.AppDatabase
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "gws-auto.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWorkflowDao(appDatabase: AppDatabase): WorkflowDao {
        return appDatabase.workflowDao()
    }

    @Provides
    @Singleton
    fun provideGoogleApiAuthorizer(@ApplicationContext context: Context): GoogleApiAuthorizer {
        return GoogleApiAuthorizer(context)
    }

    @Provides
    @Singleton
    fun provideWorkflowRepository(
        workflowDao: WorkflowDao,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): WorkflowRepository {
        return WorkflowRepository(workflowDao, firestore, auth)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        googleApiAuthorizer: GoogleApiAuthorizer
    ): ScheduleRepository {
        return ScheduleRepositoryImpl(firestore, auth, googleApiAuthorizer)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }
}
