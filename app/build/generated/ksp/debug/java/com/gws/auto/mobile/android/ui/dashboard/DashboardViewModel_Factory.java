package com.gws.auto.mobile.android.ui.dashboard;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<HistoryRepository> historyRepositoryProvider;

  private DashboardViewModel_Factory(Provider<HistoryRepository> historyRepositoryProvider) {
    this.historyRepositoryProvider = historyRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(historyRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<HistoryRepository> historyRepositoryProvider) {
    return new DashboardViewModel_Factory(historyRepositoryProvider);
  }

  public static DashboardViewModel newInstance(HistoryRepository historyRepository) {
    return new DashboardViewModel(historyRepository);
  }
}
