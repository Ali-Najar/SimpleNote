// app/src/main/java/com/example/simplenote/MainActivity.kt
package com.example.simplenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.simplenote.ui.AppNav

// app/src/main/java/com/example/simplenote/MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.simplenote.data.remote.ApiClient.init(applicationContext) // ✅
        setContent { MaterialTheme { Surface { com.example.simplenote.ui.AppNav() } } }
    }
}

