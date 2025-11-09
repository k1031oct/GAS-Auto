package com.gws.auto.mobile.android;

import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
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
public final class SignInActivity_MembersInjector implements MembersInjector<SignInActivity> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private SignInActivity_MembersInjector(Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public void injectMembers(SignInActivity instance) {
    injectAuthorizer(instance, authorizerProvider.get());
  }

  public static MembersInjector<SignInActivity> create(
      Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new SignInActivity_MembersInjector(authorizerProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.SignInActivity.authorizer")
  public static void injectAuthorizer(SignInActivity instance, GoogleApiAuthorizer authorizer) {
    instance.authorizer = authorizer;
  }
}
