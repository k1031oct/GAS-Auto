package com.gws.auto.mobile.android.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private AppModule_ProvideScheduleRepositoryFactory(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleDaoProvider = scheduleDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleRepository get() {
    return provideScheduleRepository(scheduleDaoProvider.get(), firestoreProvider.get(), authProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static AppModule_ProvideScheduleRepositoryFactory create(
      Provider<ScheduleDao> scheduleDaoProvider, Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new AppModule_ProvideScheduleRepositoryFactory(scheduleDaoProvider, firestoreProvider, authProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleRepository provideScheduleRepository(ScheduleDao scheduleDao,
      FirebaseFirestore firestore, FirebaseAuth auth, GoogleApiAuthorizer googleApiAuthorizer) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideScheduleRepository(scheduleDao, firestore, auth, googleApiAuthorizer));
  }
}
