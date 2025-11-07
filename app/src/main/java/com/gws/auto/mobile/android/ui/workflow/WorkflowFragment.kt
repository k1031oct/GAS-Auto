package com.gws.auto.mobile.android.ui.workflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.databinding.FragmentWorkflowBinding
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WorkflowFragment : Fragment() {

    private var _binding: FragmentWorkflowBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkflowViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

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
        binding.workflowRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.fabAddWorkflow.setOnClickListener {
            Timber.d("fabAddWorkflow clicked")
            startActivity(Intent(activity, WorkflowEditorActivity::class.java))
        }

        viewModel.workflows.observe(viewLifecycleOwner) { workflows ->
            binding.workflowRecyclerView.adapter = WorkflowAdapter(workflows)
            Timber.d("Successfully loaded ${workflows.size} workflows.")
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume called")
        updateUI()
    }

    private fun updateUI() {
        if (auth.currentUser != null) {
            // User is logged in
            Timber.d("User is logged in. Loading workflows.")
            binding.workflowRecyclerView.isVisible = true
            binding.loginPromptText.isVisible = false
            binding.fabAddWorkflow.show()
            viewModel.loadWorkflows()
        } else {
            // User is not logged in
            Timber.d("User is not logged in.")
            binding.workflowRecyclerView.isVisible = false
            binding.loginPromptText.isVisible = true
            binding.fabAddWorkflow.hide()
            binding.workflowRecyclerView.adapter = WorkflowAdapter(emptyList()) // Clear the list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
