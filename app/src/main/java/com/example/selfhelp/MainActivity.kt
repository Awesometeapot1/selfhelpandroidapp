package com.example.selfhelp

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.selfhelp.ui.theme.SelfhelpTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("selfhelp_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language", "en") ?: "en"
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SelfHelpApp()
        }
    }
}

// Theme state must live outside SelfhelpTheme so changing it re-wraps the whole UI
@Composable
fun SelfHelpApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("selfhelp_prefs", Context.MODE_PRIVATE) }
    var selectedTheme by remember { mutableStateOf(loadThemePreference(prefs)) }
    var largeText by remember { mutableStateOf(loadLargeText(prefs)) }
    var selectedLanguage by remember { mutableStateOf(loadLanguage(prefs)) }

    val baseDensity = LocalDensity.current
    val density = if (largeText)
        Density(baseDensity.density, fontScale = baseDensity.fontScale * 1.3f)
    else baseDensity

    CompositionLocalProvider(LocalDensity provides density) {
        SelfhelpTheme(theme = selectedTheme) {
            SelfHelpRoot(
                prefs = prefs,
                selectedTheme = selectedTheme,
                onThemeChange = { theme ->
                    selectedTheme = theme
                    saveThemePreference(prefs, theme)
                },
                largeText = largeText,
                onLargeTextChange = { value ->
                    largeText = value
                    saveLargeText(prefs, value)
                },
                selectedLanguage = selectedLanguage,
                onLanguageChange = { lang ->
                    selectedLanguage = lang
                    saveLanguage(prefs, lang)
                    (context as? android.app.Activity)?.recreate()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfHelpRoot(
    prefs: SharedPreferences,
    selectedTheme: AppThemeOption,
    onThemeChange: (AppThemeOption) -> Unit,
    largeText: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    var moodEntries by remember { mutableStateOf(loadMoodEntries(prefs)) }
    var noteEntries by remember { mutableStateOf(loadNoteEntries(prefs)) }
    var dailyTasks by remember { mutableStateOf(loadDailyTasks(prefs)) }
    var checkedTaskIds by remember { mutableStateOf(loadTodayTaskChecks(prefs)) }
    var techniqueEntries by remember { mutableStateOf(loadTechniqueEntries(prefs)) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openMenu: () -> Unit = { scope.launch { drawerState.open() } }

    val bottomNavItems = listOf(
        Triple(AppScreen.HOME, Icons.Default.Home, "Home"),
        Triple(AppScreen.STORY, Icons.Default.PlayArrow, "Journey"),
        Triple(AppScreen.DAILY_ROUTINE, Icons.Default.List, "Routine"),
        Triple(AppScreen.DASHBOARD, Icons.Default.Info, "Stats"),
        Triple(AppScreen.NOTES, Icons.Default.Edit, "Notes")
    )
    val drawerItems = bottomNavItems + listOf(
        Triple(AppScreen.BREATHING, Icons.Default.Favorite, "Breathing"),
        Triple(AppScreen.SETTINGS, Icons.Default.Settings, "Settings")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "SelfHelp",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                drawerItems.forEach { (screen, icon, label) ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = currentScreen == screen,
                        icon = { Icon(icon, contentDescription = null) },
                        onClick = {
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { (screen, icon, label) ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(
                        onNavigate = { currentScreen = it },
                        onOpenMenu = openMenu
                    )
                    AppScreen.STORY -> StoryScreen(
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    AppScreen.MOOD_TRACKER -> MoodTrackerScreen(
                        moodEntries = moodEntries,
                        onSaveMood = { mood, note ->
                            val newEntry = MoodEntry(mood, currentTimestamp(), note)
                            moodEntries = moodEntries + newEntry
                            saveMoodEntries(prefs, moodEntries)
                        },
                        onClearHistory = {
                            moodEntries = emptyList()
                            clearMoodEntries(prefs)
                        },
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    AppScreen.DAILY_ROUTINE -> DailyRoutineScreen(
                        tasks = dailyTasks,
                        checkedIds = checkedTaskIds,
                        onAddTask = { title, time ->
                            val newTask = createCustomTask(title, time)
                            dailyTasks = dailyTasks + newTask
                            saveDailyTasks(prefs, dailyTasks)
                        },
                        onToggleTask = { id ->
                            checkedTaskIds = if (id in checkedTaskIds) checkedTaskIds - id else checkedTaskIds + id
                            saveTodayTaskChecks(prefs, checkedTaskIds)
                        },
                        onEditTask = { id, title, time ->
                            dailyTasks = dailyTasks.map { if (it.id == id) it.copy(title = title, time = time) else it }
                            saveDailyTasks(prefs, dailyTasks)
                        },
                        onDeleteTask = { id ->
                            dailyTasks = dailyTasks.filter { it.id != id }
                            checkedTaskIds = checkedTaskIds - id
                            saveDailyTasks(prefs, dailyTasks)
                            saveTodayTaskChecks(prefs, checkedTaskIds)
                        },
                        onClearProgress = {
                            checkedTaskIds = emptySet()
                            clearTodayTaskChecks(prefs)
                        },
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    AppScreen.DASHBOARD -> DashboardScreen(
                        moodEntries = moodEntries,
                        techniqueEntries = techniqueEntries,
                        dailyTasks = dailyTasks,
                        checkedTaskIds = checkedTaskIds,
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    AppScreen.NOTES -> NotesScreen(
                        noteEntries = noteEntries,
                        onSaveNote = { text ->
                            val newEntry = NoteEntry(text, currentTimestamp())
                            noteEntries = noteEntries + newEntry
                            saveNoteEntries(prefs, noteEntries)
                        },
                        onClearNotes = {
                            noteEntries = emptyList()
                            clearNoteEntries(prefs)
                        },
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    AppScreen.BREATHING -> BreathingScreen(
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    AppScreen.SETTINGS -> SettingsScreen(
                        selectedTheme = selectedTheme,
                        onThemeChange = onThemeChange,
                        largeText = largeText,
                        onLargeTextChange = onLargeTextChange,
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = onLanguageChange,
                        onBack = { currentScreen = AppScreen.HOME },
                        onOpenMenu = openMenu
                    )
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Button(onClick = { currentScreen = AppScreen.HOME }) {
                                Text("Back to Home")
                            }
                        }
                    }
                }
            }
        }
    }
}
