package com.gws.auto.mobile.android.ui.workflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.databinding.FragmentWorkflowBinding
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity

class WorkflowFragment : Fragment() {

    private var _binding: FragmentWorkflowBinding? = null
    private val binding get() = _binding!!

    private val workflowRepository = WorkflowRepository()

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
        binding.workflowRecyclerView.layoutManager = LinearLayoutManager(context)

        workflowRepository.getAllWorkflows().addOnSuccessListener { documents ->
            val workflows = documents.toObjects(Workflow::class.java)
            binding.workflowRecyclerView.adapter = WorkflowAdapter(workflows)
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }

        binding.fabAddWorkflow.setOnClickListener {
            startActivity(Intent(activity, WorkflowEditorActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "WorkflowFragment"
    }
}
