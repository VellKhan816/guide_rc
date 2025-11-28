package com.example.my_story_rc.model

import java.io.Serializable

/**
 * Модель данных для главы истории.
 * @property id Уникальный идентификатор главы (например, storyId * 100 + chapterNumber).
 * @property storyId ID истории, к которой принадлежит глава.
 * @property number Номер главы в рамках истории.
 * @property title Название главы.
 * @property content Содержимое главы (например, строка с текстом или путь к файлу).
 */
data class Chapter(
    val id: Int,
    val storyId: Int,
    val number: Int,
    val title: String,
    val content: String // Можно заменить на путь к файлу, если тексты большие
) : Serializable