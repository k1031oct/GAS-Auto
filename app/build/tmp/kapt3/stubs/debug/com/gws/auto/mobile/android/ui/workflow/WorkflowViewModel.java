package com.gws.auto.mobile.android.ui.workflow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u000f\u001a\u00020\u0007R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001d\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\f\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/WorkflowViewModel;", "Landroidx/lifecycle/ViewModel;", "workflowRepository", "Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;", "(Lcom/gws/auto/mobile/android/data/repository/WorkflowRepository;)V", "_query", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_workflows", "", "Lcom/gws/auto/mobile/android/domain/model/Workflow;", "filteredWorkflows", "Lkotlinx/coroutines/flow/StateFlow;", "getFilteredWorkflows", "()Lkotlinx/coroutines/flow/StateFlow;", "query", "getQuery$annotations", "()V", "getQuery", "loadWorkflows", "", "onQueryChanged", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class WorkflowViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.gws.auto.mobile.android.data.repository.WorkflowRepository workflowRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.gws.auto.mobile.android.domain.model.Workflow>> _workflows = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _query = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> query = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.gws.auto.mobile.android.domain.model.Workflow>> filteredWorkflows = null;
    
    @javax.inject.Inject()
    public WorkflowViewModel(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.data.repository.WorkflowRepository workflowRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getQuery() {
        return null;
    }
    
    @kotlin.Suppress(names = {"unused"})
    @java.lang.Deprecated()
    public static void getQuery$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.gws.auto.mobile.android.domain.model.Workflow>> getFilteredWorkflows() {
        return null;
    }
    
    public final void onQueryChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void loadWorkflows() {
    }
}