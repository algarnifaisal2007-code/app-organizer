package com.faisal.apporganizer

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.faisal.apporganizer.ui.OrganizerApp
import com.faisal.apporganizer.ui.OrganizerViewModel
import com.faisal.apporganizer.ui.theme.OrganizerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: OrganizerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            OrganizerTheme {
                OrganizerApp(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // تحديث القائمة عند العودة، لالتقاط التطبيقات المثبتة أو المحذوفة حديثًا
        viewModel.refresh()
    }
}
