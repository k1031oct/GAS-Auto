package com.gws.auto.mobile.android.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH&J\u0016\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/gws/auto/mobile/android/data/repository/ScheduleRepository;", "", "addSchedule", "", "schedule", "Lcom/gws/auto/mobile/android/data/model/Schedule;", "(Lcom/gws/auto/mobile/android/data/model/Schedule;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSchedule", "scheduleId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSchedulesFlow", "Lkotlinx/coroutines/flow/Flow;", "", "updateSchedule", "app_debug"})
public abstract interface ScheduleRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.gws.auto.mobile.android.data.model.Schedule>> getSchedulesFlow();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addSchedule(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.data.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateSchedule(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.data.model.Schedule schedule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSchedule(@org.jetbrains.annotations.NotNull()
    java.lang.String scheduleId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}