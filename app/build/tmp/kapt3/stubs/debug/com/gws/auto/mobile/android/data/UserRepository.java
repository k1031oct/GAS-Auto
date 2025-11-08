package com.gws.auto.mobile.android.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b2\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u0001R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/gws/auto/mobile/android/data/UserRepository;", "", "()V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "db", "Lcom/google/firebase/firestore/FirebaseFirestore;", "getUserSetting", "Lcom/google/android/gms/tasks/Task;", "Lcom/google/firebase/firestore/DocumentSnapshot;", "key", "", "saveUserSetting", "", "value", "app_debug"})
public final class UserRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    
    public UserRepository() {
        super();
    }
    
    public final void saveUserSetting(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.lang.Object value) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot> getUserSetting(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
}