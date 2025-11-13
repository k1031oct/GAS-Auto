package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.local.db.ScheduleDao;
import com.gws.auto.mobile.android.data.repository.ScheduleRepository;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
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
public final class AppModule_ProvideScheduleRepositoryFactory implements Factory<ScheduleRepository> {
  private final Provider<ScheduleDao> scheduleDaoProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private AppModule_ProvideScheduleRepositoryFactory(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleDaoProvider = scheduleDaoProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleRepository get() {
    return provideScheduleRepository(scheduleDaoProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static AppModule_ProvideScheduleRepositoryFactory create(
      Provider<ScheduleDao> scheduleDaoProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new AppModule_ProvideScheduleRepositoryFactory(scheduleDaoProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleRepository provideScheduleRepository(ScheduleDao scheduleDao,
      GoogleApiAuthorizer googleApiAuthorizer) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideScheduleRepository(scheduleDao, googleApiAuthorizer));
  }
}
