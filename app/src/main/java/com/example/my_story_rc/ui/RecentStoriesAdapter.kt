package com.example.my_story_rc.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R

// Предположим, что "Последние истории" - это просто список строк (названий)
class RecentStoriesAdapter(
    private var stories: List<String> = emptyList() // Изменили тип на String, если используется список названий
) : RecyclerView.Adapter<RecentStoriesAdapter.RecentStoryViewHolder>() {

    fun updateStories(newStories: List<String>) { // Изменили параметр на List<String>
        stories = newStories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentStoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_story, parent, false) // Убедитесь, что файл существует
        return RecentStoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentStoryViewHolder, position: Int) {
        if (position >= 0 && position < stories.size) {
            holder.bind(stories[position])
        }
    }

    override fun getItemCount(): Int = stories.size

    class RecentStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storyTitle: TextView = itemView.findViewById(R.id.storyTitle) // Убедитесь, что ID правильный

        fun bind(title: String) { // Изменили параметр на String
            storyTitle.text = title // Установим название
        }
    }
}