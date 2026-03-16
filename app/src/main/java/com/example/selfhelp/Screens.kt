package com.example.selfhelp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartJourney: () -> Unit,
    onMoodTracker: () -> Unit,
    onTechniqueLog: () -> Unit,
    onDailyRoutine: () -> Unit,
    onDashboard: () -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SelfHelp") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Welcome",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "A gentle self-help space with interactive pathways, coping tools, mood tracking, routine support, and technique history.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onStartJourney, modifier = Modifier.fillMaxWidth()) {
                Text("Start Journey")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onDailyRoutine, modifier = Modifier.fillMaxWidth()) {
                Text("Daily Routine")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onMoodTracker, modifier = Modifier.fillMaxWidth()) {
                Text("Mood Tracker")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onTechniqueLog, modifier = Modifier.fillMaxWidth()) {
                Text("Techniques Used")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onDashboard, modifier = Modifier.fillMaxWidth()) {
                Text("Dashboard")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
                Text("Settings")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    story: Map<String, Scene>,
    currentSceneId: String,
    lastMapSceneId: String,
    onSceneChange: (String) -> Unit,
    onMapChange: (String) -> Unit,
    onBackHome: () -> Unit,
    onTechniqueTracked: (String) -> Unit
) {
    val currentScene = story[currentSceneId] ?: story["start"]

    val mapSceneIds = setOf(
        "map_sunny",
        "map_cloudy",
        "map_rainy",
        "map_windy"
    )

    val techniqueSceneNames = mapOf(
        "grounding_54321" to "5-4-3-2-1 Grounding",
        "breathing_box" to "Box Breathing",
        "deer_temp" to "Temperature Reset",
        "deer_wave" to "Ride the Wave",
        "hedgehog_restart" to "Gentle Restart Steps",
        "hedgehog_slow" to "Permission to Go Slow",
        "otter_nice" to "Two-Minute Nice Thing",
        "otter_senses" to "Soothing Senses",
        "badger_sentences" to "Boundary Sentences",
        "badger_guilt" to "Guilt vs Safety",
        "hare_thoughts" to "Thoughts Aren’t Facts",
        "hare_next_step" to "Pick One Next Step",
        "rabbit_compassion" to "Self-Compassion Sentence",
        "robin_comfort" to "Comfort Words",
        "robin_plan" to "Small Support Plan",
        "fox_name" to "Name the Critic Voice",
        "fox_rewrite" to "Rewrite the Thought Kindly"
    )

    LaunchedEffect(currentSceneId) {
        val techniqueName = techniqueSceneNames[currentSceneId]
        if (techniqueName != null) {
            onTechniqueTracked(techniqueName)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Journey") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentScene?.text ?: "Scene not found.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                currentScene?.choices?.forEach { choice ->
                    Button(
                        onClick = {
                            when (choice.next) {
                                "Return to Map" -> {
                                    onSceneChange(
                                        if (lastMapSceneId in story) lastMapSceneId else "start"
                                    )
                                }

                                else -> {
                                    if (choice.next in mapSceneIds) {
                                        onMapChange(choice.next)
                                    }
                                    onSceneChange(choice.next)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(choice.text)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSceneChange("start")
                        onMapChange("start")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restart Story")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackHome,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedTheme: AppThemeOption,
    onThemeSelected: (AppThemeOption) -> Unit,
    onBackHome: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppThemeOption.entries.forEach { themeOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedTheme == themeOption,
                                    onClick = { onThemeSelected(themeOption) }
                                )
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedTheme == themeOption,
                                onClick = { onThemeSelected(themeOption) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = themeOption.label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerScreen(
    moodEntries: List<MoodEntry>,
    onAddMood: (String, String) -> Unit,
    onClearHistory: () -> Unit,
    onBackHome: () -> Unit
) {
    val moodOptions = listOf(
        "Good / okay",
        "Low / heavy",
        "Anxious / on edge",
        "Numb / disconnected"
    )

    val noteState = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Tracker") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How are you feeling right now?",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = noteState.value,
                        onValueChange = { noteState.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Optional note") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    moodOptions.forEach { mood ->
                        Button(
                            onClick = {
                                onAddMood(mood, noteState.value)
                                noteState.value = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(mood)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onClearHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Mood History")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Home")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Mood History",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (moodEntries.isEmpty()) {
                Text("No mood entries yet.")
            } else {
                moodEntries.forEach { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = entry.mood,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = entry.timestamp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (entry.note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Note: ${entry.note}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniqueLogScreen(
    techniqueEntries: List<TechniqueEntry>,
    onClearHistory: () -> Unit,
    onBackHome: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Techniques Used") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onClearHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Technique History")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Home")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Technique History",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (techniqueEntries.isEmpty()) {
                Text("No techniques logged yet.")
            } else {
                techniqueEntries.forEach { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = entry.technique,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = entry.timestamp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRoutineScreen(
    tasks: List<DailyTask>,
    checkedTaskIds: Set<String>,
    onToggleTask: (String, Boolean) -> Unit,
    onAddTask: (String, String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onClearTodayChecks: () -> Unit,
    onBackHome: () -> Unit
) {
    val titleState = remember { mutableStateOf("") }
    val timeState = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daily Routine") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Custom Task",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = titleState.value,
                        onValueChange = { titleState.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Task name") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = timeState.value,
                        onValueChange = { timeState.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Optional time (e.g. 08:00)") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val title = titleState.value.trim()
                            val time = timeState.value.trim()
                            if (title.isNotBlank()) {
                                onAddTask(title, time)
                                titleState.value = ""
                                timeState.value = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Task")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onClearTodayChecks,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Today")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Home")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Today’s Checklist",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            tasks.forEach { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Checkbox(
                            checked = task.id in checkedTaskIds,
                            onCheckedChange = { checked ->
                                onToggleTask(task.id, checked)
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            if (task.time.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = task.time,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (task.isBuiltIn) "Built-in task" else "Custom task",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (!task.isBuiltIn) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onDeleteTask(task.id) }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }

            if (tasks.isEmpty()) {
                Text("No routine tasks yet.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    moodEntries: List<MoodEntry>,
    techniqueEntries: List<TechniqueEntry>,
    tasks: List<DailyTask>,
    checkedTaskIds: Set<String>,
    onBackHome: () -> Unit
) {
    val moodCounts = moodEntries.groupingBy { it.mood }.eachCount().toList().sortedByDescending { it.second }
    val techniqueCounts = techniqueEntries.groupingBy { it.technique }.eachCount().toList().sortedByDescending { it.second }

    val latestMood = moodEntries.firstOrNull()?.mood ?: "No mood logged yet"
    val latestTechnique = techniqueEntries.firstOrNull()?.technique ?: "No technique logged yet"

    val mostCommonMood = moodCounts.firstOrNull()?.first ?: "No data"
    val mostUsedTechnique = techniqueCounts.firstOrNull()?.first ?: "No data"

    val completedCount = checkedTaskIds.count { checkedId -> tasks.any { it.id == checkedId } }
    val totalTaskCount = tasks.size
    val routinePercent = if (totalTaskCount > 0) {
        (completedCount * 100) / totalTaskCount
    } else {
        0
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mood Overview", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Latest mood: $latestMood")
                    Text("Total mood entries: ${moodEntries.size}")
                    Text("Most common mood: $mostCommonMood")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Technique Overview", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Latest technique: $latestTechnique")
                    Text("Total techniques used: ${techniqueEntries.size}")
                    Text("Most used technique: $mostUsedTechnique")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today’s Routine", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Completed: $completedCount / $totalTaskCount")
                    Text("Completion: $routinePercent%")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Mood Breakdown", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(10.dp))

            if (moodCounts.isEmpty()) {
                Text("No mood data yet.")
            } else {
                moodCounts.forEach { (mood, count) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = mood, style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Count: $count", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Technique Breakdown", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(10.dp))

            if (techniqueCounts.isEmpty()) {
                Text("No technique data yet.")
            } else {
                techniqueCounts.forEach { (technique, count) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = technique, style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Count: $count", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}