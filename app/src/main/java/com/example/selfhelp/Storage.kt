package com.example.selfhelp

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun currentTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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