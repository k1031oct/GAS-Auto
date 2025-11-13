package com.gws.auto.mobile.android.ui.workflow.editor

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ActivityWorkflowEditorBinding
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@AndroidEntryPoint
class WorkflowEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkflowEditorBinding
    private val viewModel: WorkflowViewModel by viewModels()
    private lateinit var moduleAdapter: ModuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkflowEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, v.paddingBottom)
            insets
        }

        setupRecyclerView()
        observeViewModel()

        binding.addModuleButton.setOnClickListener {
            showAddModuleDialog()
        }

        binding.saveWorkflowButton.setOnClickListener {
            saveWorkflow()
        }
    }

    private fun setupRecyclerView() {
        moduleAdapter = ModuleAdapter(mutableListOf()) { position ->
            val module = moduleAdapter.getModules()[position]
            viewModel.removeModule(module)
        }
        binding.moduleRecyclerView.apply {
            adapter = moduleAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity)
        }

        val callback = ModuleTouchHelperCallback(moduleAdapter, viewModel)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.moduleRecyclerView)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.modules.collectLatest { modules ->
                moduleAdapter.updateModules(modules)
            }
        }
    }

    private fun showAddModuleDialog() {
        val moduleTypes = arrayOf("Delay", "IfElse", "Toast", "Log") // From the guide
        AlertDialog.Builder(this)
            .setTitle("Select Module")
            .setItems(moduleTypes) { _, which ->
                val selectedType = moduleTypes[which]
                viewModel.addModule(Module(id = UUID.randomUUID().toString(), type = selectedType, parameters = emptyMap()))
            }
            .show()
    }

    private fun saveWorkflow() {
        val name = binding.workflowNameEditor.text.toString()
        val description = binding.workflowDescriptionEditor.text.toString()

        if (name.isBlank()) {
            binding.workflowNameEditor.error = "Workflow name is required."
            return
        }

        lifecycleScope.launch {
            try {
                viewModel.saveNewWorkflow(name, description)
                Timber.i("Workflow '$name' saved successfully.")
                finish() // Close the editor on successful save
            } catch (e: Exception) {
                Timber.e(e, "Failed to save workflow.")
                // Optionally, show a toast or a snackbar to the user
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
