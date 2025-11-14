package com.gws.auto.mobile.android.di;

import android.content.Context;
import com.gws.auto.mobile.android.data.local.db.AppDatabase;
import com.gws.auto.mobile.android.data.local.db.HistoryDao;
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
public final class AppModule_ProvideAppDatabaseFactory implements Factory<AppDatabase> {
  private final Provider<Context> appContextProvider;

  private final Provider<HistoryDao> historyDaoProvider;

  private AppModule_ProvideAppDatabaseFactory(Provider<Context> appContextProvider,
      Provider<HistoryDao> historyDaoProvider) {
    this.appContextProvider = appContextProvider;
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public AppDatabase get() {
    return provideAppDatabase(appContextProvider.get(), historyDaoProvider);
  }

  public static AppModule_ProvideAppDatabaseFactory create(Provider<Context> appContextProvider,
      Provider<HistoryDao> historyDaoProvider) {
    return new AppModule_ProvideAppDatabaseFactory(appContextProvider, historyDaoProvider);
  }

  public static AppDatabase provideAppDatabase(Context appContext,
      javax.inject.Provider<HistoryDao> historyDaoProvider) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAppDatabase(appContext, historyDaoProvider));
  }
}
