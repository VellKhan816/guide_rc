package com.example.my_story_rc

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.my_story_rc.ui.StoriesListFragment

class StoriesActivity : AppCompatActivity() {
    private var currentTab = 0 // Изменено: по умолчанию "Новинки" (tabIndex = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stories)

        // Инициализация первой вкладки как "Новинки"
        loadFragment(currentTab) // Загрузит вкладку с tabIndex = 0

        // Установка слушателей для переключения вкладок
        findViewById<LinearLayout>(R.id.tab_new).setOnClickListener { loadFragment(0) }      // Новинки
        findViewById<LinearLayout>(R.id.tab_for_you).setOnClickListener { loadFragment(1) }   // Для Вас
        findViewById<LinearLayout>(R.id.tab_popular).setOnClickListener { loadFragment(2) }  // Популярные
        findViewById<LinearLayout>(R.id.tab_started).setOnClickListener { loadFragment(3) }  // Начатые
        findViewById<LinearLayout>(R.id.tab_completed).setOnClickListener { loadFragment(4) } // Завершённые
        findViewById<LinearLayout>(R.id.tab_all).setOnClickListener { loadFragment(5) }       // Все
    }

    private fun loadFragment(tabIndex: Int) {
        // Не перезагружаем фрагмент, если он уже активен
        if (tabIndex == currentTab) return
        currentTab = tabIndex

        // Создаём фрагмент с аргументом типа вкладки
        val fragment: Fragment = StoriesListFragment.newInstance(tabIndex)

        // Заменяем содержимое контейнера фрагментом
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            // Здесь можно добавить анимацию, если нужно
            //setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
    }
}