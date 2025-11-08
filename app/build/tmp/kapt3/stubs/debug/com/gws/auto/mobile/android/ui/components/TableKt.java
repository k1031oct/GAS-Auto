package com.gws.auto.mobile.android.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\u001aR\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032 \b\u0002\u0010\u0004\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0005\u00a2\u0006\u0002\b\u0007\u00a2\u0006\u0002\b\b2\u001c\u0010\t\u001a\u0018\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\u0005\u00a2\u0006\u0002\b\u0007\u00a2\u0006\u0002\b\bH\u0007\u001a\u001a\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\b\u0010\u000e\u001a\u00020\u0001H\u0007\u001a0\u0010\u000f\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u001c\u0010\t\u001a\u0018\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005\u00a2\u0006\u0002\b\u0007\u00a2\u0006\u0002\b\bH\u0007\u001a(\u0010\u0010\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\u001a(\u0010\u0013\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\u00a8\u0006\u0014"}, d2 = {"GwsTable", "", "modifier", "Landroidx/compose/ui/Modifier;", "header", "Lkotlin/Function1;", "Landroidx/compose/foundation/layout/RowScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "content", "Landroidx/compose/foundation/layout/ColumnScope;", "GwsTableCaption", "text", "", "GwsTablePreview", "GwsTableRow", "GwsTableCell", "weight", "", "GwsTableHead", "app_debug"})
public final class TableKt {
    
    @androidx.compose.runtime.Composable()
    public static final void GwsTable(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.RowScope, kotlin.Unit> header, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.ColumnScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GwsTableRow(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.RowScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GwsTableHead(@org.jetbrains.annotations.NotNull()
    androidx.compose.foundation.layout.RowScope $this$GwsTableHead, @org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, float weight) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GwsTableCell(@org.jetbrains.annotations.NotNull()
    androidx.compose.foundation.layout.RowScope $this$GwsTableCell, @org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, float weight) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GwsTableCaption(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true)
    @androidx.compose.runtime.Composable()
    public static final void GwsTablePreview() {
    }
}