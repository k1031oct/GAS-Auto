package com.gws.auto.mobile.android.ui.schedule;

import android.content.SharedPreferences;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
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
public final class ScheduleViewModel_Factory implements Factory<ScheduleViewModel> {
  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<SharedPreferences> prefsProvider;

  private ScheduleViewModel_Factory(Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SharedPreferences> prefsProvider) {
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public ScheduleViewModel get() {
    return newInstance(scheduleRepositoryProvider.get(), prefsProvider.get());
  }

  public static ScheduleViewModel_Factory create(
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<SharedPreferences> prefsProvider) {
    return new ScheduleViewModel_Factory(scheduleRepositoryProvider, prefsProvider);
  }

  public static ScheduleViewModel newInstance(ScheduleRepository scheduleRepository,
      SharedPreferences prefs) {
    return new ScheduleViewModel(scheduleRepository, prefs);
  }
}
