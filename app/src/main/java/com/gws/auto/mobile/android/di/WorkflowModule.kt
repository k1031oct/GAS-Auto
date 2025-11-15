package com.gws.auto.mobile.android.di

import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.engine.modules.CreateGmailDraftModule
import com.gws.auto.mobile.android.domain.engine.modules.DefineVariableModule
import com.gws.auto.mobile.android.domain.engine.modules.GetRelativeDateModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
object WorkflowModule {

    @Provides
    @IntoMap
    @StringKey("DEFINE_VARIABLE")
    fun provideDefineVariableModule(module: DefineVariableModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("GET_RELATIVE_DATE")
    fun provideGetRelativeDateModule(module: GetRelativeDateModule): ModuleExecutor = module

    @Provides
    @IntoMap
    @StringKey("CREATE_GMAIL_DRAFT")
    fun provideCreateGmailDraftModule(module: CreateGmailDraftModule): ModuleExecutor = module

    // Add other module providers here
}
