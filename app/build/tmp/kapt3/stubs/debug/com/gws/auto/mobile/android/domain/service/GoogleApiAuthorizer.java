package com.gws.auto.mobile.android.domain.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u000e\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/gws/auto/mobile/android/domain/service/GoogleApiAuthorizer;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "credentialManager", "Landroidx/credentials/CredentialManager;", "getGoogleIdTokenCredential", "Lcom/google/android/libraries/identity/googleid/GoogleIdTokenCredential;", "request", "Landroidx/credentials/GetCredentialRequest;", "(Landroidx/credentials/GetCredentialRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signIn", "serverClientId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signOut", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class GoogleApiAuthorizer {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.credentials.CredentialManager credentialManager = null;
    
    @javax.inject.Inject()
    public GoogleApiAuthorizer(@dagger.hilt.android.qualifiers.ActivityContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signIn(@org.jetbrains.annotations.NotNull()
    java.lang.String serverClientId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super androidx.credentials.GetCredentialRequest> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getGoogleIdTokenCredential(@org.jetbrains.annotations.NotNull()
    androidx.credentials.GetCredentialRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.google.android.libraries.identity.googleid.GoogleIdTokenCredential> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signOut(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}