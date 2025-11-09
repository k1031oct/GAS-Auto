package com.gws.auto.mobile.android.ui.workflow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0013B\u001b\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\t\u001a\u00020\nH\u0016J\u001c\u0010\u000b\u001a\u00020\f2\n\u0010\r\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000e\u001a\u00020\nH\u0016J\u001c\u0010\u000f\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\nH\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/WorkflowAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/gws/auto/mobile/android/ui/workflow/WorkflowAdapter$WorkflowViewHolder;", "workflows", "", "Lcom/gws/auto/mobile/android/domain/model/Workflow;", "workflowEngine", "Lcom/gws/auto/mobile/android/domain/engine/WorkflowEngine;", "(Ljava/util/List;Lcom/gws/auto/mobile/android/domain/engine/WorkflowEngine;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "WorkflowViewHolder", "app_debug"})
public final class WorkflowAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.gws.auto.mobile.android.ui.workflow.WorkflowAdapter.WorkflowViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.gws.auto.mobile.android.domain.model.Workflow> workflows;
    @org.jetbrains.annotations.NotNull()
    private final com.gws.auto.mobile.android.domain.engine.WorkflowEngine workflowEngine = null;
    
    public WorkflowAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.gws.auto.mobile.android.domain.model.Workflow> workflows, @org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.domain.engine.WorkflowEngine workflowEngine) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.gws.auto.mobile.android.ui.workflow.WorkflowAdapter.WorkflowViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.ui.workflow.WorkflowAdapter.WorkflowViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/WorkflowAdapter$WorkflowViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/gws/auto/mobile/android/databinding/ListItemWorkflowBinding;", "(Lcom/gws/auto/mobile/android/ui/workflow/WorkflowAdapter;Lcom/gws/auto/mobile/android/databinding/ListItemWorkflowBinding;)V", "bind", "", "workflow", "Lcom/gws/auto/mobile/android/domain/model/Workflow;", "app_debug"})
    public final class WorkflowViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding binding = null;
        
        public WorkflowViewHolder(@org.jetbrains.annotations.NotNull()
        com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.gws.auto.mobile.android.domain.model.Workflow workflow) {
        }
    }
}