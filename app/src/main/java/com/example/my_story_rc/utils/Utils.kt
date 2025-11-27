package com.example.my_story_rc.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Проверяет, является ли строка корректной датой в формате "dd.MM.yyyy".
 * Дата должна:
 * - иметь правильный формат (10 символов, точки на 3-й и 6-й позициях),
 * - содержать числовые значения,
 * - быть реальной (например, не 31.04 или 29.02.2023),
 * - не быть из будущего.
 */
fun String.isValidDate(): Boolean {
    if (length != 10 || this[2] != '.' || this[5] != '.') return false
    return try {
        val parts = split(".")
        if (parts.size != 3) return false
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        // Создаем календарь для проверки даты
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Месяцы в Calendar начинаются с 0
        calendar.set(Calendar.DAY_OF_MONTH, day)

        // Устанавливаем время в 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Получаем текущую дату без времени
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Проверяем, что дата не в будущем
        val date = calendar.time
        date <= today.time && date == calendar.time // Проверка на корректность (например, 31.04.2023)
    } catch (e: NumberFormatException) {
        false
    } catch (e: IllegalArgumentException) {
        false // Например, 32.01.2000
    }
}

/**
 * Преобразует строку вида "dd.MM.yyyy" в Date.
 * Возвращает null, если строка некорректна.
 */
fun parseDate(dateString: String): Date? {
    if (!dateString.isValidDate()) return null
    return try {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        sdf.parse(dateString)
    } catch (e: Exception) {
        null
    }
}

/**
 * Рассчитывает возраст на основе даты рождения в формате Date.
 * Возвращает -1, если дата некорректна.
 */
fun calculateAge(dateOfBirth: Date): Int {
    val birthDate = dateOfBirth
    val today = Calendar.getInstance()

    val birthCalendar = Calendar.getInstance()
    birthCalendar.time = birthDate

    var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
    val monthDiff = today.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)
    val dayDiff = today.get(Calendar.DAY_OF_MONTH) - birthCalendar.get(Calendar.DAY_OF_MONTH)

    if (monthDiff < 0 || (monthDiff == 0 && dayDiff < 0)) {
        age--
    }

    return age
}