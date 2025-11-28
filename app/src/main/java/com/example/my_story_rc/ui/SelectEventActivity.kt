package com.example.my_story_rc.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.R
import com.example.my_story_rc.data.AdminPreferencesRepository
import kotlinx.coroutines.launch

class SelectEventActivity : AppCompatActivity() {

    private lateinit var adminRepo: AdminPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_event)

        adminRepo = AdminPreferencesRepository(this)

        // Проверяем, вошёл ли админ (опционально, можно добавить проверку на всякий случай)
        lifecycleScope.launch {
            adminRepo.isAdminLoggedIn.collect { isLoggedIn ->
                if (!isLoggedIn) {
                    Toast.makeText(this@SelectEventActivity, "Доступ запрещён", Toast.LENGTH_SHORT).show()
                    finish()
                    return@collect
                }
                // Если вошёл, настраиваем UI
                setupEventSelection()
            }
        }
    }

    private fun setupEventSelection() {
        // Пример кнопок для разных ивентов
        findViewById<Button>(R.id.btnEvent1).setOnClickListener { sendNotification("Алмазная Лихорадка 72 часа") }
        findViewById<Button>(R.id.btnEvent2).setOnClickListener { sendNotification("Алмазная Лихорадка 48 часов") }
        findViewById<Button>(R.id.btnEvent3).setOnClickListener { sendNotification("Чапитие на 48 часов") }
        findViewById<Button>(R.id.btnEvent4).setOnClickListener { sendNotification("День Шоппинга 48 часов") }
        findViewById<Button>(R.id.btnEvent5).setOnClickListener { sendNotification("День Шоппинга 24 часа") }
        // ... другие кнопки ...
    }

    private fun sendNotification(message: String) {
        // --- ПРОСТАЯ РЕАЛИЗАЦИЯ: Отправка уведомления на устройстве администратора ---
        // Это *демонстрация*. В реальности вы бы использовали Firebase Cloud Messaging (FCM) или другую систему PUSH.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал уведомлений (для Android O и выше)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "admin_channel",
                "Административные уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = android.app.Notification.Builder(this, "admin_channel")
            .setContentTitle("Скоро: Ивент")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Используйте вашу иконку
            .setAutoCancel(true)

        notificationManager.notify(1001, builder.build())

        Toast.makeText(this, "Уведомление отправлено: $message", Toast.LENGTH_SHORT).show()

        // Альтернатива: Просто показать AlertDialog
        // AlertDialog.Builder(this)
        //     .setTitle("Уведомление отправлено")
        //     .setMessage(message)
        //     .setPositiveButton("OK", null)
        //     .show()
    }
}