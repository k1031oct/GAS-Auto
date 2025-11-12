package com.gws.auto.mobile.android.ui.settings;

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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private SettingsViewModel_Factory(Provider<WorkflowRepository> workflowRepositoryProvider) {
    this.workflowRepositoryProvider = workflowRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(workflowRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    return new SettingsViewModel_Factory(workflowRepositoryProvider);
  }

  public static SettingsViewModel newInstance(WorkflowRepository workflowRepository) {
    return new SettingsViewModel(workflowRepository);
  }
}
