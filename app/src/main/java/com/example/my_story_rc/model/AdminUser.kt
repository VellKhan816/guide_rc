package com.example.my_story_rc.model

/**
 * Простая модель данных для администратора.
 * В реальном приложении это будет связано с серверной аутентификацией.
 */
data class AdminUser(
    val login: String,
    val passwordHash: String // Храните хеш, а не сам пароль!
)