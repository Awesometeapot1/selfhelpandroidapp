package com.example.selfhelp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SelfHelpRoot()
        }
    }
}

@Composable
fun SelfHelpRoot() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("selfhelp_prefs", Context.MODE_PRIVATE)
    }
    val story = remember { loadStoryFromAssets(context) }

    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var currentSceneId by rememberSaveable { mutableStateOf("start") }
    var lastMapSceneId by rememberSaveable { mutableStateOf("start") }

    var selectedTheme by rememberSaveable {
        mutableStateOf(loadThemePreference(prefs))
    }

    var moodEntries by remember { mutableStateOf(loadMoodEntries(prefs)) }
    var techniqueEntries by remember { mutableStateOf(loadTechniqueEntries(prefs)) }

    val colorScheme = when (selectedTheme) {
        AppThemeOption.SOFT_LAVENDER -> lightColorScheme(
            primary = Color(0xFF7E57C2),
            secondary = Color(0xFFB39DDB),
            tertiary = Color(0xFFD1C4E9),
            background = Color(0xFFF6F1FA),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF2E2140),
            onSurface = Color(0xFF2E2140)
        )

        AppThemeOption.FOREST_CALM -> lightColorScheme(
            primary = Color(0xFF4F7A5A),
            secondary = Color(0xFF8FB996),
            tertiary = Color(0xFFCFE3D1),
            background = Color(0xFFF4F8F3),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF223127),
            onSurface = Color(0xFF223127)
        )

        AppThemeOption.DARK_NIGHT -> darkColorScheme(
            primary = Color(0xFFB39DDB),
            secondary = Color(0xFF7E57C2),
            tertiary = Color(0xFF5E548E),
            background = Color(0xFF121218),
            surface = Color(0xFF1E1E28),
            onPrimary = Color.Black,
            onBackground = Color(0xFFF3EEFF),
            onSurface = Color(0xFFF3EEFF)
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    onStartJourney = {
                        currentSceneId = "start"
                        lastMapSceneId = "start"
                        currentScreen = AppScreen.STORY
                    },
                    onMoodTracker = { currentScreen = AppScreen.MOOD_TRACKER },
                    onTechniqueLog = { currentScreen = AppScreen.TECHNIQUE_LOG },
                    onSettings = { currentScreen = AppScreen.SETTINGS }
                )

                AppScreen.STORY -> StoryScreen(
                    story = story,
                    currentSceneId = currentSceneId,
                    lastMapSceneId = lastMapSceneId,
                    onSceneChange = { newSceneId -> currentSceneId = newSceneId },
                    onMapChange = { newMapSceneId -> lastMapSceneId = newMapSceneId },
                    onBackHome = { currentScreen = AppScreen.HOME },
                    onTechniqueTracked = { techniqueName ->
                        val updated = listOf(
                            TechniqueEntry(
                                technique = techniqueName,
                                timestamp = currentTimestamp()
                            )
                        ) + techniqueEntries

                        techniqueEntries = updated
                        saveTechniqueEntries(prefs, updated)
                    }
                )

                AppScreen.SETTINGS -> SettingsScreen(
                    selectedTheme = selectedTheme,
                    onThemeSelected = { newTheme ->
                        selectedTheme = newTheme
                        saveThemePreference(prefs, newTheme)
                    },
                    onBackHome = { currentScreen = AppScreen.HOME }
                )

                AppScreen.MOOD_TRACKER -> MoodTrackerScreen(
                    moodEntries = moodEntries,
                    onAddMood = { mood, note ->
                        val updated = listOf(
                            MoodEntry(
                                mood = mood,
                                timestamp = currentTimestamp(),
                                note = note
                            )
                        ) + moodEntries

                        moodEntries = updated
                        saveMoodEntries(prefs, updated)
                    },
                    onClearHistory = {
                        moodEntries = emptyList()
                        clearMoodEntries(prefs)
                    },
                    onBackHome = { currentScreen = AppScreen.HOME }
                )

                AppScreen.TECHNIQUE_LOG -> TechniqueLogScreen(
                    techniqueEntries = techniqueEntries,
                    onClearHistory = {
                        techniqueEntries = emptyList()
                        clearTechniqueEntries(prefs)
                    },
                    onBackHome = { currentScreen = AppScreen.HOME }

                )
            }
        }
    }
}