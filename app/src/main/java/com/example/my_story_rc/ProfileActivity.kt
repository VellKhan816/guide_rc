package com.example.my_story_rc

import android.app.Dialog
import android.os.Bundle
import android.view.View // Импортируем View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.ui.AvatarPreviewAdapter
import com.example.my_story_rc.ui.RecentStoriesAdapter // Импортируем адаптер
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var repo: UserPreferencesRepository
    private lateinit var rvRecentStories: RecyclerView
    private lateinit var ivAvatar: ImageView // Для доступа к аватару из диалога

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        repo = UserPreferencesRepository(applicationContext)
        rvRecentStories = findViewById(R.id.rvRecentStories)
        ivAvatar = findViewById(R.id.ivAvatar) // Находим ImageView аватара

        setupUI()
        observeData()
    }

    private fun setupUI() {
        // --- НОВОЕ: Обработка навигации "назад" ---
        findViewById<View>(R.id.backButton).setOnClickListener {
            finish() // Закрывает текущую активность и возвращает на предыдущую
        }
        // --- КОНЕЦ НОВОГО ---

        // Обработка редактирования никнейма
        findViewById<Button>(R.id.btnEditNickname).setOnClickListener {
            val current = findViewById<TextView>(R.id.tvNickname).text.toString()
            val editText = androidx.appcompat.widget.AppCompatEditText(this).apply {
                setText(current)
                setSingleLine()
            }

            AlertDialog.Builder(this)
                .setTitle("Изменить никнейм")
                .setView(editText)
                .setPositiveButton("Сохранить") { _, _ ->
                    val newNick = editText.text.toString().trim()
                    if (newNick.isNotEmpty()) {
                        lifecycleScope.launch {
                            repo.saveNickname(newNick)
                        }
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        // Обработка редактирования "причины"
        findViewById<ImageView>(R.id.ivEditReason).setOnClickListener {
            val current = findViewById<TextView>(R.id.tvReason).text.toString()
            val editText = androidx.appcompat.widget.AppCompatEditText(this).apply {
                setText(current)
                setSingleLine()
            }

            AlertDialog.Builder(this)
                .setTitle("Почему вы играете в Клуб Романтики?")
                .setView(editText)
                .setPositiveButton("Сохранить") { _, _ ->
                    val newText = editText.text.toString().trim()
                    lifecycleScope.launch {
                        repo.saveReasonText(if (newText.isEmpty()) "..." else newText)
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        // Подсказка по UID
        findViewById<TextView>(R.id.tvUIDHint).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Как найти свой UID?")
                .setMessage("Ваш UID — это уникальный идентификатор, присвоенный при регистрации. Он отображается здесь и не может быть изменён.")
                .setPositiveButton("Понятно", null)
                .show()
        }

        // Аватар — выбор из предложенных (обновлённый список) с предпросмотром
        ivAvatar.setOnClickListener {
            showAvatarSelectionDialog()
        }

        // Настройка RecyclerView для последних историй
        rvRecentStories.layoutManager = LinearLayoutManager(this)
        rvRecentStories.adapter = RecentStoriesAdapter(emptyList())
    }

    // --- НОВОЕ: Метод для отображения диалога с предпросмотром аватаров ---
    private fun showAvatarSelectionDialog() {
        val avatars = listOf(
            R.drawable.adi_av,
            R.drawable.adi2_av,
            R.drawable.astaror_av,
            R.drawable.banda_av,
            R.drawable.david_av,
            R.drawable.david2_av,
            R.drawable.egor_borya_ac,
            R.drawable.eliza_av,
            R.drawable.endy_av,
            R.drawable.eragon_av,
            R.drawable.homy_av,
            R.drawable.kain_av,
            R.drawable.kristofer_av,
            R.drawable.lily_av,
            R.drawable.lubash_eva_av,
            R.drawable.luci_av,
            R.drawable.lusifer_av,
            R.drawable.malbonte_av,
            R.drawable.mamon_av,
            R.drawable.mimi_av,
            R.drawable.mimi2_av,
            R.drawable.nebiros_av,
            R.drawable.noa_av,
            R.drawable.plague_av,
            R.drawable.rusich_av,
            R.drawable.sami_av,
            R.drawable.semi2_av,
            R.drawable.war_av,
            R.drawable.yo_av
        )

        // Создаём диалог
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_avatar_selection) // Создайте этот макет
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            (resources.displayMetrics.heightPixels * 0.7).toInt()
        )

        // Находим элементы в диалоге
        val recyclerViewAvatars = dialog.findViewById<RecyclerView>(R.id.recyclerViewAvatars)
        val btnSave = dialog.findViewById<Button>(R.id.btnSaveAvatar)

        // --- ИСПРАВЛЕНО: Получаем текущий выбранный аватар через lifecycleScope.launch ---
        // Запускаем корутину для получения значения из Flow
        lifecycleScope.launch {
            val currentAvatarResId = repo.selectedAvatarId.first() // Получаем текущее значение

            // Создаём адаптер ВНУТРИ корутины, чтобы использовать полученное значение
            val adapter = AvatarPreviewAdapter(avatars, onAvatarSelected = { selectedResId ->
                // Обновляем аватар в ProfileActivity временно
                runOnUiThread { // Обновляем UI в основном потоке
                    ivAvatar.setImageResource(selectedResId)
                }
            }, initiallySelectedAvatarResId = currentAvatarResId)

            // Настраиваем RecyclerView
            recyclerViewAvatars.layoutManager = GridLayoutManager(this@showAvatarSelectionDialog, 4) // 4 колонки
            recyclerViewAvatars.adapter = adapter

            // Обработка кнопки "Сохранить"
            btnSave.setOnClickListener {
                val finalSelectedResId = adapter.getSelectedAvatarResId()
                lifecycleScope.launch {
                    repo.saveSelectedAvatar(finalSelectedResId)
                    Toast.makeText(this@showAvatarSelectionDialog, "Аватар изменён", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }
        // Диалог должен быть показан до того, как RecyclerView будет настроен,
        // иначе пользователь может увидеть пустой список.
        // Лучше запустить корутину и показать диалог, а настройку адаптера выполнить асинхронно.
        // Но для простоты, покажем диалог сразу, а корутина настроит его асинхронно.
        // Альтернатива: Использовать StateFlow/LiveData или передавать начальное значение в адаптер через конструктор.
        // Пока оставим как есть, но помним про потенциальную задержку при открытии.
        dialog.show()
    }
    // --- КОНЕЦ НОВОГО ---


    private fun observeData() {
        lifecycleScope.launch {
            repo.nickname.collect { nick ->
                findViewById<TextView>(R.id.tvNickname).text = nick.ifEmpty { "Гость" }
            }
        }

        lifecycleScope.launch {
            repo.reasonText.collect { reason ->
                findViewById<TextView>(R.id.tvReason).text = reason.ifEmpty { "..." }
            }
        }

        lifecycleScope.launch {
            repo.selectedAvatarId.collect { resId ->
                ivAvatar.setImageResource(resId) // Обновляем аватар, когда он изменяется в DataStore
            }
        }

        // --- ОБНОВЛЕНО: Наблюдение за начатыми и завершёнными историями ---
        lifecycleScope.launch {
            repo.getStartedStories().collect { startedStories ->
                findViewById<TextView>(R.id.tvStarted).text = startedStories.size.toString() // Обновляем счётчик начатых
                // Здесь можно обновить RecyclerView с начатыми историями, если нужно отдельно их показывать
            }
        }

        lifecycleScope.launch {
            repo.getCompletedStories().collect { completedStories ->
                findViewById<TextView>(R.id.tvCompleted).text = completedStories.size.toString() // Обновляем счётчик завершённых
                // Здесь можно обновить RecyclerView с завершёнными историями, если нужно отдельно их показывать
            }
        }

        // UID (можно генерировать или брать из DataStore)
        val uid = "UID-${System.currentTimeMillis().toString().takeLast(6)}"
        findViewById<TextView>(R.id.tvUID).text = "Игровой UID: $uid"

        // Последние истории — наблюдение за последними 3 историями из общего списка (или из прогресса, если нужно)
        // Предположим, что "последние" - это последние прочитанные или добавленные в прогресс
        // Для простоты, покажем последние 3 из общего списка, но вы можете изменить логику
        lifecycleScope.launch {
            repo.getStartedStories().collect { startedStories ->
                // Сортируем по ID (или по времени последнего чтения, если хранится) и берем последние 3
                val recent = startedStories.sortedBy { it.id }.takeLast(3).map { it.title }
                // Находим адаптер, установленный в rvRecentStories
                val adapter = rvRecentStories.adapter as? RecentStoriesAdapter
                adapter?.updateStories(recent) // Передаём список названий или объектов Story, в зависимости от реализации адаптера
            }
        }
    }
}