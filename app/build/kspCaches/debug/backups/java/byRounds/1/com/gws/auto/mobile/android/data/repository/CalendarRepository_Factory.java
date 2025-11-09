package com.gws.auto.mobile.android.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CalendarRepository_Factory implements Factory<CalendarRepository> {
  private final Provider<Context> contextProvider;

  private CalendarRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CalendarRepository get() {
    return newInstance(contextProvider.get());
  }

  public static CalendarRepository_Factory create(Provider<Context> contextProvider) {
    return new CalendarRepository_Factory(contextProvider);
  }

  public static CalendarRepository newInstance(Context context) {
    return new CalendarRepository(context);
  }
}
