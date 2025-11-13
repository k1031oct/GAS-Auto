package com.gws.auto.mobile.android.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ScheduleRepositoryImpl_Factory(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.scheduleDaoProvider = scheduleDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleRepositoryImpl get() {
    return newInstance(scheduleDaoProvider.get(), firestoreProvider.get(), authProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static ScheduleRepositoryImpl_Factory create(Provider<ScheduleDao> scheduleDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ScheduleRepositoryImpl_Factory(scheduleDaoProvider, firestoreProvider, authProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleRepositoryImpl newInstance(ScheduleDao scheduleDao,
      FirebaseFirestore firestore, FirebaseAuth auth, GoogleApiAuthorizer googleApiAuthorizer) {
    return new ScheduleRepositoryImpl(scheduleDao, firestore, auth, googleApiAuthorizer);
  }
}
