package com.faisal.apporganizer.data

import android.content.pm.ApplicationInfo

/** يحدد فئة التطبيق تلقائيًا من اسم الحزمة والاسم الظاهر وتصنيف النظام. */
object Classifier {

    fun classify(packageName: String, label: String, systemCategory: Int, isSystem: Boolean): Category {
        val text = "${packageName.lowercase()} ${label.lowercase()}"
        Category.entries.firstOrNull { cat -> cat.keywords.any { text.contains(it) } }?.let { return it }

        when (systemCategory) {
            ApplicationInfo.CATEGORY_GAME -> return Category.GAMES
            ApplicationInfo.CATEGORY_AUDIO -> return Category.MUSIC
            ApplicationInfo.CATEGORY_VIDEO -> return Category.VIDEO
            ApplicationInfo.CATEGORY_IMAGE -> return Category.PHOTO
            ApplicationInfo.CATEGORY_SOCIAL -> return Category.SOCIAL
            ApplicationInfo.CATEGORY_NEWS -> return Category.NEWS
            ApplicationInfo.CATEGORY_MAPS -> return Category.TRAVEL
            ApplicationInfo.CATEGORY_PRODUCTIVITY -> return Category.PRODUCTIVITY
            ApplicationInfo.CATEGORY_ACCESSIBILITY -> return Category.TOOLS
        }

        return if (isSystem) Category.SYSTEM else Category.OTHER
    }

    private val diacritics = Regex("[\\u064B-\\u0652\\u0640]")

    /** توحيد النص العربي للبحث: إزالة التشكيل وتوحيد الألف والتاء المربوطة والياء. */
    fun normalize(text: String): String = text.lowercase()
        .replace(diacritics, "")
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
        .replace('ة', 'ه').replace('ى', 'ي')
        .trim()
}
