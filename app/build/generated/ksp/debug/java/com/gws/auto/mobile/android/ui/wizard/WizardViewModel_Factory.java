package com.gws.auto.mobile.android.ui.wizard;

import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class WizardViewModel_Factory implements Factory<WizardViewModel> {
  private final Provider<SharedPreferences> prefsProvider;

  private WizardViewModel_Factory(Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public WizardViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static WizardViewModel_Factory create(Provider<SharedPreferences> prefsProvider) {
    return new WizardViewModel_Factory(prefsProvider);
  }

  public static WizardViewModel newInstance(SharedPreferences prefs) {
    return new WizardViewModel(prefs);
  }
}
