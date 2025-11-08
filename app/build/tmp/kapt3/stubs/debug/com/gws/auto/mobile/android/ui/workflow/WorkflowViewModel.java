package com.gws.auto.mobile.android.ui.workflow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\r\u001a\u00020\u000eR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u000f"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/WorkflowViewModel;", "Landroidx/lifecycle/ViewModel;", "workflowRepository", "Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;", "(Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;)V", "_workflows", "Landroidx/lifecycle/MutableLiveData;", "", "Lcom/gws/auto/mobile/android/domain/model/Workflow;", "workflows", "Landroidx/lifecycle/LiveData;", "getWorkflows", "()Landroidx/lifecycle/LiveData;", "loadWorkflows", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class WorkflowViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.gws.auto.mobile.android.data.repository.WorkflowRepository workflowRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.gws.auto.mobile.android.domain.model.Workflow>> _workflows = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.gws.auto.mobile.android.domain.model.Workflow>> workflows = null;
    
    @javax.inject.Inject()
    public WorkflowViewModel(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.data.repository.WorkflowRepository workflowRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.gws.auto.mobile.android.domain.model.Workflow>> getWorkflows() {
        return null;
    }
    
    public final void loadWorkflows() {
    }
}