package com.gws.auto.mobile.android.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MainSharedViewModel_Factory implements Factory<MainSharedViewModel> {
  @Override
  public MainSharedViewModel get() {
    return newInstance();
  }

  public static MainSharedViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MainSharedViewModel newInstance() {
    return new MainSharedViewModel();
  }

  private static final class InstanceHolder {
    static final MainSharedViewModel_Factory INSTANCE = new MainSharedViewModel_Factory();
  }
}
