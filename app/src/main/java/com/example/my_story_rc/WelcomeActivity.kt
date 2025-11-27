package com.example.my_story_rc

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.my_story_rc.data.UserPreferencesRepository
import kotlinx.coroutines.launch
import java.util.*

class WelcomeActivity : AppCompatActivity() {
    private lateinit var nicknameInput: EditText
    private lateinit var passwordInput: EditText // НОВОЕ
    private lateinit var passwordConfirmInput: EditText // НОВОЕ
    private lateinit var nextBtn: Button
    private lateinit var subtitle: TextView
    private lateinit var userRepo: UserPreferencesRepository // Добавлен репозиторий

    private var nickname: String = ""
    private var birthDate: Calendar? = null
    private var password: String = "" // НОВОЕ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        nicknameInput = findViewById(R.id.nicknameInput)
        passwordInput = findViewById(R.id.passwordInput) // НОВОЕ
        passwordConfirmInput = findViewById(R.id.passwordConfirmInput) // НОВОЕ
        nextBtn = findViewById(R.id.nextBtn)
        subtitle = findViewById(R.id.subtitle)
        userRepo = UserPreferencesRepository(this) // Инициализация репозитория

        showNicknameStep()

        // Обновлённый обработчик нажатия на кнопку
        nextBtn.setOnClickListener {
            when {
                nickname.isEmpty() -> {
                    nickname = nicknameInput.text.toString().trim()
                    if (nickname.isEmpty()) {
                        Toast.makeText(this, "Пожалуйста, введите имя", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    showDatePicker()
                }
                birthDate == null -> {
                    showDatePicker() // Если дата не выбрана, снова показываем диалог
                }
                // Проверяем, введены ли и подтверждены пароли
                else -> {
                    if (!validatePasswords()) {
                        return@setOnClickListener // Если пароли не прошли проверку, выходим
                    }
                    // Все данные введены и проверены, сохраняем и переходим
                    saveUserDataAndNavigate()
                }
            }
        }
    }

    private fun showNicknameStep() {
        subtitle.text = "Как я могу к вам обращаться?"
        nicknameInput.setText("")
        nicknameInput.visibility = android.view.View.VISIBLE
        // Скрываем поля пароля
        passwordInput.visibility = android.view.View.GONE
        passwordConfirmInput.visibility = android.view.View.GONE
        nextBtn.text = "Далее"
        nickname = ""
        password = "" // Сбросим пароль при возврате к началу
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                // Проверка возраста (например, пользователь не может быть младше 13 или старше 130)
                val today = Calendar.getInstance()
                val age = today.get(Calendar.YEAR) - selectedYear
                if (age < 13 || age > 130) {
                    Toast.makeText(this, "Пожалуйста, введите корректную дату рождения", Toast.LENGTH_LONG).show()
                    showDatePicker() // Повторно показываем диалог
                    return@DatePickerDialog
                }
                birthDate = selectedCalendar
                subtitle.text = "Дата рождения: $selectedDay.${selectedMonth + 1}.$selectedYear"
                nicknameInput.visibility = android.view.View.GONE // Скрываем поле ввода имени
                // Показываем поля ввода пароля
                passwordInput.visibility = android.view.View.VISIBLE
                passwordConfirmInput.visibility = android.view.View.VISIBLE
                nextBtn.text = "Зарегистрироваться" // Меняем текст кнопки
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // НОВОЕ: Метод для проверки сложности пароля
    private fun isPasswordValid(password: String): Boolean {
        // Минимальная длина
        if (password.length < 8) {
            return false
        }

        var hasUpperCase = false
        var hasLowerCase = false
        var hasDigit = false
        var hasSpecialChar = false

        for (char in password) {
            when {
                char.isUpperCase() -> hasUpperCase = true
                char.isLowerCase() -> hasLowerCase = true
                char.isDigit() -> hasDigit = true
                // Проверяем, является ли символ специальным (не буквой и не цифрой)
                !char.isLetterOrDigit() -> hasSpecialChar = true
            }
        }

        // Все условия должны быть выполнены
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    // НОВОЕ: Метод для проверки паролей
    private fun validatePasswords(): Boolean {
        val pass = passwordInput.text.toString()
        val confirmPass = passwordConfirmInput.text.toString()

        if (pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите и подтвердите пароль", Toast.LENGTH_SHORT).show()
            return false
        }

        if (pass != confirmPass) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return false
        }

        // Проверка сложности пароля
        if (!isPasswordValid(pass)) {
            Toast.makeText(this, "Пароль должен содержать не менее 8 символов, включая заглавные, строчные буквы, цифры и специальный символ.", Toast.LENGTH_LONG).show()
            return false
        }

        password = pass
        return true
    }

    private fun saveUserDataAndNavigate() {
        val nick = nicknameInput.text.toString().trim()
        val dobCalendar = birthDate // <-- Тип: Calendar?

        if (nick.isEmpty() || dobCalendar == null || password.isEmpty()) {
            Toast.makeText(this, "Данные не заполнены", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Создаём объект Date из Calendar
                val dateOfBirthAsDate = Date(dobCalendar.timeInMillis)
                // Вызываем saveProfileData, передавая String, Date и String
                userRepo.saveProfileData(nick, dateOfBirthAsDate, password)
                Toast.makeText(this@WelcomeActivity, "Данные сохранены! Добро пожаловать!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@WelcomeActivity, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}