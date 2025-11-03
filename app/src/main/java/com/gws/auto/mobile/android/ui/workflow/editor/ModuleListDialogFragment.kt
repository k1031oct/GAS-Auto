package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ModuleListDialogFragment : DialogFragment() {

    interface ModuleListListener {
        fun onModuleSelected(moduleType: String)
    }

    var listener: ModuleListListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val moduleTypes = arrayOf("LOG_MESSAGE", "SHOW_TOAST", "CREATE_PDF_FROM_SHEET", "CONVERT_EXCEL_TO_SHEET")

        return AlertDialog.Builder(requireContext())
            .setTitle("Select a module")
            .setItems(moduleTypes) { _, which ->
                listener?.onModuleSelected(moduleTypes[which])
            }
            .create()
    }
}
