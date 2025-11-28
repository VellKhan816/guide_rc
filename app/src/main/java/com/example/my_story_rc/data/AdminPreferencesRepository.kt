package com.example.my_story_rc.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.my_story_rc.model.AdminUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

// Определяем DataStore для администратора
private val Context.adminDataStore: DataStore<Preferences> by preferencesDataStore(name = "admin_prefs")

class AdminPreferencesRepository(context: Context) {
    private val dataStore = context.adminDataStore // <-- Используем adminDataStore

    // --- Flow для статуса администратора ---
    val isAdminLoggedIn: Flow<Boolean> = dataStore.data // <-- Используем dataStore от класса
        .map { preferences -> preferences[AdminPreferencesKeys.IS_LOGGED_IN] ?: false }

    // --- Методы для работы с данными администратора ---

    /**
     * Проверяет введённые учётные данные администратора.
     * Сравнивает с жёстко закодированными значениями: login = "envell16", password = "envell0810".
     */
    suspend fun checkAdminCredentials(inputLogin: String, inputPassword: String): Boolean {
        val correctLogin = "envell16"
        val correctPassword = "envell0810"

        // Хэшируем введённые данные
        val hashedInputPassword = hashPassword(inputPassword)

        // Хэшируем правильные данные для сравнения
        val correctPasswordHash = hashPassword(correctPassword)

        // Проверяем, совпадают ли введённые данные с правильными
        return inputLogin == correctLogin && hashedInputPassword == correctPasswordHash
    }

    suspend fun setAdminLoggedIn(status: Boolean) {
        dataStore.edit { preferences -> // <-- Используем dataStore от класса
            preferences[AdminPreferencesKeys.IS_LOGGED_IN] = status
        }
    }

    // Опционально: метод для выхода из аккаунта администратора
    suspend fun clearAdminCredentials() {
        dataStore.edit { preferences -> // <-- Используем dataStore от класса
            preferences.remove(AdminPreferencesKeys.IS_LOGGED_IN) // <-- Используем AdminPreferencesKeys
        }
    }

    // --- Вспомогательная функция для хэширования пароля ---
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private object AdminPreferencesKeys { // <-- Правильное определение объекта
        val IS_LOGGED_IN = booleanPreferencesKey("is_admin_logged_in")
        // УБРАНО: ADMIN_LOGIN, ADMIN_PASSWORD_HASH, так как не используем сохранение
        // val ADMIN_LOGIN = stringPreferencesKey("admin_login") // <-- Было удалено
        // val ADMIN_PASSWORD_HASH = stringPreferencesKey("admin_password_hash") // <-- Было удалено
    }
}