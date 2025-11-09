package com.gws.auto.mobile.android.ui.workflow.editor;

import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
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
public final class WorkflowEditorActivity_MembersInjector implements MembersInjector<WorkflowEditorActivity> {
  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private WorkflowEditorActivity_MembersInjector(
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    this.workflowRepositoryProvider = workflowRepositoryProvider;
  }

  @Override
  public void injectMembers(WorkflowEditorActivity instance) {
    injectWorkflowRepository(instance, workflowRepositoryProvider.get());
  }

  public static MembersInjector<WorkflowEditorActivity> create(
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    return new WorkflowEditorActivity_MembersInjector(workflowRepositoryProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity.workflowRepository")
  public static void injectWorkflowRepository(WorkflowEditorActivity instance,
      WorkflowRepository workflowRepository) {
    instance.workflowRepository = workflowRepository;
  }
}
