package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
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
public final class AppModule_ProvideWorkflowRepositoryFactory implements Factory<WorkflowRepository> {
  private final Provider<WorkflowDao> workflowDaoProvider;

  private AppModule_ProvideWorkflowRepositoryFactory(Provider<WorkflowDao> workflowDaoProvider) {
    this.workflowDaoProvider = workflowDaoProvider;
  }

  @Override
  public WorkflowRepository get() {
    return provideWorkflowRepository(workflowDaoProvider.get());
  }

  public static AppModule_ProvideWorkflowRepositoryFactory create(
      Provider<WorkflowDao> workflowDaoProvider) {
    return new AppModule_ProvideWorkflowRepositoryFactory(workflowDaoProvider);
  }

  public static WorkflowRepository provideWorkflowRepository(WorkflowDao workflowDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWorkflowRepository(workflowDao));
  }
}
