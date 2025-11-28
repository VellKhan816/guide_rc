package com.example.my_story_rc.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R
import com.example.my_story_rc.model.Chapter

class ChapterAdapter(
    private var chapters: MutableList<Chapter>, // <-- Изменено на var MutableList
    private val onChapterClick: (Chapter) -> Unit
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    // Метод для обновления списка
    fun updateChapters(newChapters: List<Chapter>) {
        chapters.clear() // <-- Теперь clear() доступен
        chapters.addAll(newChapters) // <-- Теперь addAll() доступен
        notifyDataSetChanged() // <-- Уведомляем об изменении
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ChapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(chapters[position], onChapterClick) // Исправлено: onChapterClick, а не onChapterClick
    }

    override fun getItemCount(): Int = chapters.size

    class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberView: TextView = itemView.findViewById(R.id.chapterNumber)
        private val titleView: TextView = itemView.findViewById(R.id.chapterTitle)

        fun bind(chapter: Chapter, clickListener: (Chapter) -> Unit) {
            numberView.text = "Глава ${chapter.number}"
            titleView.text = chapter.title

            itemView.setOnClickListener { clickListener(chapter) }
        }
    }
}