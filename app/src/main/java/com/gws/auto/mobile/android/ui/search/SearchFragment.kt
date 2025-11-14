package com.gws.auto.mobile.android.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentSearchBinding
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by viewModels({
        requireActivity()
    })

    private lateinit var tagAdapter: TagAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerViews() {
        tagAdapter = TagAdapter(
            onTagClicked = { tag ->
                mainSharedViewModel.setSearchQuery(tag.name)
                viewModel.addSearchHistory(tag.name)
                dismiss()
            },
            onTagLongClicked = { tag ->
                showDeleteConfirmationDialog(getString(R.string.delete_tag_confirmation), tag.name) { viewModel.deleteTag(tag) }
            },
            onAddTagClicked = {
                showAddTagDialog()
            }
        )
        binding.tagRecyclerView.adapter = tagAdapter

        historyAdapter = SearchHistoryAdapter(
            onHistoryItemClicked = { history ->
                mainSharedViewModel.setSearchQuery(history.query)
                dismiss()
            },
            onHistoryItemLongClicked = { history ->
                showDeleteConfirmationDialog(getString(R.string.delete_history_confirmation), history.query) { viewModel.deleteSearchHistoryItem(history.query) }
            }
        )
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun observeViewModel() {
        viewModel.tags.onEach { tags ->
            tagAdapter.submitList(tags)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.searchHistory.onEach { history ->
            historyAdapter.submitList(history)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupClickListeners() {
        binding.clearHistoryButton.setOnClickListener {
            showDeleteConfirmationDialog(getString(R.string.clear_search_history_confirmation), null) { viewModel.clearSearchHistory() }
        }
    }

    private fun showAddTagDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_tag_title)
            .setView(editText)
            .setPositiveButton(R.string.add) { _, _ ->
                val tagName = editText.text.toString()
                if (tagName.isNotBlank()) {
                    viewModel.addTag(tagName)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(message: String, itemName: String?, onConfirm: () -> Unit) {
        val finalMessage = if (itemName != null) "$message \"$itemName\"?" else message
        AlertDialog.Builder(requireContext())
            .setMessage(finalMessage)
            .setPositiveButton(R.string.delete) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
