package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView // Импортируем ImageView
import android.widget.LinearLayout // Импортируем LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Story
import com.example.my_story_rc.ui.StoryAdapter
import com.example.my_story_rc.utils.calculateAge
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private var adapter: StoryAdapter? = null
    private lateinit var userRepo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRepo = UserPreferencesRepository(this)

        lifecycleScope.launch {
            userRepo.isProfileComplete.collect { isComplete ->
                if (!isComplete) {
                    startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                    finish()
                    return@collect
                }
                initializeUI()
            }
        }
    }

    private fun initializeUI() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTopStories)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = StoryAdapter(emptyList()) { /* заглушка */ }
        recyclerView.adapter = adapter

        // --- ИСПОЛЬЗУЕМ СПИСОК ТОП 20 ИСТОРИЙ ---
        val allStories = StoryRepository.top20Stories
        lifecycleScope.launch {
            userRepo.dateOfBirth.collect { dob ->
                if (dob == null) {
                    showProfileError()
                    return@collect
                }
                val userAge = calculateAge(dob)
                if (userAge !in 1..130) {
                    showProfileError()
                    return@collect
                }

                // --- ФИЛЬТРУЕМ ТОП 20 ПО ВОЗРАСТУ ---
                val allowedStories = if (userAge >= 18) allStories else allStories.filter { !it.is18Plus }

                adapter?.updateStoriesAndClickListener(allowedStories) { story ->
                    if (story.is18Plus && userAge < 18) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Ограничение по возрасту")
                            .setMessage("Доступно только с 18+")
                            .setPositiveButton("Понятно", null)
                            .show()
                        // --- ИСПРАВЛЕНО: возвращаемся из лямбды updateStoriesAndClickListener ---
                        return@updateStoriesAndClickListener
                    }
                    startActivity(Intent(this@MainActivity, StoryDetailActivity::class.java).apply {
                        putExtra("story_id", story.id)
                    })
                }
            }
        }
        setupNavigation()
    }

    private fun setupNavigation() {
        // ИСПРАВЛЕНО: Используем findViewById с правильным типом View (LinearLayout или ImageView)
        // ВАЖНО: Убедитесь, что в activity_main.xml ID nav_stories, nav_profile, nav_app_settings, nav_exit
        // принадлежат LinearLayout. Если они ImageView, замените findViewById<LinearLayout> на findViewById<ImageView>.
        findViewById<LinearLayout>(R.id.nav_stories).setOnClickListener {
            startActivity(Intent(this, StoriesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_app_settings).setOnClickListener { // Обратите внимание на ID
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_exit).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Вы уверены?")
                .setPositiveButton("Да") { _, _ ->
                    Toast.makeText(this, "Спасибо. До новых встреч", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
                .setNegativeButton("Нет", null)
                .show()
        }

        // --- НОВОЕ: Обработчик нажатия на иконку поиска ---
        findViewById<ImageView>(R.id.nav_search).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        // --- КОНЕЦ НОВОГО ---
    }

    private fun showProfileError() {
        AlertDialog.Builder(this)
            .setTitle("Профиль не настроен")
            .setMessage("Пожалуйста, завершите регистрацию.")
            .setPositiveButton("Перейти") { _, _ ->
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .show()
    }
}