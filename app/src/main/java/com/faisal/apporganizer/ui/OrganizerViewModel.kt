package com.faisal.apporganizer.ui

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faisal.apporganizer.data.AppItem
import com.faisal.apporganizer.data.AppRepository
import com.faisal.apporganizer.data.Category
import com.faisal.apporganizer.data.Classifier
import com.faisal.apporganizer.data.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

enum class SortMode(val title: String) {
    NAME("أبجديًا"),
    NEWEST("الأحدث تثبيتًا"),
    MOST_USED("الأكثر استخدامًا"),
}

class OrganizerViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = Prefs(application)
    private val repository = AppRepository(application)
    private val collator = Collator.getInstance(Locale.forLanguageTag("ar"))
    private var loadJob: Job? = null

    var apps by mutableStateOf<List<AppItem>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var favorites by mutableStateOf(prefs.favorites())
        private set
    var query by mutableStateOf("")
    var selected by mutableStateOf<Category?>(null)
    var sort by mutableStateOf(SortMode.NAME)

    /** الفئات غير الفارغة مرتبة حسب عدد التطبيقات. */
    val groups: List<Pair<Category, List<AppItem>>> by derivedStateOf {
        apps.groupBy { it.category }
            .mapValues { (_, list) -> list.sortedWith(compareBy(collator) { it.label }) }
            .toList()
            .sortedWith(
                compareByDescending<Pair<Category, List<AppItem>>> { it.second.size }
                    .thenBy { it.first.ordinal },
            )
    }

    val favoriteApps: List<AppItem> by derivedStateOf {
        apps.filter { it.packageName in favorites }.sortedWith(compareBy(collator) { it.label })
    }

    val mostUsed: List<AppItem> by derivedStateOf {
        apps.filter { it.launchCount > 0 }
            .sortedWith(compareByDescending<AppItem> { it.launchCount }.thenByDescending { it.lastLaunch })
            .take(10)
    }

    val recentlyInstalled: List<AppItem> by derivedStateOf {
        val weekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        apps.filter { it.installTime > weekAgo }.sortedByDescending { it.installTime }.take(10)
    }

    val searchResults: List<AppItem> by derivedStateOf {
        val q = Classifier.normalize(query)
        if (q.isEmpty()) {
            emptyList()
        } else {
            apps.filter {
                Classifier.normalize(it.label).contains(q) ||
                    Classifier.normalize(it.category.title).contains(q) ||
                    it.packageName.contains(q)
            }.sortedWith(compareBy(collator) { it.label })
        }
    }

    fun appsIn(category: Category): List<AppItem> {
        val list = apps.filter { it.category == category }
        return when (sort) {
            SortMode.NAME -> list.sortedWith(compareBy(collator) { it.label })
            SortMode.NEWEST -> list.sortedByDescending { it.installTime }
            SortMode.MOST_USED -> list.sortedWith(
                compareByDescending<AppItem> { it.launchCount }.thenBy(collator) { it.label },
            )
        }
    }

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            apps = withContext(Dispatchers.Default) { repository.load(prefs) }
            loading = false
        }
    }

    fun isFavorite(pkg: String) = pkg in favorites

    fun toggleFavorite(pkg: String) {
        favorites = if (pkg in favorites) favorites - pkg else favorites + pkg
        prefs.setFavorites(favorites)
    }

    /** نقل تطبيق إلى فئة؛ القيمة null تعيده إلى تصنيفه التلقائي. */
    fun moveTo(pkg: String, category: Category?) {
        prefs.setCategoryOverride(pkg, category)
        updateApp(pkg) { it.copy(category = category ?: it.autoCategory) }
    }

    fun recordLaunch(pkg: String) {
        val now = System.currentTimeMillis()
        prefs.recordLaunch(pkg, now)
        updateApp(pkg) { it.copy(launchCount = it.launchCount + 1, lastLaunch = now) }
    }

    private fun updateApp(pkg: String, transform: (AppItem) -> AppItem) {
        apps = apps.map { if (it.packageName == pkg) transform(it) else it }
    }
}
