// ReadingActivity.kt
package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Chapter
import kotlinx.coroutines.launch

class ReadingActivity : AppCompatActivity() {

    private lateinit var userRepo: UserPreferencesRepository
    private var chapterId: Int = -1
    private var chapter: Chapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)

        userRepo = UserPreferencesRepository(applicationContext)

        chapterId = intent.getIntExtra("chapter_id", -1)
        if (chapterId == -1) {
            Toast.makeText(this, "Глава не найдена", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Найдём главу по ID среди всех историй
        chapter = StoryRepository.allStories.flatMap { it.chapters }.find { it.id == chapterId }
        if (chapter == null) {
            Toast.makeText(this, "Глава не найдена", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        val chapterTitle = findViewById<TextView>(R.id.chapterTitle)
        val chapterContent = findViewById<TextView>(R.id.chapterContent)
        val btnNext = findViewById<Button>(R.id.btnNextChapter)
        val btnPrev = findViewById<Button>(R.id.btnPrevChapter)
        val btnBackToStory = findViewById<Button>(R.id.btnBackToStoryList)

        val chap = chapter ?: return

        chapterTitle.text = "${chap.title} (${chap.number})"
        chapterContent.text = chap.content // Установим содержимое

        // Настройка кнопок навигации
        btnNext.setOnClickListener {
            goToChapter(chap.number + 1)
        }

        btnPrev.setOnClickListener {
            goToChapter(chap.number - 1)
        }

        btnBackToStory.setOnClickListener {
            finish() // Закрывает ReadingActivity и возвращает к списку глав или деталям истории
        }

        // Здесь можно добавить логику проверки возраста, если глава принадлежит 18+ истории
        // и обновления прогресса пользователя при чтении.
        // lifecycleScope.launch { userRepo.updateStoryProgress(...) }
    }

    private fun goToChapter(number: Int) {
        val chap = chapter ?: return
        val story = StoryRepository.allStories.find { it.id == chap.storyId } ?: return

        val targetChapter = story.chapters.find { it.number == number }
        if (targetChapter != null) {
            // Обновляем прогресс в DataStore
            lifecycleScope.launch {
                userRepo.updateStoryProgress(
                    com.example.my_story_rc.model.StoryProgress( // Убедитесь, что вы используете правильный путь к StoryProgress
                        chap.storyId,
                        lastReadChapter = targetChapter.number,
                        isCompleted = false // Пока не завершена
                    )
                )
            }
            // Перезапускаем активность с новой главой
            val intent = Intent(this, ReadingActivity::class.java).apply {
                putExtra("chapter_id", targetChapter.id)
            }
            startActivity(intent)
            finish() // Закрываем текущую, чтобы не было цепочки
        } else {
            // Достигнут конец/начало истории
            if (number > story.chapters.maxOfOrNull { it.number } ?: 0) {
                // Достижение завершения истории
                lifecycleScope.launch {
                    // --- ИСПРАВЛЕНО: вызываем markStoryAsCompleted ---
                    userRepo.markStoryAsCompleted(chap.storyId) // <-- Используем новый метод
                }
                AlertDialog.Builder(this)
                    .setTitle("Поздравляем!")
                    .setMessage("Вы завершили историю \"${story.title}\"!")
                    .setPositiveButton("Вернуться к списку") { _, _ -> finish() }
                    .setCancelable(false)
                    .show()
            } else if (number < 1) {
                // Назад с первой главы
                Toast.makeText(this, "Вы на первой главе", Toast.LENGTH_SHORT).show()
            }
        }
    }
}