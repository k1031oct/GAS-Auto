package com.gws.auto.mobile.android.ui.settings.app;

import android.content.SharedPreferences;
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
public final class AppSettingsFragment_MembersInjector implements MembersInjector<AppSettingsFragment> {
  private final Provider<SharedPreferences> prefsProvider;

  private AppSettingsFragment_MembersInjector(Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public void injectMembers(AppSettingsFragment instance) {
    injectPrefs(instance, prefsProvider.get());
  }

  public static MembersInjector<AppSettingsFragment> create(
      Provider<SharedPreferences> prefsProvider) {
    return new AppSettingsFragment_MembersInjector(prefsProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment.prefs")
  public static void injectPrefs(AppSettingsFragment instance, SharedPreferences prefs) {
    instance.prefs = prefs;
  }
}
