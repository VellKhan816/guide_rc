// model/Story.kt
package com.example.my_story_rc.model

import java.io.Serializable

/**
 * Модель интерактивной истории.
 *
 * @property id Уникальный идентификатор истории.
 * @property title Название истории.
 * @property description Краткое описание сюжета.
 * @property characters Список главных персонажей.
 * @property isCompleted Флаг завершённости (пройдена ли история).
 * @property coverResId Идентификатор ресурса обложки (R.drawable.xxx).
 * @property is18Plus Флаг возрастного ограничения (true = 18+ контент).
 * @property lastReadChapter Номер последней прочитанной главы (0, если не начата).
 */
data class Story(
    val id: Int,
    val title: String,
    val description: String,
    val characters: List<String>,
    val isCompleted: Boolean,
    val coverResId: Int,
    val is18Plus: Boolean = false,
    var lastReadChapter: Int = 0 // <-- НОВОЕ: Поле для отслеживания прогресса
) : Serializable