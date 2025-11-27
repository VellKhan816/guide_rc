package com.example.my_story_rc.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.my_story_rc.R
import com.example.my_story_rc.model.Story

class TopStoryAdapter(
    private val context: Context,
    private val stories: List<Story>,
    private val onStoryClick: (Story) -> Unit
) : RecyclerView.Adapter<TopStoryAdapter.TopStoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopStoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return TopStoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopStoryViewHolder, position: Int) {
        holder.bind(stories[position]) { story ->
            onStoryClick(story)
        }
    }

    override fun getItemCount(): Int = stories.size

    inner class TopStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cover: ImageView = itemView.findViewById(R.id.storyCover)
        private val title: TextView = itemView.findViewById(R.id.storyTitle)
        private val description: TextView = itemView.findViewById(R.id.storyDescription)
        private val characters: TextView = itemView.findViewById(R.id.storyCharacters)
        private val status: TextView = itemView.findViewById(R.id.storyStatus)
        private val button: Button = itemView.findViewById(R.id.btnReadStory)

        fun bind(story: Story, clickListener: (Story) -> Unit) {
            // Загружаем обложку через Glide
            Glide.with(context)
                .load(story.coverResId)
                .placeholder(R.drawable.thecoverismissing) // заглушка при загрузке
                .error(R.drawable.thecoverismissing)      // при ошибке
                .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
                .into(cover)

            title.text = story.title
            description.text = story.description
            characters.text = "Главные герои:\n• " + story.characters.joinToString("\n• ")
            button.text = if (story.isCompleted) "Попробовать ещё раз" else "Новая история"

            // Статус и цвет
            status.text = if (story.isCompleted) "Завершена" else "В процессе"
            status.setTextColor(
                if (story.isCompleted) {
                    ContextCompat.getColor(context, R.color.status_completed)
                } else {
                    ContextCompat.getColor(context, R.color.status_in_progress)
                }
            )

            itemView.setOnClickListener { clickListener(story) }
        }
    }
}