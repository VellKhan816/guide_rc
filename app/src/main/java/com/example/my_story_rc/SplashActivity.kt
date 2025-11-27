package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private var progress = 0
    private lateinit var userRepo: UserPreferencesRepository // Добавлен репозиторий

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        userRepo = UserPreferencesRepository(this) // Инициализация репозитория

        // Инициализация UI
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Запуск прогресса
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                if (progress <= 100) {
                    progressBar.progress = progress
                    progress++
                    handler?.postDelayed(this, 15) // ~1.5 секунды
                } else {
                    // Проверяем учётные данные ПОСЛЕ завершения анимации
                    checkCredentialsAndNavigate()
                }
            }
        })
    }

    // НОВОЕ: Метод для проверки учётных данных и навигации
    private fun checkCredentialsAndNavigate() {
        lifecycleScope.launch {
            val isSet = userRepo.isCredentialsSet.first() // Получаем текущее значение
            if (isSet) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            }
            finish() // Закрываем SplashActivity
        }
    }

    override fun onDestroy() {
        // Предотвращаем утечку памяти
        handler?.removeCallbacksAndMessages(null)
        handler = null
        super.onDestroy()
    }
}