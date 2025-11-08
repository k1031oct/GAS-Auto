package com.gws.auto.mobile.android.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bJ\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\bJ\u001c\u0010\u000e\u001a\u0010\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u000f0\u000f0\b2\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0014\u001a\u00020\u0015R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/gws/auto/mobile/android/data/repository/ScheduleRepository;", "", "()V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "db", "Lcom/google/firebase/firestore/FirebaseFirestore;", "deleteSchedule", "Lcom/google/android/gms/tasks/Task;", "Ljava/lang/Void;", "scheduleId", "", "getAllSchedules", "Lcom/google/firebase/firestore/QuerySnapshot;", "getSchedule", "Lcom/google/firebase/firestore/DocumentSnapshot;", "kotlin.jvm.PlatformType", "getSchedulesCollection", "Lcom/google/firebase/firestore/CollectionReference;", "saveSchedule", "schedule", "Lcom/gws/auto/mobile/android/domain/model/Schedule;", "app_debug"})
public final class ScheduleRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    
    public ScheduleRepository() {
        super();
    }
    
    private final com.google.firebase.firestore.CollectionReference getSchedulesCollection() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<java.lang.Void> saveSchedule(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.domain.model.Schedule schedule) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot> getSchedule(@org.jetbrains.annotations.NotNull()
    java.lang.String scheduleId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot> getAllSchedules() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.tasks.Task<java.lang.Void> deleteSchedule(@org.jetbrains.annotations.NotNull()
    java.lang.String scheduleId) {
        return null;
    }
}