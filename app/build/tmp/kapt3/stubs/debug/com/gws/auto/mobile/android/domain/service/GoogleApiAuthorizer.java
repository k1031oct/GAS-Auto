package com.gws.auto.mobile.android.domain.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lcom/gws/auto/mobile/android/domain/service/GoogleApiAuthorizer;", "", "activity", "Landroid/app/Activity;", "(Landroid/app/Activity;)V", "credentialManager", "Landroidx/credentials/CredentialManager;", "getCredentialManager", "()Landroidx/credentials/CredentialManager;", "credentialManager$delegate", "Lkotlin/Lazy;", "getGoogleIdTokenCredential", "Lcom/google/android/libraries/identity/googleid/GoogleIdTokenCredential;", "response", "Landroidx/credentials/GetCredentialResponse;", "signIn", "serverClientId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signOut", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class GoogleApiAuthorizer {
    @org.jetbrains.annotations.NotNull()
    private final android.app.Activity activity = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy credentialManager$delegate = null;
    
    public GoogleApiAuthorizer(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
        super();
    }
    
    private final androidx.credentials.CredentialManager getCredentialManager() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signIn(@org.jetbrains.annotations.NotNull()
    java.lang.String serverClientId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super androidx.credentials.GetCredentialResponse> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signOut(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.libraries.identity.googleid.GoogleIdTokenCredential getGoogleIdTokenCredential(@org.jetbrains.annotations.NotNull()
    androidx.credentials.GetCredentialResponse response) {
        return null;
    }
}