package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Story
import com.example.my_story_rc.ui.SearchAdapter // Создадим чуть позже
import com.example.my_story_rc.utils.calculateAge
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var userRepo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // Убедитесь, что файл существует

        searchInput = findViewById(R.id.searchInput)
        backButton = findViewById(R.id.backButton)
        recyclerView = findViewById(R.id.recyclerViewSearchResults)
        userRepo = UserPreferencesRepository(applicationContext)

        // Инициализация адаптера
        adapter = SearchAdapter { story ->
            lifecycleScope.launch {
                val userAge = userRepo.dateOfBirth.first()?.let { calculateAge(it) } ?: -1
                if (story.is18Plus && userAge < 18) {
                    // Показать диалог ограничения по возрасту, как в MainActivity или StoryDetailActivity
                    androidx.appcompat.app.AlertDialog.Builder(this@SearchActivity)
                        .setTitle("Ограничение по возрасту")
                        .setMessage("Доступно только с 18+")
                        .setPositiveButton("Понятно", null)
                        .show()
                    return@launch
                }
                // Если возраст подходит, открываем StoryDetailActivity
                val intent = Intent(this@SearchActivity, StoryDetailActivity::class.java).apply {
                    putExtra("story_id", story.id)
                }
                startActivity(intent)
            }
        }
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Такой же как в MainActivity
        recyclerView.adapter = adapter

        // Обработчик для кнопки "Назад"
        backButton.setOnClickListener {
            finish() // Закрывает SearchActivity и возвращает на предыдущую
        }

        // Слушатель изменений текста для поиска
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            val userAge = userRepo.dateOfBirth.first()?.let { calculateAge(it) } ?: -1
            val allStories = StoryRepository.allStories

            // Фильтрация: по названию и по возрасту
            val results = if (query.isBlank()) {
                emptyList() // Не показываем результаты, если запрос пуст
            } else {
                allStories.filter { story ->
                    story.title.contains(query, ignoreCase = true) && // Поиск по названию
                            (!story.is18Plus || userAge >= 18) // Проверка возраста
                }
            }

            adapter.updateStories(results)
        }
    }
}