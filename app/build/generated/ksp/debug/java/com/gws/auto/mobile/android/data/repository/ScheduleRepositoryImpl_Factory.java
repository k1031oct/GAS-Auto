package com.gws.auto.mobile.android.data.repository;

import com.gws.auto.mobile.android.data.local.db.ScheduleDao;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
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
public final class ScheduleRepositoryImpl_Factory implements Factory<ScheduleRepositoryImpl> {
  private final Provider<ScheduleDao> scheduleDaoProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ScheduleRepositoryImpl_Factory(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleDaoProvider = scheduleDaoProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleRepositoryImpl get() {
    return newInstance(scheduleDaoProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static ScheduleRepositoryImpl_Factory create(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ScheduleRepositoryImpl_Factory(scheduleDaoProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleRepositoryImpl newInstance(ScheduleDao scheduleDao,
      GoogleApiAuthorizer googleApiAuthorizer) {
    return new ScheduleRepositoryImpl(scheduleDao, googleApiAuthorizer);
  }
}
