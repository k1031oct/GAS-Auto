package com.gws.auto.mobile.android.ui.workflow.editor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0015B\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\bH\u0016J\u0018\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u000e\u0010\u0011\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\bJ\u0016\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\bR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleAdapter$ModuleViewHolder;", "modules", "", "Lcom/gws/auto/mobile/android/domain/model/Module;", "(Ljava/util/List;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "onItemDismiss", "onItemMove", "fromPosition", "toPosition", "ModuleViewHolder", "app_debug"})
public final class ModuleAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.gws.auto.mobile.android.ui.workflow.editor.ModuleAdapter.ModuleViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.gws.auto.mobile.android.domain.model.Module> modules = null;
    
    public ModuleAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.gws.auto.mobile.android.domain.model.Module> modules) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.gws.auto.mobile.android.ui.workflow.editor.ModuleAdapter.ModuleViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.gws.auto.mobile.android.ui.workflow.editor.ModuleAdapter.ModuleViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void onItemMove(int fromPosition, int toPosition) {
    }
    
    public final void onItemDismiss(int position) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleAdapter$ModuleViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "moduleType", "Landroid/widget/TextView;", "getModuleType", "()Landroid/widget/TextView;", "app_debug"})
    public static final class ModuleViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView moduleType = null;
        
        public ModuleViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View itemView) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getModuleType() {
            return null;
        }
    }
}