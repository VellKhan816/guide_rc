package com.example.my_story_rc.utils

import com.example.my_story_rc.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first // ← ОБЯЗАТЕЛЬНЫЙ ИМПОРТ
import kotlinx.coroutines.runBlocking

object AgeRestrictionHelper {

    /**
     * Получает возраст пользователя на основе даты рождения из DataStore.
     * Блокирует поток (только для синхронного использования вне UI!).
     * В UI-слое лучше использовать Flow + lifecycleScope.
     */
    fun getUserAge(userRepo: UserPreferencesRepository): Int {
        return try {
            val dob = runBlocking { userRepo.dateOfBirth.first() }
            dob?.let(::calculateAge) ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Проверяет, разрешён ли доступ к 18+ контенту.
     */
    fun isAllowedFor18Plus(userRepo: UserPreferencesRepository): Boolean {
        return getUserAge(userRepo) >= 18
    }
}