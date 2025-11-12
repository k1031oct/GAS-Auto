package com.gws.auto.mobile.android.di;

import android.content.Context;
import android.content.SharedPreferences;
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
public final class AppModule_ProvideSharedPreferencesFactory implements Factory<SharedPreferences> {
  private final Provider<Context> appContextProvider;

  private AppModule_ProvideSharedPreferencesFactory(Provider<Context> appContextProvider) {
    this.appContextProvider = appContextProvider;
  }

  @Override
  public SharedPreferences get() {
    return provideSharedPreferences(appContextProvider.get());
  }

  public static AppModule_ProvideSharedPreferencesFactory create(
      Provider<Context> appContextProvider) {
    return new AppModule_ProvideSharedPreferencesFactory(appContextProvider);
  }

  public static SharedPreferences provideSharedPreferences(Context appContext) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSharedPreferences(appContext));
  }
}
