package com.gws.auto.mobile.android.ui.settings.user;

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

  private UserInfoFragment_MembersInjector(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public void injectMembers(UserInfoFragment instance) {
    injectAuth(instance, authProvider.get());
  }

  public static MembersInjector<UserInfoFragment> create(Provider<FirebaseAuth> authProvider) {
    return new UserInfoFragment_MembersInjector(authProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.user.UserInfoFragment.auth")
  public static void injectAuth(UserInfoFragment instance, FirebaseAuth auth) {
    instance.auth = auth;
  }
}
