package com.gws.auto.mobile.android.domain.engine;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ActivityContext")
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
public final class WorkflowEngine_Factory implements Factory<WorkflowEngine> {
  private final Provider<Context> contextProvider;

  private WorkflowEngine_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WorkflowEngine get() {
    return newInstance(contextProvider.get());
  }

  public static WorkflowEngine_Factory create(Provider<Context> contextProvider) {
    return new WorkflowEngine_Factory(contextProvider);
  }

  public static WorkflowEngine newInstance(Context context) {
    return new WorkflowEngine(context);
  }
}
