package com.gws.auto.mobile.android.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
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
public final class AppModule_ProvideWorkflowRepositoryFactory implements Factory<WorkflowRepository> {
  private final Provider<FirebaseFirestore> dbProvider;

  private final Provider<FirebaseAuth> authProvider;

  private AppModule_ProvideWorkflowRepositoryFactory(Provider<FirebaseFirestore> dbProvider,
      Provider<FirebaseAuth> authProvider) {
    this.dbProvider = dbProvider;
    this.authProvider = authProvider;
  }

  @Override
  public WorkflowRepository get() {
    return provideWorkflowRepository(dbProvider.get(), authProvider.get());
  }

  public static AppModule_ProvideWorkflowRepositoryFactory create(
      Provider<FirebaseFirestore> dbProvider, Provider<FirebaseAuth> authProvider) {
    return new AppModule_ProvideWorkflowRepositoryFactory(dbProvider, authProvider);
  }

  public static WorkflowRepository provideWorkflowRepository(FirebaseFirestore db,
      FirebaseAuth auth) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWorkflowRepository(db, auth));
  }
}
