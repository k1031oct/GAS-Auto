package com.gws.auto.mobile.android.ui.workflow.editor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.databinding.ActivityWorkflowEditorBinding
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class WorkflowEditorActivity : AppCompatActivity(), ModuleListDialogFragment.ModuleListListener, ModuleParameterDialogFragment.ModuleParameterListener {

    private lateinit var binding: ActivityWorkflowEditorBinding
    private lateinit var moduleAdapter: ModuleAdapter

    @Inject
    lateinit var workflowRepository: WorkflowRepository

    private val modules = mutableListOf<Module>()
    private val tags = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkflowEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moduleAdapter = ModuleAdapter(modules)
        binding.moduleRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.moduleRecyclerView.adapter = moduleAdapter

        val callback = ModuleItemTouchHelperCallback(moduleAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.moduleRecyclerView)

        binding.addModuleButton.setOnClickListener {
            val dialog = ModuleListDialogFragment()
            dialog.listener = this
            dialog.show(supportFragmentManager, "ModuleListDialogFragment")
        }

        binding.addTagButton.setOnClickListener {
            val tagText = binding.tagEditor.text.toString().trim()
            if (tagText.isNotEmpty() && !tags.contains(tagText)) {
                tags.add(tagText)
                addChipToGroup(tagText)
                binding.tagEditor.text.clear()
            }
        }

        binding.saveWorkflowButton.setOnClickListener {
            saveWorkflow()
        }
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(this).apply {
            text = tag
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.tagChipGroup.removeView(this)
                tags.remove(tag)
            }
        }
        binding.tagChipGroup.addView(chip)
    }

    override fun onModuleSelected(moduleType: String) {
        val dialog = ModuleParameterDialogFragment.newInstance(moduleType)
        dialog.listener = this
        dialog.show(supportFragmentManager, "ModuleParameterDialogFragment")
    }

    override fun onModuleParametersSet(module: Module) {
        modules.add(module.copy(id = UUID.randomUUID().toString()))
        moduleAdapter.notifyItemInserted(modules.size - 1)
    }

    private fun saveWorkflow() {
        val workflow = Workflow(
            id = UUID.randomUUID().toString(),
            name = binding.workflowNameEditor.text.toString(),
            description = binding.workflowDescriptionEditor.text.toString(),
            modules = modules,
            status = WORKFLOW_STATUS_PENDING,
            trigger = WORKFLOW_TRIGGER_MANUAL,
            tags = tags
        )

        workflowRepository.saveWorkflow(workflow).addOnSuccessListener {
            Toast.makeText(this, "Workflow saved", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error saving workflow: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val WORKFLOW_STATUS_PENDING = "Pending"
        private const val WORKFLOW_TRIGGER_MANUAL = "Manual"
    }
}
