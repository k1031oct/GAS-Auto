package com.gws.auto.mobile.android.di;

import android.content.Context;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideGoogleApiAuthorizerFactory implements Factory<GoogleApiAuthorizer> {
  private final Provider<Context> contextProvider;

  private AppModule_ProvideGoogleApiAuthorizerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GoogleApiAuthorizer get() {
    return provideGoogleApiAuthorizer(contextProvider.get());
  }

  public static AppModule_ProvideGoogleApiAuthorizerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideGoogleApiAuthorizerFactory(contextProvider);
  }

  public static GoogleApiAuthorizer provideGoogleApiAuthorizer(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideGoogleApiAuthorizer(context));
  }
}
