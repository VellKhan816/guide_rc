package com.example.my_story_rc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var repo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        repo = UserPreferencesRepository(applicationContext)

        val switchDark = findViewById<Switch>(R.id.switchDarkMode)
        val switchNotif = findViewById<Switch>(R.id.switchNotifications)
        val btnAccount = findViewById<Button>(R.id.btnAccountAction)

        // Загрузка текущих настроек
        lifecycleScope.launch {
            repo.isDarkModeEnabled.collect { enabled ->
                switchDark.isChecked = enabled
                updateTheme(enabled)
            }
        }
        lifecycleScope.launch {
            repo.isNotificationsEnabled.collect { enabled ->
                switchNotif.isChecked = enabled
            }
        }

        // Переключение темы
        switchDark.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                repo.saveDarkMode(isChecked)
                updateTheme(isChecked)
            }
        }

        // Уведомления
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                repo.saveNotifications(isChecked)
                // Здесь можно интегрировать Firebase или системные уведомления
            }
        }

        // Кнопка входа/выхода
        btnAccount.setOnClickListener {
            lifecycleScope.launch {
                val isLoggedIn = repo.isProfileComplete.first() // Используем isProfileComplete
                if (isLoggedIn) {
                    // Выход: очищаем данные
                    repo.logout()
                    btnAccount.text = "Войти с учётной записью"
                    Toast.makeText(this@SettingsActivity, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()

                    // Направляем пользователя на WelcomeActivity через перезапуск MainActivity
                    val intent = Intent(this@SettingsActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish() // Закрываем SettingsActivity

                } else {
                    // Вход (в данном случае "регистрация"): направляем на WelcomeActivity
                    repo.login() // login() пустой, но вызываем для совместимости, если используется где-то ещё
                    startActivity(Intent(this@SettingsActivity, WelcomeActivity::class.java))
                    finish() // Закрываем SettingsActivity
                }
            }
        }
    }

    private fun updateTheme(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}