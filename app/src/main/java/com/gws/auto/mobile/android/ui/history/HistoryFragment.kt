package com.gws.auto.mobile.android.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupMenu()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { headerItem, position ->
            if (headerItem.isExpanded) {
                val logCount = headerItem.history.logs.lines().size
                historyList.subList(position + 1, position + 1 + logCount).clear()
                historyAdapter.notifyItemRangeRemoved(position + 1, logCount)
            } else {
                val logs = headerItem.history.logs.lines().map { HistoryListItem.LogItem(it) }
                historyList.addAll(position + 1, logs)
                historyAdapter.notifyItemRangeInserted(position + 1, logs.size)
            }
            headerItem.isExpanded = !headerItem.isExpanded
            historyAdapter.notifyItemChanged(position)
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = historyAdapter.currentList[position]
                if (item is HistoryListItem.HeaderItem) {
                    viewModel.deleteHistory(item.history)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.historyRecyclerView)
    }

    private fun observeViewModel() {
        viewModel.executionLogs
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { logs ->
                historyList.clear()
                historyList.addAll(logs.map { HistoryListItem.HeaderItem(it) })
                historyAdapter.submitList(historyList.toList()) // Use toList() to create a new list for the adapter
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete_all_history -> {
                        showDeleteAllConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDeleteAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All History")
            .setMessage("Are you sure you want to delete all execution history? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAllHistory()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
