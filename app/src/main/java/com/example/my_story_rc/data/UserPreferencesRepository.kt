package com.example.my_story_rc.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

// --- Модель для прогресса по истории ---
data class StoryProgress(
    val storyId: Int,
    val lastReadChapter: Int = 0,
    val isCompleted: Boolean = false,
    val readTimeMs: Long = 0L // Если нужно отслеживать время
)

class UserPreferencesRepository(context: Context) {
    private val dataStore = context.dataStore

    // --- Существующие Flow ---
    val nickname: Flow<String> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.NICKNAME] ?: "" }

    val dateOfBirth: Flow<Date?> = dataStore.data
        .map { preferences ->
            val dateInMillis = preferences[PreferencesKeys.DOB_MILLIS]
            dateInMillis?.let { Date(it) }
        }

    val password: Flow<String> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.PASSWORD] ?: "" }

    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.DARK_MODE] ?: false }

    val isNotificationsEnabled: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.NOTIFICATIONS] ?: true }

    // --- НОВЫЕ Flow для профиля ---
    val reasonText: Flow<String> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.REASON] ?: "" }

    val selectedAvatarId: Flow<Int> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.AVATAR] ?: 0 }

    // --- НОВОЕ: Статистика (предполагаем, что это количество начатых/завершённых историй) ---
    val statsStarted: Flow<Int> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.STATS_STARTED] ?: 0 }

    val statsCompleted: Flow<Int> = dataStore.data
        .map { preferences -> preferences[PreferencesKeys.STATS_COMPLETED] ?: 0 }


    /**
     * Проверяет, заполнены ли основные данные профиля (включая пароль).
     * Используется для определения необходимости показа WelcomeActivity.
     */
    val isProfileComplete: Flow<Boolean> = dataStore.data
        .map { preferences ->
            val nick = preferences[PreferencesKeys.NICKNAME].orEmpty()
            val dobMillis = preferences[PreferencesKeys.DOB_MILLIS]
            val pass = preferences[PreferencesKeys.PASSWORD].orEmpty() // НОВОЕ: проверяем пароль
            nick.isNotEmpty() && dobMillis != null && pass.isNotEmpty() // Добавлено условие на пароль
        }

    // --- НОВОЕ: Flow для проверки учётных данных ---
    val isCredentialsSet: Flow<Boolean> = dataStore.data
        .map { preferences ->
            val nick = preferences[PreferencesKeys.NICKNAME].orEmpty()
            val pass = preferences[PreferencesKeys.PASSWORD].orEmpty()
            nick.isNotEmpty() && pass.isNotEmpty() // Проверяем, что и ник, и пароль установлены
        }


    // --- НОВОЕ: Flow для прогресса по историям ---
    val storyProgress: Flow<Map<Int, StoryProgress>> = dataStore.data
        .map { preferences ->
            val progressMapJson = preferences[PreferencesKeys.STORY_PROGRESS_MAP_JSON] ?: "{}"
            // Здесь нужно реализовать десериализацию JSON в Map<Int, StoryProgress>
            // Используем простую реализацию через DataStore Preferences для ключей вида "story_progress_$id"
            val map = mutableMapOf<Int, StoryProgress>()
            preferences.asMap().forEach { (key, value) ->
                if (key.name.startsWith("story_progress_") && value is String) {
                    val idStr = key.name.substringAfter("story_progress_")
                    val id = idStr.toIntOrNull()
                    if (id != null) {
                        // Предположим, значение - это JSON для StoryProgress
                        // Для простоты, будем хранить отдельные ключи для lastReadChapter и isCompleted
                        // или использовать один JSON-ключ, как показано в комментарии ниже.
                        // Реализуем хранение в одном JSON-ключе для прогресса всех историй.
                        // Этот подход требует десериализации при каждом изменении.
                        // Альтернатива - использовать Room.
                        // Для текущего решения с DataStore будем использовать отдельные ключи.
                    }
                }
            }
            // Более реалистичный подход с DataStore - использовать отдельные ключи для каждого storyId
            // См. методы getStoryProgress и updateStoryProgress ниже.
            emptyMap() // Возвращаем пустую карту, так как полный список получаем через другие методы
        }

    // --- Существующие методы сохранения ---
    suspend fun saveProfileData(nickname: String, dateOfBirth: Date, password: String = "") { // Правильная сигнатура
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NICKNAME] = nickname
            preferences[PreferencesKeys.DOB_MILLIS] = dateOfBirth.time
            if (password.isNotEmpty()) { // Сохраняем только если передан
                preferences[PreferencesKeys.PASSWORD] = password // НЕБЕЗОПАСНО!
            }
        }
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun saveNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] = enabled
        }
    }

    // --- НОВЫЕ методы сохранения для профиля ---
    suspend fun saveNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NICKNAME] = nickname
        }
    }

    suspend fun saveReasonText(reason: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REASON] = reason
        }
    }

    suspend fun saveSelectedAvatar(avatarResId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AVATAR] = avatarResId
        }
    }

    // --- НОВОЕ: Методы для работы с прогрессом по истории ---
    suspend fun getStoryProgress(storyId: Int): StoryProgress {
        val lastChapter = dataStore.data.first()[PreferencesKeys.getStoryLastChapterKey(storyId)] ?: 0
        val isComp = dataStore.data.first()[PreferencesKeys.getStoryCompletedKey(storyId)] ?: false
        return StoryProgress(storyId, lastReadChapter = lastChapter, isCompleted = isComp)
    }

    suspend fun updateStoryProgress(storyProgress: StoryProgress) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.getStoryLastChapterKey(storyProgress.storyId)] = storyProgress.lastReadChapter
            preferences[PreferencesKeys.getStoryCompletedKey(storyProgress.storyId)] = storyProgress.isCompleted
        }
    }

    // --- НОВОЕ: Методы для получения списков начатых/завершённых историй ---
    // Эти методы фильтруют StoryRepository.allStories на основе прогресса, хранящегося в DataStore.
    fun getStartedStories(): Flow<List<Story>> {
        return dataStore.data.map { preferences ->
            StoryRepository.allStories.filter { story ->
                val lastChapter = preferences[PreferencesKeys.getStoryLastChapterKey(story.id)] ?: 0
                lastChapter > 0 // История считается начатой, если прочитана хотя бы одна глава
            }
        }
    }

    fun getCompletedStories(): Flow<List<Story>> {
        return dataStore.data.map { preferences ->
            StoryRepository.allStories.filter { story ->
                val isComp = preferences[PreferencesKeys.getStoryCompletedKey(story.id)] ?: false
                isComp // История считается завершённой, если установлен флаг isCompleted
            }
        }
    }


    // --- Вход/Выход ---
    suspend fun login() {
        // Ничего не делаем — данные уже есть
    }

    suspend fun logout() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private object PreferencesKeys {
        val NICKNAME = stringPreferencesKey("nickname")
        val DOB_MILLIS = longPreferencesKey("dob_millis") // Храним в миллисекундах
        val PASSWORD = stringPreferencesKey("password") // НОВОЕ (НЕБЕЗОПАСНО!)
        val REASON = stringPreferencesKey("reason") // <-- НОВОЕ
        val AVATAR = intPreferencesKey("avatar")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")

        // --- НОВОЕ: Ключи для статистики ---
        val STATS_STARTED = intPreferencesKey("stats_started")
        val STATS_COMPLETED = intPreferencesKey("stats_completed")

        // --- НОВОЕ: Ключи для прогресса по историям ---
        val STORY_PROGRESS_MAP_JSON = stringPreferencesKey("story_progress_map_json") // Не используем напрямую

        // Генерация ключей для конкретной истории
        fun getStoryLastChapterKey(storyId: Int) = intPreferencesKey("story_${storyId}_last_chapter")
        fun getStoryCompletedKey(storyId: Int) = booleanPreferencesKey("story_${storyId}_completed")
    }
}