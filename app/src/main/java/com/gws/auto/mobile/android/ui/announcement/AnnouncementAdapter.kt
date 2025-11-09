package com.gws.auto.mobile.android.ui.announcement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Announcement

class AnnouncementAdapter(private val onItemClick: (Announcement) -> Unit) :
    ListAdapter<Announcement, AnnouncementAdapter.AnnouncementViewHolder>(AnnouncementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = getItem(position)
        holder.bind(announcement)
        holder.itemView.setOnClickListener { onItemClick(announcement) }
    }

    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.announcement_title)
        private val contentTextView: TextView = itemView.findViewById(R.id.announcement_content)
        private val unreadIndicator: ImageView = itemView.findViewById(R.id.unread_indicator)

        fun bind(announcement: Announcement) {
            titleTextView.text = announcement.title
            contentTextView.text = announcement.content
            unreadIndicator.isVisible = !announcement.isRead
        }
    }
}

class AnnouncementDiffCallback : DiffUtil.ItemCallback<Announcement>() {
    override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
        return oldItem == newItem
    }
}
