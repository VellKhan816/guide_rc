package com.example.my_story_rc.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_story_rc.R
import com.example.my_story_rc.StoryDetailActivity
import com.example.my_story_rc.data.UserPreferencesRepository
import com.example.my_story_rc.domain.StoryRepository
import com.example.my_story_rc.model.Story
import com.example.my_story_rc.utils.calculateAge
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoriesListFragment : Fragment() {

    private var tabType: Int = 5 // по умолчанию "Все"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt("tabType")?.let { tabType = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stories_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewInTab)
        // Устанавливаем GridLayoutManager с 2 колонками для отображения 2xN
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val allStories = StoryRepository.allStories
        // val filteredStories = filterStoriesByTab(allStories, tabType) // <-- УБРАНО: теперь фильтрация внутри collect

        val userRepo = UserPreferencesRepository(requireContext())

        lifecycleScope.launch {
            userRepo.dateOfBirth.collect { dob ->
                val userAge = dob?.let { calculateAge(it) } ?: -1

                // --- ИСПРАВЛЕНО: Фильтрация происходит после получения прогресса ---
                val storiesToFilter = when (tabType) {
                    0 -> allStories.takeLast(10) // Новинки
                    1 -> allStories.filter { story ->
                        arrayOf(
                            "Любовь", "Секрет", "Грех", "Кали", "Дракула", "Бюро", "Песнь",
                            "Пси", "Легенда", "Ярость", "7 братьев", "Бездушная", "Te amo",
                            "Морок", "Я охочусь на тебя", "В ритме страсти", "Идеал", "Эдем"
                        ).any { keyword -> story.title.contains(keyword, ignoreCase = true) }
                    }
                    2 -> allStories.take(8) // Популярные
                    // 3 -> all.filter { it.lastReadChapter > 0 } // НЕПРАВИЛЬНО
                    3 -> userRepo.getStartedStories().first() // <-- ИСПРАВЛЕНО: Получаем начатые из репозитория
                    // 4 -> all.filter { it.isCompleted } // НЕПРАВИЛЬНО (для отслеживания прогресса)
                    4 -> userRepo.getCompletedStories().first() // <-- ИСПРАВЛЕНО: Получаем завершённые из репозитория
                    else -> allStories // Все
                }

                // Фильтрация по возрасту
                val allowedStories = if (userAge >= 18) storiesToFilter else storiesToFilter.filter { !it.is18Plus }

                // Создаём адаптер с фильтрованным списком
                val adapter = StoryAdapter(allowedStories) { story ->
                    if (story.is18Plus && userAge < 18) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Ограничение по возрасту")
                            .setMessage("Эта история доступна только пользователям старше 18 лет.")
                            .setPositiveButton("Понятно", null)
                            .show()
                        return@StoryAdapter // Возвращаемся из лямбды, не открываем историю
                    }

                    // Если возраст подходит, открываем StoryDetailActivity
                    val intent = Intent(requireContext(), StoryDetailActivity::class.java).apply {
                        putExtra("story_id", story.id) // Передаём ID истории
                    }
                    startActivity(intent)
                }
                // Устанавливаем адаптер в RecyclerView
                recyclerView.adapter = adapter
            }
        }
    }

    // --- УБРАНО: filterStoriesByTab теперь не используется в этом месте ---
    // private fun filterStoriesByTab(all: List<Story>, tabIndex: Int): List<Story> {
    //     return when (tabIndex) {
    //         0 -> all.takeLast(10) // Новинки
    //         1 -> all.filter { story ->
    //             arrayOf(
    //                 "Любовь", "Секрет", "Грех", "Кали", "Дракула", "Бюро", "Песнь",
    //                 "Пси", "Легенда", "Ярость", "7 братьев", "Бездушная", "Te amo",
    //                 "Морок", "Я охочусь на тебя", "В ритме страсти", "Идеал", "Эдем"
    //             ).any { keyword -> story.title.contains(keyword, ignoreCase = true) }
    //         }
    //         2 -> all.take(8) // Популярные
    //         3 -> all.filter { it.lastReadChapter > 0 } // НЕПРАВИЛЬНО: lastReadChapter не существует в модели Story
    //         4 -> all.filter { it.isCompleted } // НЕПРАВИЛЬНО: isCompleted из модели, не из прогресса
    //         else -> all // Все
    //     }
    // }
    // --- КОНЕЦ УДАЛЕНИЯ ---


    companion object {
        fun newInstance(tabType: Int): StoriesListFragment {
            return StoriesListFragment().apply {
                arguments = Bundle().apply { putInt("tabType", tabType) }
            }
        }
    }
}