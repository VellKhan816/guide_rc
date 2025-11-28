package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Story
import com.example.my_story_rc.utils.calculateAge
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var userRepo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ИСПРАВЛЕНО: Используем правильный файл разметки для деталей истории
        setContentView(R.layout.item_story)

        userRepo = UserPreferencesRepository(applicationContext)

        // Получаем ID истории из Intent
        val storyId = intent.getIntExtra("story_id", -1) // -1 как значение по умолчанию
        if (storyId == -1) {
            Toast.makeText(this, "История не найдена", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Находим историю по ID
        val story = StoryRepository.allStories.find { it.id == storyId }
        if (story == null) {
            Toast.makeText(this, "История не найдена", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Проверяем возрастные ограничения
        lifecycleScope.launch {
            val userAge = userRepo.dateOfBirth.first()?.let { calculateAge(it) } ?: -1
            if (story.is18Plus && userAge < 18) {
                showAgeRestrictionDialog()
                return@launch
            }

            // Если проверка пройдена, находим View и устанавливаем данные
            // Находим View (ID должны соответствовать activity_story_detail.xml)
            val backButton = findViewById<ImageView>(R.id.backButton) // Убедитесь, что ID правильный
            val cover = findViewById<ImageView>(R.id.storyCover)     // Убедитесь, что ID правильный
            val titleView = findViewById<TextView>(R.id.storyTitle)  // Убедитесь, что ID правильный
            val descView = findViewById<TextView>(R.id.storyDescription) // Убедитесь, что ID правильный
            val statusView = findViewById<TextView>(R.id.storyStatus) // <-- Убедитесь, что ID правильный
            val charactersView = findViewById<TextView>(R.id.storyCharacters) // <-- Убедитесь, что ID правильный
            val readButton = findViewById<Button>(R.id.btnReadStory) // Убедитесь, что ID правильный
            val backButtonTop = findViewById<ImageView>(R.id.backButton) // <-- ID может быть одинаковым, но убедитесь, что он уникален или используйте другой ID, например, R.id.backButtonTop

            // Устанавливаем данные
            cover.setImageResource(story.coverResId)
            titleView.text = story.title
            descView.text = story.description // Используем реальное описание из модели
            // Устанавливаем статус
            statusView.text = if (story.isCompleted) "Завершена" else "В процессе"
            // Устанавливаем персонажей
            charactersView.text = "Главные герои:\n• ${story.characters.joinToString("\n• ")}"

            // Обработка кнопок
            backButton.setOnClickListener { finish() }
            readButton.setOnClickListener {
                // Здесь можно открыть список глав или начать сюжет
                val intent = Intent(this@StoryDetailActivity, ChapterListActivity::class.java).apply { // Запускаем ChapterListActivity
                    putExtra("story_id", story.id) // Передаём ID истории
                }
                startActivity(intent)
                // finish() // Не вызываем finish(), чтобы пользователь мог вернуться к деталям истории
            }
            // ... установка слушателей для других кнопок ...
        }
    }


    private fun showAgeRestrictionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Ограничение по возрасту")
            .setMessage("Эта история доступна только пользователям старше 18 лет.")
            .setPositiveButton("Понятно") { _, _ -> finish() } // Закрываем активность при нажатии "Понятно"
            .setCancelable(false)
            .show()
    }
}