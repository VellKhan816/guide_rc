package com.example.my_story_rc.model

import java.io.Serializable

/**
 * Модель данных для прогресса по истории.
 * @param storyId Уникальный идентификатор истории.
 * @param lastReadChapter Номер последней прочитанной главы.
 * @param isCompleted Статус завершения истории.
 * @param readTimeMs Общее время чтения (опционально).
 */

data class StoryProgress(
    val storyId: Int,
    val lastReadChapter: Int = 0,
    val isCompleted: Boolean = false,
    val readTimeMs: Long = 0L
) : Serializable