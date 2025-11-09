package com.gws.auto.mobile.android.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepositoryImpl
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideWorkflowRepository(db: FirebaseFirestore, auth: FirebaseAuth): WorkflowRepository {
        return WorkflowRepository(db, auth)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): ScheduleRepository {
        return ScheduleRepositoryImpl(firestore, auth)
    }
}
