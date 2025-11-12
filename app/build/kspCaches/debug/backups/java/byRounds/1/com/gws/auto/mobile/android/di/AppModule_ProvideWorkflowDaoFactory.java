package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.local.db.AppDatabase;
import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideWorkflowDaoFactory implements Factory<WorkflowDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  private AppModule_ProvideWorkflowDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public WorkflowDao get() {
    return provideWorkflowDao(appDatabaseProvider.get());
  }

  public static AppModule_ProvideWorkflowDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new AppModule_ProvideWorkflowDaoFactory(appDatabaseProvider);
  }

  public static WorkflowDao provideWorkflowDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWorkflowDao(appDatabase));
  }
}
