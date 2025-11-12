package com.gws.auto.mobile.android.ui.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  @Override
  public DashboardViewModel get() {
    return newInstance();
  }

  public static DashboardViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DashboardViewModel newInstance() {
    return new DashboardViewModel();
  }

  private static final class InstanceHolder {
    static final DashboardViewModel_Factory INSTANCE = new DashboardViewModel_Factory();
  }
}
