package com.gws.auto.mobile.android.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class WorkflowRepository_Factory implements Factory<WorkflowRepository> {
  private final Provider<WorkflowDao> workflowDaoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private WorkflowRepository_Factory(Provider<WorkflowDao> workflowDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    this.workflowDaoProvider = workflowDaoProvider;
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
  }

  @Override
  public WorkflowRepository get() {
    return newInstance(workflowDaoProvider.get(), firestoreProvider.get(), authProvider.get());
  }

  public static WorkflowRepository_Factory create(Provider<WorkflowDao> workflowDaoProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> authProvider) {
    return new WorkflowRepository_Factory(workflowDaoProvider, firestoreProvider, authProvider);
  }

  public static WorkflowRepository newInstance(WorkflowDao workflowDao, FirebaseFirestore firestore,
      FirebaseAuth auth) {
    return new WorkflowRepository(workflowDao, firestore, auth);
  }
}
