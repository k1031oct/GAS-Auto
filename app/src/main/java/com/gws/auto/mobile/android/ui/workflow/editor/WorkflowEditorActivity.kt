package com.gws.auto.mobile.android.ui.workflow.editor

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.databinding.ActivityWorkflowEditorBinding
import com.gws.auto.mobile.android.domain.model.Module
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@AndroidEntryPoint
class WorkflowEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkflowEditorBinding
    private val viewModel: WorkflowEditorViewModel by viewModels()
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var libraryAdapter: ModuleLibraryAdapter

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

        val workflowId = intent.getStringExtra("workflowId")
        if (workflowId != null) {
            viewModel.loadWorkflow(workflowId)
        }

        setupRecyclerView()
        setupLibraryRecyclerView()
        setupDragAndDrop()
        observeViewModel()

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

    private fun setupLibraryRecyclerView() {
        val libraryModules = listOf(
            Module(id = "", type = "Delay", parameters = emptyMap()),
            Module(id = "", type = "IfElse", parameters = emptyMap()),
            Module(id = "", type = "Toast", parameters = emptyMap()),
            Module(id = "", type = "Log", parameters = emptyMap())
        )

        libraryAdapter = ModuleLibraryAdapter(libraryModules) { module, view ->
            val item = ClipData.Item(module.type)
            val dragData = ClipData(
                module.type,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            val myShadow = View.DragShadowBuilder(view)
            view.startDragAndDrop(dragData, myShadow, null, 0)
            true
        }
        binding.libraryRecyclerView.apply {
            adapter = libraryAdapter
            layoutManager = LinearLayoutManager(this@WorkflowEditorActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupDragAndDrop() {
        binding.moduleRecyclerView.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val moduleType = item.text.toString()
                    viewModel.addModule(Module(id = UUID.randomUUID().toString(), type = moduleType, parameters = emptyMap()))
                    true
                }
                else -> true
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.modules.collectLatest { modules ->
                moduleAdapter.updateModules(modules)
            }
        }
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
