package com.gws.auto.mobile.android.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bJ\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\bJ\u001c\u0010\u000e\u001a\u0010\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u000f0\u000f0\b2\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0014\u001a\u00020\u0015R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;", "", "db", "Lcom/google/firebase/firestore/FirebaseFirestore;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "(Lcom/google/firebase/firestore/FirebaseFirestore;Lcom/google/firebase/auth/FirebaseAuth;)V", "deleteWorkflow", "Lcom/google/android/gms/tasks/Task;", "Ljava/lang/Void;", "workflowId", "", "getAllWorkflows", "Lcom/google/firebase/firestore/QuerySnapshot;", "getWorkflow", "Lcom/google/firebase/firestore/DocumentSnapshot;", "kotlin.jvm.PlatformType", "getWorkflowsCollection", "Lcom/google/firebase/firestore/CollectionReference;", "saveWorkflow", "workflow", "Lcom/gws/auto/mobile/android/domain/model/Workflow;", "app_debug"})
public final class WorkflowRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    
    @javax.inject.Inject()
    public WorkflowRepository(@org.jetbrains.annotations.NotNull()
    com.google.firebase.firestore.FirebaseFirestore db, @org.jetbrains.annotations.NotNull()
    com.google.firebase.auth.FirebaseAuth auth) {
        super();
    }
    
    private final com.google.firebase.firestore.CollectionReference getWorkflowsCollection() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<java.lang.Void> saveWorkflow(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.domain.model.Workflow workflow) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot> getWorkflow(@org.jetbrains.annotations.NotNull()
    java.lang.String workflowId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot> getAllWorkflows() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<java.lang.Void> deleteWorkflow(@org.jetbrains.annotations.NotNull()
    java.lang.String workflowId) {
        return null;
    }
}