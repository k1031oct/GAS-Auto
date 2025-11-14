package com.gws.auto.mobile.android.ui.settings.user;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class UserInfoFragment_MembersInjector implements MembersInjector<UserInfoFragment> {
  private final Provider<FirebaseAuth> authProvider;

  private final Provider<GoogleSignInClient> googleSignInClientProvider;

  private UserInfoFragment_MembersInjector(Provider<FirebaseAuth> authProvider,
      Provider<GoogleSignInClient> googleSignInClientProvider) {
    this.authProvider = authProvider;
    this.googleSignInClientProvider = googleSignInClientProvider;
  }

  @Override
  public void injectMembers(UserInfoFragment instance) {
    injectAuth(instance, authProvider.get());
    injectGoogleSignInClient(instance, googleSignInClientProvider.get());
  }

  public static MembersInjector<UserInfoFragment> create(Provider<FirebaseAuth> authProvider,
      Provider<GoogleSignInClient> googleSignInClientProvider) {
    return new UserInfoFragment_MembersInjector(authProvider, googleSignInClientProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.user.UserInfoFragment.auth")
  public static void injectAuth(UserInfoFragment instance, FirebaseAuth auth) {
    instance.auth = auth;
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.user.UserInfoFragment.googleSignInClient")
  public static void injectGoogleSignInClient(UserInfoFragment instance,
      GoogleSignInClient googleSignInClient) {
    instance.googleSignInClient = googleSignInClient;
  }
}
