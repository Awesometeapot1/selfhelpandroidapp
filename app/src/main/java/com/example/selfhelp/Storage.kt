package com.example.selfhelp

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun currentTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date())
}

fun currentDateKey(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date())
}

fun loadThemePreference(prefs: SharedPreferences): AppThemeOption {
    val saved = prefs.getString("selected_theme", AppThemeOption.SOFT_LAVENDER.name)
        ?: AppThemeOption.SOFT_LAVENDER.name

    return try {
        AppThemeOption.valueOf(saved)
    } catch (_: Exception) {
        AppThemeOption.SOFT_LAVENDER
    }
}

fun saveThemePreference(prefs: SharedPreferences, theme: AppThemeOption) {
    prefs.edit().putString("selected_theme", theme.name).apply()
}

fun loadMoodEntries(prefs: SharedPreferences): List<MoodEntry> {
    val raw = prefs.getString("mood_entries", "[]") ?: "[]"
    val jsonArray = JSONArray(raw)
    val result = mutableListOf<MoodEntry>()

    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        result.add(
            MoodEntry(
                mood = obj.getString("mood"),
                timestamp = obj.getString("timestamp"),
                note = obj.optString("note", "")
            )
        )
    }

    return result
}

fun saveMoodEntries(prefs: SharedPreferences, entries: List<MoodEntry>) {
    val jsonArray = JSONArray()
    entries.forEach { entry ->
        val obj = JSONObject()
        obj.put("mood", entry.mood)
        obj.put("timestamp", entry.timestamp)
        obj.put("note", entry.note)
        jsonArray.put(obj)
    }
    prefs.edit().putString("mood_entries", jsonArray.toString()).apply()
}

fun clearMoodEntries(prefs: SharedPreferences) {
    prefs.edit().putString("mood_entries", "[]").apply()
}

fun loadTechniqueEntries(prefs: SharedPreferences): List<TechniqueEntry> {
    val raw = prefs.getString("technique_entries", "[]") ?: "[]"
    val jsonArray = JSONArray(raw)
    val result = mutableListOf<TechniqueEntry>()

    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        result.add(
            TechniqueEntry(
                technique = obj.getString("technique"),
                timestamp = obj.getString("timestamp")
            )
        )
    }

    return result
}

fun saveTechniqueEntries(prefs: SharedPreferences, entries: List<TechniqueEntry>) {
    val jsonArray = JSONArray()
    entries.forEach { entry ->
        val obj = JSONObject()
        obj.put("technique", entry.technique)
        obj.put("timestamp", entry.timestamp)
        jsonArray.put(obj)
    }
    prefs.edit().putString("technique_entries", jsonArray.toString()).apply()
}

fun clearTechniqueEntries(prefs: SharedPreferences) {
    prefs.edit().putString("technique_entries", "[]").apply()
}

fun defaultDailyTasks(): List<DailyTask> {
    return listOf(
        DailyTask(id = "builtin_meds", title = "Take meds", time = "08:00", isBuiltIn = true),
        DailyTask(id = "builtin_water", title = "Drink water", time = "", isBuiltIn = true),
        DailyTask(id = "builtin_breakfast", title = "Eat breakfast", time = "", isBuiltIn = true),
        DailyTask(id = "builtin_lunch", title = "Eat lunch", time = "", isBuiltIn = true),
        DailyTask(id = "builtin_dinner", title = "Eat dinner", time = "", isBuiltIn = true),
        DailyTask(id = "builtin_shower", title = "Have a shower", time = "19:00", isBuiltIn = true),
        DailyTask(id = "builtin_teeth", title = "Brush teeth", time = "", isBuiltIn = true),
        DailyTask(id = "builtin_outside", title = "Go outside", time = "", isBuiltIn = true)
    )
}

fun loadDailyTasks(prefs: SharedPreferences): List<DailyTask> {
    val raw = prefs.getString("daily_tasks", null)

    if (raw == null) {
        val defaults = defaultDailyTasks()
        saveDailyTasks(prefs, defaults)
        return defaults
    }

    val jsonArray = JSONArray(raw)
    val result = mutableListOf<DailyTask>()

    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        result.add(
            DailyTask(
                id = obj.getString("id"),
                title = obj.getString("title"),
                time = obj.optString("time", ""),
                isBuiltIn = obj.optBoolean("isBuiltIn", false)
            )
        )
    }

    return result
}

fun saveDailyTasks(prefs: SharedPreferences, tasks: List<DailyTask>) {
    val jsonArray = JSONArray()
    tasks.forEach { task ->
        val obj = JSONObject()
        obj.put("id", task.id)
        obj.put("title", task.title)
        obj.put("time", task.time)
        obj.put("isBuiltIn", task.isBuiltIn)
        jsonArray.put(obj)
    }
    prefs.edit().putString("daily_tasks", jsonArray.toString()).apply()
}

fun createCustomTask(title: String, time: String): DailyTask {
    return DailyTask(
        id = UUID.randomUUID().toString(),
        title = title,
        time = time,
        isBuiltIn = false
    )
}

fun loadTodayTaskChecks(prefs: SharedPreferences): Set<String> {
    val key = "task_checks_${currentDateKey()}"
    return prefs.getStringSet(key, emptySet()) ?: emptySet()
}

fun saveTodayTaskChecks(prefs: SharedPreferences, checkedIds: Set<String>) {
    val key = "task_checks_${currentDateKey()}"
    prefs.edit().putStringSet(key, checkedIds).apply()
}

fun clearTodayTaskChecks(prefs: SharedPreferences) {
    val key = "task_checks_${currentDateKey()}"
    prefs.edit().remove(key).apply()
}