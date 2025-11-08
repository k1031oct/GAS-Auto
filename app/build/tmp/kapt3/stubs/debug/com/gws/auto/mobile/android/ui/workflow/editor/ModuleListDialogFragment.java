package com.gws.auto.mobile.android.ui.workflow.editor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\rB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\u000e"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleListDialogFragment;", "Landroidx/fragment/app/DialogFragment;", "()V", "listener", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleListDialogFragment$ModuleListListener;", "getListener", "()Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleListDialogFragment$ModuleListListener;", "setListener", "(Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleListDialogFragment$ModuleListListener;)V", "onCreateDialog", "Landroid/app/Dialog;", "savedInstanceState", "Landroid/os/Bundle;", "ModuleListListener", "app_debug"})
public final class ModuleListDialogFragment extends androidx.fragment.app.DialogFragment {
    @org.jetbrains.annotations.Nullable()
    private com.gws.auto.mobile.android.ui.workflow.editor.ModuleListDialogFragment.ModuleListListener listener;
    
    public ModuleListDialogFragment() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.gws.auto.mobile.android.ui.workflow.editor.ModuleListDialogFragment.ModuleListListener getListener() {
        return null;
    }
    
    public final void setListener(@org.jetbrains.annotations.Nullable()
    com.gws.auto.mobile.android.ui.workflow.editor.ModuleListDialogFragment.ModuleListListener p0) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.app.Dialog onCreateDialog(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleListDialogFragment$ModuleListListener;", "", "onModuleSelected", "", "moduleType", "", "app_debug"})
    public static abstract interface ModuleListListener {
        
        public abstract void onModuleSelected(@org.jetbrains.annotations.NotNull()
        java.lang.String moduleType);
    }
}