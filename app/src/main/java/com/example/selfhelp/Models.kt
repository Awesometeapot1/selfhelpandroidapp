package com.example.selfhelp

data class Choice(
    val text: String,
    val next: String
)

data class Scene(
    val text: String,
    val choices: List<Choice>
)

data class MoodEntry(
    val mood: String,
    val timestamp: String,
    val note: String = ""
)

data class TechniqueEntry(
    val technique: String,
    val timestamp: String
)

data class NoteEntry(
    val text: String,
    val timestamp: String
)

data class DailyTask(
    val id: String,
    val title: String,
    val time: String = "",
    val isBuiltIn: Boolean = false
)

enum class AppScreen {
    HOME,
    STORY,
    SETTINGS,
    MOOD_TRACKER,
    TECHNIQUE_LOG,
    DAILY_ROUTINE,
    DASHBOARD,
    BREATHING,
    NOTES
}

enum class AppThemeOption(val label: String) {
    SOFT_LAVENDER("Soft Lavender"),
    FOREST_CALM("Forest Calm"),
    DARK_NIGHT("Dark Night")
}

enum class AppLanguage(val label: String, val code: String) {
    ENGLISH("English",  "en"),
    FRENCH( "Français", "fr"),
    SPANISH("Español",  "es"),
    WELSH(  "Cymraeg",  "cy")
}