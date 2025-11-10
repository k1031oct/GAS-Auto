package com.gws.auto.mobile.android.ui.workflow;

import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import dagger.Lazy;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class WorkflowViewModel_Factory implements Factory<WorkflowViewModel> {
  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private WorkflowViewModel_Factory(Provider<WorkflowRepository> workflowRepositoryProvider) {
    this.workflowRepositoryProvider = workflowRepositoryProvider;
  }

  @Override
  public WorkflowViewModel get() {
    return newInstance(DoubleCheck.lazy(workflowRepositoryProvider));
  }

  public static WorkflowViewModel_Factory create(
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    return new WorkflowViewModel_Factory(workflowRepositoryProvider);
  }

  public static WorkflowViewModel newInstance(Lazy<WorkflowRepository> workflowRepository) {
    return new WorkflowViewModel(workflowRepository);
  }
}
