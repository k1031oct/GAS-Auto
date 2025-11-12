package com.gws.auto.mobile.android.ui.schedule;

import android.content.Context;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ScheduleSettingsViewModel_Factory implements Factory<ScheduleSettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private ScheduleSettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
  }

  @Override
  public ScheduleSettingsViewModel get() {
    return newInstance(contextProvider.get(), scheduleRepositoryProvider.get());
  }

  public static ScheduleSettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider) {
    return new ScheduleSettingsViewModel_Factory(contextProvider, scheduleRepositoryProvider);
  }

  public static ScheduleSettingsViewModel newInstance(Context context,
      ScheduleRepository scheduleRepository) {
    return new ScheduleSettingsViewModel(context, scheduleRepository);
  }
}
