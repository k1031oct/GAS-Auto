package com.gws.auto.mobile.android.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ScheduleRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public ScheduleRepositoryImpl get() {
    return newInstance(firestoreProvider.get(), authProvider.get(), googleApiAuthorizerProvider.get());
  }

  public static ScheduleRepositoryImpl_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ScheduleRepositoryImpl_Factory(firestoreProvider, authProvider, googleApiAuthorizerProvider);
  }

  public static ScheduleRepositoryImpl newInstance(FirebaseFirestore firestore, FirebaseAuth auth,
      GoogleApiAuthorizer googleApiAuthorizer) {
    return new ScheduleRepositoryImpl(firestore, auth, googleApiAuthorizer);
  }
}
