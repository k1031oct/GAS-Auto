package com.gws.auto.mobile.android.ui.settings;

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
public final class SettingsFragment_MembersInjector implements MembersInjector<SettingsFragment> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private SettingsFragment_MembersInjector(Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public void injectMembers(SettingsFragment instance) {
    injectAuthorizer(instance, authorizerProvider.get());
  }

  public static MembersInjector<SettingsFragment> create(
      Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new SettingsFragment_MembersInjector(authorizerProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.SettingsFragment.authorizer")
  public static void injectAuthorizer(SettingsFragment instance, GoogleApiAuthorizer authorizer) {
    instance.authorizer = authorizer;
  }
}
