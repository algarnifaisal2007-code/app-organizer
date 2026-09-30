package com.faisal.apporganizer.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppItem(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap,
    val category: Category,
    val autoCategory: Category,
    val installTime: Long,
    val launchCount: Int,
    val lastLaunch: Long,
)

/** يقرأ التطبيقات المثبتة القابلة للتشغيل ويصنّفها. */
class AppRepository(private val context: Context) {
    private val pm: PackageManager = context.packageManager
    private val iconCache = HashMap<String, Pair<Long, ImageBitmap>>()

    @Synchronized
    fun load(prefs: Prefs): List<AppItem> {
        val seen = HashSet<String>()
        val result = ArrayList<AppItem>()

        for (ri in queryLauncherActivities()) {
            val appInfo = ri.activityInfo.applicationInfo
            val pkg = appInfo.packageName
            if (pkg == context.packageName || !seen.add(pkg)) continue

            val label = ri.loadLabel(pm).toString().trim().ifEmpty { pkg }
            val pkgInfo = packageInfo(pkg)
            val updated = pkgInfo?.lastUpdateTime ?: 0L
            val icon = iconCache[pkg]?.takeIf { it.first == updated }?.second
                ?: ri.loadIcon(pm).toBitmap(ICON_PX, ICON_PX).asImageBitmap()
                    .also { iconCache[pkg] = updated to it }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 &&
                (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
            val auto = Classifier.classify(pkg, label, appInfo.category, isSystem)

            result += AppItem(
                packageName = pkg,
                label = label,
                icon = icon,
                category = prefs.categoryOverride(pkg) ?: auto,
                autoCategory = auto,
                installTime = pkgInfo?.firstInstallTime ?: 0L,
                launchCount = prefs.launchCount(pkg),
                lastLaunch = prefs.lastLaunch(pkg),
            )
        }
        iconCache.keys.retainAll(seen)
        return result
    }

    private fun queryLauncherActivities(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }
    }

    private fun packageInfo(pkg: String): PackageInfo? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(pkg, 0)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    private companion object {
        const val ICON_PX = 144
    }
}
