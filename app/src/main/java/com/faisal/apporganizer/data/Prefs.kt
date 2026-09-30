package com.faisal.apporganizer.data

import android.content.Context

/** تخزين تفضيلات المستخدم: المفضلة، الفئات المخصصة، وعدد مرات الفتح. */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("organizer", Context.MODE_PRIVATE)

    fun favorites(): Set<String> = sp.getStringSet(KEY_FAVORITES, emptySet())?.toSet() ?: emptySet()

    fun setFavorites(favorites: Set<String>) {
        sp.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    fun categoryOverride(pkg: String): Category? =
        sp.getString("cat_$pkg", null)?.let { name -> Category.entries.firstOrNull { it.name == name } }

    fun setCategoryOverride(pkg: String, category: Category?) {
        sp.edit().apply {
            if (category == null) remove("cat_$pkg") else putString("cat_$pkg", category.name)
        }.apply()
    }

    fun launchCount(pkg: String): Int = sp.getInt("cnt_$pkg", 0)

    fun lastLaunch(pkg: String): Long = sp.getLong("last_$pkg", 0L)

    fun recordLaunch(pkg: String, time: Long) {
        sp.edit()
            .putInt("cnt_$pkg", launchCount(pkg) + 1)
            .putLong("last_$pkg", time)
            .apply()
    }

    private companion object {
        const val KEY_FAVORITES = "favorites"
    }
}
