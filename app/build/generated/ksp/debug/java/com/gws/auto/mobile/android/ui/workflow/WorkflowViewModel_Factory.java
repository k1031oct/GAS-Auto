package com.gws.auto.mobile.android.ui.workflow;

import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import dagger.internal.DaggerGenerated;
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

  private final Provider<SearchHistoryRepository> searchHistoryRepositoryProvider;

  private WorkflowViewModel_Factory(Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider) {
    this.workflowRepositoryProvider = workflowRepositoryProvider;
    this.searchHistoryRepositoryProvider = searchHistoryRepositoryProvider;
  }

  @Override
  public WorkflowViewModel get() {
    return newInstance(workflowRepositoryProvider.get(), searchHistoryRepositoryProvider.get());
  }

  public static WorkflowViewModel_Factory create(
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider) {
    return new WorkflowViewModel_Factory(workflowRepositoryProvider, searchHistoryRepositoryProvider);
  }

  public static WorkflowViewModel newInstance(WorkflowRepository workflowRepository,
      SearchHistoryRepository searchHistoryRepository) {
    return new WorkflowViewModel(workflowRepository, searchHistoryRepository);
  }
}
