package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Chapter
import com.example.my_story_rc.model.Story
import com.example.my_story_rc.ui.ChapterAdapter // Импортируем адаптер
import kotlinx.coroutines.launch

class ChapterListActivity : AppCompatActivity() {

    private lateinit var repo: UserPreferencesRepository
    private var storyId: Int = -1
    private var story: Story? = null
    private var adapter: ChapterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_list)

        repo = UserPreferencesRepository(applicationContext)

        storyId = intent.getIntExtra("story_id", -1)
        if (storyId == -1) {
            // Обработка ошибки: история не найдена
            finish()
            return
        }

        story = StoryRepository.allStories.find { it.id == storyId }
        if (story == null) {
            // Обработка ошибки: история не найдена
            finish()
            return
        }

        setupUI()
        observeData()
    }

    private fun setupUI() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChapters)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // --- ИСПРАВЛЕНО: Явно создаём пустой MutableList ---
        adapter = ChapterAdapter(mutableListOf()) { chapter -> // <-- Передаём MutableList<Chapter>
            // При клике на главу, запускаем ReadingActivity
            val intent = Intent(this, ReadingActivity::class.java).apply {
                putExtra("chapter_id", chapter.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Установка заголовка
        findViewById<TextView>(R.id.tvChapterListTitle).text = "${story?.title} - Главы"

        // Обработчик кнопки "назад"
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            // Загружаем главы для истории и обновляем адаптер
            val chapters = story?.chapters ?: emptyList() // <-- Тип List<Chapter>
            // --- ИСПРАВЛЕНО: Передаём List, но updateChapters внутри адаптера преобразует его ---
            // Адаптер сам должен преобразовать List в MutableList внутри updateChapters
            adapter?.updateChapters(chapters) // <-- Передаём List<Chapter>, адаптер сам преобразует
        }
    }
}