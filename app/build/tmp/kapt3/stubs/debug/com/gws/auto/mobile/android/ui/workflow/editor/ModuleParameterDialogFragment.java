package com.gws.auto.mobile.android.ui.workflow.editor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \r2\u00020\u0001:\u0002\r\u000eB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\u000f"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment;", "Landroidx/fragment/app/DialogFragment;", "()V", "listener", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment$ModuleParameterListener;", "getListener", "()Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment$ModuleParameterListener;", "setListener", "(Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment$ModuleParameterListener;)V", "onCreateDialog", "Landroid/app/Dialog;", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "ModuleParameterListener", "app_debug"})
public final class ModuleParameterDialogFragment extends androidx.fragment.app.DialogFragment {
    @org.jetbrains.annotations.Nullable()
    private com.gws.auto.mobile.android.ui.workflow.editor.ModuleParameterDialogFragment.ModuleParameterListener listener;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ARG_MODULE_TYPE = "module_type";
    @org.jetbrains.annotations.NotNull()
    public static final com.gws.auto.mobile.android.ui.workflow.editor.ModuleParameterDialogFragment.Companion Companion = null;
    
    public ModuleParameterDialogFragment() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.gws.auto.mobile.android.ui.workflow.editor.ModuleParameterDialogFragment.ModuleParameterListener getListener() {
        return null;
    }
    
    public final void setListener(@org.jetbrains.annotations.Nullable()
    com.gws.auto.mobile.android.ui.workflow.editor.ModuleParameterDialogFragment.ModuleParameterListener p0) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.app.Dialog onCreateDialog(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment$Companion;", "", "()V", "ARG_MODULE_TYPE", "", "newInstance", "Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment;", "moduleType", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.gws.auto.mobile.android.ui.workflow.editor.ModuleParameterDialogFragment newInstance(@org.jetbrains.annotations.NotNull()
        java.lang.String moduleType) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/gws/auto/mobile/android/ui/workflow/editor/ModuleParameterDialogFragment$ModuleParameterListener;", "", "onModuleParametersSet", "", "module", "Lcom/gws/auto/mobile/android/domain/model/Module;", "app_debug"})
    public static abstract interface ModuleParameterListener {
        
        public abstract void onModuleParametersSet(@org.jetbrains.annotations.NotNull()
        com.gws.auto.mobile.android.domain.model.Module module);
    }
}