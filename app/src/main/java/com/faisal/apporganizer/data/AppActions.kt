package com.faisal.apporganizer.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

/** إجراءات النظام على تطبيق: تشغيل، معلومات، إلغاء تثبيت. */
object AppActions {

    fun launch(context: Context, pkg: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(pkg)
        if (intent == null) {
            Toast.makeText(context, "تعذّر فتح التطبيق", Toast.LENGTH_SHORT).show()
            return false
        }
        return start(context, intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openInfo(context: Context, pkg: String) {
        start(context, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", pkg, null)))
    }

    fun uninstall(context: Context, pkg: String) {
        start(context, Intent(Intent.ACTION_DELETE, Uri.fromParts("package", pkg, null)))
    }

    private fun start(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "تعذّر تنفيذ الإجراء", Toast.LENGTH_SHORT).show()
        false
    }
}
