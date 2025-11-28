package com.example.my_story_rc.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.R
import com.example.my_story_rc.data.AdminPreferencesRepository
import kotlinx.coroutines.launch

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var adminRepo: AdminPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        adminRepo = AdminPreferencesRepository(this)

        val etUsername = findViewById<EditText>(R.id.etAdminLogin)
        val etPassword = findViewById<EditText>(R.id.etAdminPassword)
        val btnLogin = findViewById<Button>(R.id.btnAdminLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (adminRepo.checkAdminCredentials(username, password)) {
                    adminRepo.setAdminLoggedIn(true)
                    Toast.makeText(this@AdminLoginActivity, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()
                    // Переход к следующему действию, например, к SelectEventActivity
                    startActivity(Intent(this@AdminLoginActivity, SelectEventActivity::class.java))
                    finish() // Закрываем AdminLoginActivity
                } else {
                    Toast.makeText(this@AdminLoginActivity, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}