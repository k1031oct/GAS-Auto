package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gws.auto.mobile.android.domain.model.Module

class ModuleParameterDialogFragment : DialogFragment() {

    interface ModuleParameterListener {
        fun onModuleParametersSet(module: Module)
    }

    var listener: ModuleParameterListener? = null

    companion object {
        private const val ARG_MODULE_TYPE = "module_type"

        fun newInstance(moduleType: String): ModuleParameterDialogFragment {
            val args = Bundle()
            args.putString(ARG_MODULE_TYPE, moduleType)
            val fragment = ModuleParameterDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val moduleType = requireArguments().getString(ARG_MODULE_TYPE)!!

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }

        val parameters = mutableMapOf<String, EditText>()

        when (moduleType) {
            "LOG_MESSAGE", "SHOW_TOAST" -> {
                val messageField = EditText(requireContext()).apply { hint = "Message" }
                parameters["message"] = messageField
                layout.addView(messageField)
            }
            "CREATE_PDF_FROM_SHEET" -> {
                val sheetIdField = EditText(requireContext()).apply { hint = "Spreadsheet ID" }
                parameters["sheet_id"] = sheetIdField
                layout.addView(sheetIdField)
            }
            "CONVERT_EXCEL_TO_SHEET" -> {
                val excelFileIdField = EditText(requireContext()).apply { hint = "Excel File ID" }
                parameters["excel_file_id"] = excelFileIdField
                layout.addView(excelFileIdField)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Set parameters for $moduleType")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val moduleParameters = parameters.mapValues { it.value.text.toString() }
                val module = Module(
                    id = "", // Will be set later
                    type = moduleType,
                    parameters = moduleParameters
                )
                listener?.onModuleParametersSet(module)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
