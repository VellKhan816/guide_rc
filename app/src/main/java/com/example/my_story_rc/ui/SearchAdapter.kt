package com.example.my_story_rc.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R
import com.example.my_story_rc.model.Story

class SearchAdapter(
    private var stories: List<Story> = emptyList(),
    private val onStoryClick: (Story) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    fun updateStories(newStories: List<Story>) {
        stories = newStories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story_grid, parent, false) // Используем тот же файл разметки, что и StoryAdapter
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(stories[position], onStoryClick)
    }

    override fun getItemCount(): Int = stories.size

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cover: ImageView = itemView.findViewById(R.id.storyCoverGrid)
        private val title: TextView = itemView.findViewById(R.id.storyTitleGrid)

        fun bind(story: Story, clickListener: (Story) -> Unit) {
            cover.setImageResource(story.coverResId)
            title.text = story.title
            itemView.setOnClickListener { clickListener(story) }
        }
    }
}