package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.local.db.TagDao;
import com.gws.auto.mobile.android.data.repository.TagRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideTagRepositoryFactory implements Factory<TagRepository> {
  private final Provider<TagDao> tagDaoProvider;

  private AppModule_ProvideTagRepositoryFactory(Provider<TagDao> tagDaoProvider) {
    this.tagDaoProvider = tagDaoProvider;
  }

  @Override
  public TagRepository get() {
    return provideTagRepository(tagDaoProvider.get());
  }

  public static AppModule_ProvideTagRepositoryFactory create(Provider<TagDao> tagDaoProvider) {
    return new AppModule_ProvideTagRepositoryFactory(tagDaoProvider);
  }

  public static TagRepository provideTagRepository(TagDao tagDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTagRepository(tagDao));
  }
}
