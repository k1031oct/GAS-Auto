package com.gws.auto.mobile.android.ui.history;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<HistoryRepository> historyRepositoryProvider;

  private HistoryViewModel_Factory(Provider<HistoryRepository> historyRepositoryProvider) {
    this.historyRepositoryProvider = historyRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(historyRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<HistoryRepository> historyRepositoryProvider) {
    return new HistoryViewModel_Factory(historyRepositoryProvider);
  }

  public static HistoryViewModel newInstance(HistoryRepository historyRepository) {
    return new HistoryViewModel(historyRepository);
  }
}
