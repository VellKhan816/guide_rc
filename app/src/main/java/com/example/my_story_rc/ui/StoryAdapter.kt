package com.example.my_story_rc.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R
import com.example.my_story_rc.model.Story

class StoryAdapter(
    private var stories: List<Story> = emptyList(), // Инициализируем пустым списком
    private var onStoryClick: (Story) -> Unit = {} // Инициализируем пустой лямбдой
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    fun updateStories(newStories: List<Story>) {
        stories = newStories
        notifyDataSetChanged() // Уведомляем об изменении данных
    }


    fun updateStoriesAndClickListener(newStories: List<Story>, newOnStoryClick: (Story) -> Unit) {
        stories = newStories
        onStoryClick = newOnStoryClick
        notifyDataSetChanged() // Уведомляем об изменении данных
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story_grid, parent, false) // Используем файл разметки элемента списка
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        // Проверяем, что позиция действительна перед привязкой
        if (position >= 0 && position < stories.size) {
            holder.bind(stories[position], onStoryClick)
        }
        // Если позиция недействительна, bind не вызывается, и элемент остаётся пустым (или с предыдущими данными, если recycleView reuse)
        // Однако, если список стал пустым, и вызван notifyDataSetChanged(), то recycleView очистит все элементы.
    }

    override fun getItemCount(): Int = stories.size // Возвращаем размер списка

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // --- Обновлены ID для соответствия item_story_grid.xml ---
        private val cover: ImageView = itemView.findViewById(R.id.storyCoverGrid)
        private val title: TextView = itemView.findViewById(R.id.storyTitleGrid)

        fun bind(story: Story, clickListener: (Story) -> Unit) {
            // Устанавливаем обложку
            cover.setImageResource(story.coverResId)

            // Устанавливаем название
            title.text = story.title

            // Обработка клика по всей карточке
            itemView.setOnClickListener { clickListener(story) }
        }
    }
}