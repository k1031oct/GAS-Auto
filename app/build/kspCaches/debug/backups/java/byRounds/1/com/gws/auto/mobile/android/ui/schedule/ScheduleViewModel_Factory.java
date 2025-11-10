package com.gws.auto.mobile.android.ui.schedule;

import com.gws.auto.mobile.android.data.repository.CalendarRepository;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
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
public final class ScheduleViewModel_Factory implements Factory<ScheduleViewModel> {
  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<CalendarRepository> calendarRepositoryProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ScheduleViewModel_Factory(Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<CalendarRepository> calendarRepositoryProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.calendarRepositoryProvider = calendarRepositoryProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleViewModel get() {
    return newInstance(DoubleCheck.lazy(scheduleRepositoryProvider), DoubleCheck.lazy(calendarRepositoryProvider), googleApiAuthorizerProvider.get());
  }

  public static ScheduleViewModel_Factory create(
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<CalendarRepository> calendarRepositoryProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ScheduleViewModel_Factory(scheduleRepositoryProvider, calendarRepositoryProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleViewModel newInstance(Lazy<ScheduleRepository> scheduleRepository,
      Lazy<CalendarRepository> calendarRepository, GoogleApiAuthorizer googleApiAuthorizer) {
    return new ScheduleViewModel(scheduleRepository, calendarRepository, googleApiAuthorizer);
  }
}
