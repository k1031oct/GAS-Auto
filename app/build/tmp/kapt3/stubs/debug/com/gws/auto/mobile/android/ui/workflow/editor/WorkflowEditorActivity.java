package com.gws.auto.mobile.android.ui.workflow.editor;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u0000 \u001c2\u00020\u00012\u00020\u00022\u00020\u0003:\u0001\u001cB\u0005\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\u0010\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u000bH\u0016J\u0010\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u0013H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001d"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/WorkflowEditorActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleListDialogFragment$ModuleListListener;", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment$ModuleParameterListener;", "()V", "binding", "Lcom/gws/auto/mobile/android/databinding/ActivityWorkflowEditorBinding;", "moduleAdapter", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleAdapter;", "modules", "", "Lcom/gws/auto/mobile/android/domain/model/Module;", "workflowRepository", "Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;", "getWorkflowRepository", "()Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;", "setWorkflowRepository", "(Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;)V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onModuleParametersSet", "module", "onModuleSelected", "moduleType", "", "saveWorkflow", "Companion", "app_debug"})
public final class WorkflowEditorActivity extends androidx.appcompat.app.AppCompatActivity implements com.gws.auto.mobile.android.ui.workflow.editor.ModuleListDialogFragment.ModuleListListener, com.gws.auto.mobile.android.ui.workflow.editor.ModuleParameterDialogFragment.ModuleParameterListener {
    private com.gws.auto.mobile.android.databinding.ActivityWorkflowEditorBinding binding;
    private com.gws.auto.mobile.android.ui.workflow.editor.ModuleAdapter moduleAdapter;
    @javax.inject.Inject()
    public com.gws.auto.mobile.android.data.repository.WorkflowRepository workflowRepository;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.gws.auto.mobile.android.domain.model.Module> modules = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String WORKFLOW_STATUS_PENDING = "Pending";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String WORKFLOW_TRIGGER_MANUAL = "Manual";
    @org.jetbrains.annotations.NotNull()
    public static final com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity.Companion Companion = null;
    
    public WorkflowEditorActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.gws.auto.mobile.android.data.repository.WorkflowRepository getWorkflowRepository() {
        return null;
    }
    
    public final void setWorkflowRepository(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.data.repository.WorkflowRepository p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onModuleSelected(@org.jetbrains.annotations.NotNull()
    java.lang.String moduleType) {
    }
    
    @java.lang.Override()
    public void onModuleParametersSet(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.domain.model.Module module) {
    }
    
    private final void saveWorkflow() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/WorkflowEditorActivity$Companion;", "", "()V", "WORKFLOW_STATUS_PENDING", "", "WORKFLOW_TRIGGER_MANUAL", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}