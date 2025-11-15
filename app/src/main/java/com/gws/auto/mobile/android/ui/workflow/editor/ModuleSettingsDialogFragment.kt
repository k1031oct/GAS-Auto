package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.google.android.material.textfield.TextInputLayout
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentModuleSettingsBinding
import com.gws.auto.mobile.android.domain.model.Module
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ModuleSettingsDialogFragment(private val module: Module) : DialogFragment() {

    private var _binding: FragmentModuleSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkflowEditorViewModel by activityViewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    // General file picker
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedFileId: String? = null
    private var selectedFileName: String? = null
    private lateinit var selectedFileNameView: TextView

    // Drive Folder picker
    private lateinit var folderPickerLauncher: ActivityResultLauncher<Intent>
    private var destinationFolderId: String? = null
    private var destinationFolderName: String? = null
    private lateinit var destinationFolderNameView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    selectedFileId = it.toString()
                    selectedFileName = it.lastPathSegment
                    selectedFileNameView.text = "Source File: $selectedFileName"
                }
            }
        }
        folderPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    destinationFolderId = it.toString()
                    destinationFolderName = it.lastPathSegment
                    destinationFolderNameView.text = "Destination: $destinationFolderName"
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.moduleType.text = module.type
        binding.parametersContainer.removeAllViews()

        when (module.type) {
            "DEFINE_VARIABLE" -> setupDefineVariableUI()
            "GET_RELATIVE_DATE" -> setupGetRelativeDateUI()
            "CREATE_GMAIL_DRAFT" -> setupCreateGmailDraftUI()
            "DUPLICATE_SPREADSHEET" -> setupDuplicateSpreadsheetUI()
            else -> setupDefaultUI()
        }

        binding.cancelButton.setOnClickListener { dismiss() }
    }

    private fun setupDefineVariableUI() { /* ... existing code ... */ }
    private fun setupGetRelativeDateUI() { /* ... existing code ... */ }
    private fun setupCreateGmailDraftUI() { /* ... existing code ... */ }

    private fun setupDuplicateSpreadsheetUI() {
        selectedFileId = module.parameters["sourceFileId"]
        selectedFileName = module.parameters["sourceFileName"]
        destinationFolderId = module.parameters["destinationFolderId"]
        destinationFolderName = module.parameters["destinationFolderName"]

        val newFileNameInput = createTextInputLayout("New File Name", module.parameters["newFileName"])

        val sourceFileButton = Button(requireContext()).apply { text = "Select Source Spreadsheet" }
        selectedFileNameView = TextView(requireContext()).apply { text = "Source File: ${selectedFileName ?: "None"}" }
        sourceFileButton.setOnClickListener { openDrivePicker(isFolder = false) }

        val destFolderButton = Button(requireContext()).apply { text = "Select Destination Folder" }
        destinationFolderNameView = TextView(requireContext()).apply { text = "Destination: ${destinationFolderName ?: "Same as source"}" }
        destFolderButton.setOnClickListener { openDrivePicker(isFolder = true) }

        binding.parametersContainer.addView(sourceFileButton)
        binding.parametersContainer.addView(selectedFileNameView)
        binding.parametersContainer.addView(newFileNameInput)
        binding.parametersContainer.addView(destFolderButton)
        binding.parametersContainer.addView(destinationFolderNameView)

        binding.saveButton.setOnClickListener {
            val params = mutableMapOf(
                "newFileName" to newFileNameInput.editText?.text.toString()
            )
            selectedFileId?.let { 
                params["sourceFileId"] = it
                params["sourceFileName"] = selectedFileName ?: ""
            }
            destinationFolderId?.let { 
                params["destinationFolderId"] = it 
                params["destinationFolderName"] = destinationFolderName ?: ""
            }
            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }
    
    private fun openDrivePicker(isFolder: Boolean) {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account == null || !GoogleSignIn.hasPermissions(account, Scope(DriveScopes.DRIVE_FILE))) {
            GoogleSignIn.requestPermissions(requireActivity(), 1, account, Scope(DriveScopes.DRIVE_FILE))
            return // Wait for permission result before opening picker
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = if (isFolder) "application/vnd.google-apps.folder" else "application/vnd.google-apps.spreadsheet"
        }
        if (isFolder) {
            folderPickerLauncher.launch(intent)
        } else {
            filePickerLauncher.launch(intent)
        }
    }

    private fun setupDefaultUI() { /* ... existing code ... */ }
    private fun createTextInputLayout(hint: String, initialValue: String?, isNumeric: Boolean = false, isMultiLine: Boolean = false): TextInputLayout { /* ... existing code ... */ }
    private fun createSpinner(itemsArrayRes: Int, selectedValue: String?): Spinner { /* ... existing code ... */ }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
