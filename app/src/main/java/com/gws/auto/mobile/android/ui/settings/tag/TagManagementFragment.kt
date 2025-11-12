package com.gws.auto.mobile.android.ui.settings.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.databinding.FragmentTagManagementBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TagManagementFragment : Fragment() {

    private var _binding: FragmentTagManagementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TagManagementViewModel by viewModels()
    private lateinit var tagAdapter: TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViews()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        tagAdapter = TagAdapter { tag ->
            viewModel.deleteTag(tag)
        }
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tagRecyclerView.adapter = tagAdapter
    }

    private fun setupViews() {
        binding.addTagButton.setOnClickListener {
            val tagName = binding.tagNameInput.text.toString()
            viewModel.addTag(tagName)
            binding.tagNameInput.text.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.tags
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { tags ->
                tagAdapter.submitList(tags)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
