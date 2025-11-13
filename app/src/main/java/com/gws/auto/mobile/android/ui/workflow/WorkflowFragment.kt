package com.gws.auto.mobile.android.ui.workflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.databinding.FragmentWorkflowBinding
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WorkflowFragment : Fragment() {

    private var _binding: FragmentWorkflowBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkflowViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    @Inject
    lateinit var workflowEngine: WorkflowEngine

    private lateinit var workflowAdapter: WorkflowAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkflowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")
        setupRecyclerView()
        observeViewModels()
    }

    private fun setupRecyclerView() {
        workflowAdapter = WorkflowAdapter(
            onRunClicked = { workflow ->
                lifecycleScope.launch {
                    try {
                        workflowEngine.execute(workflow.modules)
                        Timber.d("Workflow executed: ${workflow.name}")
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to execute workflow: ${workflow.name}")
                    }
                }
            },
            onEditClicked = { workflow ->
                val intent = Intent(activity, WorkflowEditorActivity::class.java)
                intent.putExtra("workflowId", workflow.id)
                startActivity(intent)
            },
            onDeleteClicked = { workflow -> viewModel.deleteWorkflow(workflow) },
            onAddClicked = {
                Timber.d("Add new workflow clicked")
                startActivity(Intent(activity, WorkflowEditorActivity::class.java))
            }
        )
        binding.workflowRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.workflowRecyclerView.adapter = workflowAdapter
    }

    private fun observeViewModels() {
        // Observe workflows list from the feature ViewModel
        viewModel.filteredWorkflows
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workflows ->
                Timber.d("Updating UI with ${workflows.size} workflows.")
                workflowAdapter.submitList(workflows)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // Observe search query from the shared ViewModel
        mainSharedViewModel.searchQuery
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { query ->
                viewModel.onQueryChanged(query)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
