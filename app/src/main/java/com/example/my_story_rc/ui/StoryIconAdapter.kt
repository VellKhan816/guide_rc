package com.example.my_story_rc.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R
import com.example.my_story_rc.model.Story

class StoryIconAdapter(
    private var stories: List<Story> = emptyList(), // Инициализируем пустым списком
    private var onStoryClick: (Story) -> Unit = {} // Инициализируем пустой лямбдой
) : RecyclerView.Adapter<StoryIconAdapter.StoryIconViewHolder>() {

    fun updateStories(newStories: List<Story>) {
        stories = newStories
        notifyDataSetChanged() // Уведомляем об изменении данных
    }

    fun updateStoriesAndClickListener(newStories: List<Story>, newOnStoryClick: (Story) -> Unit) {
        stories = newStories
        onStoryClick = newOnStoryClick
        notifyDataSetChanged() // Уведомляем об изменении данных
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryIconViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false) // Используем файл разметки элемента списка
        return StoryIconViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryIconViewHolder, position: Int) {
        // Проверяем, что позиция действительна перед привязкой
        if (position >= 0 && position < stories.size) {
            holder.bind(stories[position], onStoryClick)
        }
    }

    override fun getItemCount(): Int = stories.size // Возвращаем размер списка

    class StoryIconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // --- ИСПРАВЛЕНО: Обновлены ID для соответствия item_story.xml ---
        private val cover: ImageView = itemView.findViewById(R.id.storyCover) // <-- Правильный ID для item_story.xml
        private val title: TextView = itemView.findViewById(R.id.storyTitle) // <-- Правильный ID для item_story.xml
        private val ageTag: TextView = itemView.findViewById(R.id.textAgeTag) // <-- Правильный ID для item_story.xml

        fun bind(story: Story, clickListener: (Story) -> Unit) {
            // Устанавливаем обложку
            cover.setImageResource(story.coverResId)

            // Устанавливаем название
            title.text = story.title

            // Устанавливаем видимость метки 18+
            ageTag.visibility = if (story.is18Plus) View.VISIBLE else View.GONE

            // Обработка клика по всей карточке
            itemView.setOnClickListener { clickListener(story) }
        }
    }
}