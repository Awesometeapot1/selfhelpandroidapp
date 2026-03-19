package com.example.selfhelp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Story Map ─────────────────────────────────────────────────────────────────

@Composable
fun StoryMapView(
    storyMap: Map<String, Scene>,
    visitedSceneIds: List<String>,
    currentSceneId: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    val nodeRadius = 20.dp
    val hSpacing = 76.dp
    val vSpacing = 62.dp
    val paddingH = 28.dp
    val paddingV = 20.dp

    val currentScene = storyMap[currentSceneId]
    val futureChoices = currentScene?.choices ?: emptyList()
    val nFuture = futureChoices.size
    val nVisited = visitedSceneIds.size.coerceAtLeast(1)
    val curIdx = nVisited - 1

    // Distinct hue per choice so they're easy to distinguish
    val choiceColors = remember(nFuture) {
        (0 until nFuture).map { i ->
            Color.hsv((i.toFloat() / nFuture.coerceAtLeast(1)) * 360f, 0.60f, 0.85f)
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val containerW = maxWidth
        // Keep current node centered; push right only when history is long
        val centerX = maxOf(containerW / 2, paddingH + hSpacing * curIdx)
        val futureSpread = if (nFuture <= 1) 0.dp else hSpacing * (nFuture - 1)
        val futureEndX = centerX + futureSpread / 2

        val mapWidth = maxOf(centerX + paddingH, futureEndX + paddingH)
        val mapHeight = paddingV * 2 + nodeRadius * 2 +
                if (nFuture > 0) vSpacing + nodeRadius * 2 + 8.dp else 0.dp

        LaunchedEffect(nVisited, containerW) {
            val scrollPx = with(density) {
                (centerX - containerW / 2).coerceAtLeast(0.dp).roundToPx()
            }
            scrollState.animateScrollTo(scrollPx)
        }

        Box(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
            Canvas(modifier = Modifier.size(width = mapWidth, height = mapHeight)) {
                val rPx = nodeRadius.toPx()
                val hSpPx = hSpacing.toPx()
                val vSpPx = vSpacing.toPx()
                val padVPx = paddingV.toPx()
                val curCX = centerX.toPx()
                val row0Y = padVPx + rPx
                val row1Y = row0Y + vSpPx

                // Visited node centers: history grows left of curCX
                val visitedCXs = (0 until nVisited).map { i -> curCX - (curIdx - i) * hSpPx }

                val futureCXs: List<Float> = if (nFuture == 0) emptyList() else {
                    val spread = (nFuture - 1) * hSpPx
                    val startCX = curCX - spread / 2f
                    (0 until nFuture).map { i -> startCX + i * hSpPx }
                }

                // History path lines
                for (i in 0 until nVisited - 1) {
                    drawLine(
                        color = primaryColor.copy(alpha = 0.55f),
                        start = Offset(visitedCXs[i] + rPx, row0Y),
                        end = Offset(visitedCXs[i + 1] - rPx, row0Y),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Dashed color-coded lines to future nodes
                futureCXs.forEachIndexed { idx, fcx ->
                    val cc = choiceColors.getOrElse(idx) { primaryColor }
                    val dx = fcx - curCX; val dy = row1Y - row0Y
                    val len = kotlin.math.sqrt(dx * dx + dy * dy)
                    if (len > 0f) {
                        val ux = dx / len; val uy = dy / len
                        var t = rPx + 4f
                        while (t < len - rPx * 0.75f) {
                            val tEnd = minOf(t + 8f, len - rPx * 0.75f)
                            drawLine(
                                color = cc.copy(alpha = 0.65f),
                                start = Offset(curCX + ux * t, row0Y + uy * t),
                                end = Offset(curCX + ux * tEnd, row0Y + uy * tEnd),
                                strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round
                            )
                            t += 14f
                        }
                    }
                }

                // Visited nodes
                for (i in 0 until nVisited) {
                    val cx = visitedCXs[i]
                    val isCurrent = i == curIdx
                    if (isCurrent) {
                        drawCircle(color = primaryColor.copy(alpha = 0.18f), radius = rPx * 1.55f, center = Offset(cx, row0Y))
                    }
                    drawCircle(
                        color = if (isCurrent) primaryColor else primaryColor.copy(alpha = 0.42f),
                        radius = rPx, center = Offset(cx, row0Y)
                    )
                }

                // Future choice nodes — color-coded
                futureCXs.forEachIndexed { idx, fcx ->
                    val cc = choiceColors.getOrElse(idx) { primaryColor }
                    drawCircle(color = cc.copy(alpha = 0.22f), radius = rPx * 0.8f, center = Offset(fcx, row1Y))
                    drawCircle(color = cc.copy(alpha = 0.75f), radius = rPx * 0.8f, center = Offset(fcx, row1Y), style = Stroke(width = 2.dp.toPx()))
                }

                // Text labels
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                for (i in 0 until nVisited) {
                    val cx = visitedCXs[i]
                    val isCurrent = i == curIdx
                    val label = visitedSceneIds[i].replace('_', ' ').take(7)
                    paint.textSize = (if (isCurrent) 11.dp else 9.5.dp).toPx()
                    paint.color = android.graphics.Color.WHITE
                    paint.isFakeBoldText = isCurrent
                    drawContext.canvas.nativeCanvas.drawText(label, cx, row0Y + paint.textSize * 0.38f, paint)
                }
                paint.isFakeBoldText = false
                paint.textSize = 8.5.dp.toPx()
                futureCXs.forEachIndexed { i, fcx ->
                    val cc = choiceColors.getOrElse(i) { onSurfaceVariantColor }
                    paint.color = cc.copy(alpha = 0.9f).toArgb()
                    val label = futureChoices[i].text.take(11)
                    drawContext.canvas.nativeCanvas.drawText(label, fcx, row1Y + paint.textSize * 0.38f, paint)
                }
            }
        }
    }
}

// ── Home ─────────────────────────────────────────────────────────────────────

private data class HomeFeature(
    val icon: ImageVector,
    val label: String,
    val description: String,
    val screen: AppScreen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (AppScreen) -> Unit, onOpenMenu: () -> Unit) {
    val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }

    val features = listOf(
        HomeFeature(Icons.Default.Favorite,    "Mood Check-in",  "How are you feeling right now?", AppScreen.MOOD_TRACKER),
        HomeFeature(Icons.Default.Edit,        "Daily Routine",  "Your tasks and structure today",  AppScreen.DAILY_ROUTINE),
        HomeFeature(Icons.Default.ArrowForward,"Breathing",      "Calm and ground your body",       AppScreen.BREATHING),
        HomeFeature(Icons.Default.Star,        "Notes",          "A private space for thoughts",    AppScreen.NOTES),
        HomeFeature(Icons.Default.Star,        "Progress",       "Look back at your journey",       AppScreen.DASHBOARD),
        HomeFeature(Icons.Default.Settings,    "Settings",       "Themes and preferences",          AppScreen.SETTINGS),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SelfHelp") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero / greeting card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        greeting,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "This is your gentle space. Take things at your own pace.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigate(AppScreen.STORY) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start the Guided Story")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "A choose-your-own-path through your feelings",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "What would you like to do?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 2-column feature grid
            features.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { feature ->
                        Card(
                            onClick = { onNavigate(feature.screen) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = feature.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    feature.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    feature.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ── Mood Tracker ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerScreen(
    moodEntries: List<MoodEntry>,
    onSaveMood: (mood: String, note: String) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val moods = listOf("Good / okay", "Low / heavy", "Anxious / on edge", "Numb / disconnected")
    var note by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("How are you feeling?", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Optional note") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    moods.forEach { mood ->
                        Button(
                            onClick = { onSaveMood(mood, note); note = "" },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) { Text(mood) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClearHistory, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Clear Mood History")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Mood History", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (moodEntries.isEmpty()) {
                Text("No mood entries yet.")
            } else {
                moodEntries.reversed().forEach { entry ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(entry.mood, style = MaterialTheme.typography.bodyLarge)
                            Text(entry.timestamp, style = MaterialTheme.typography.bodySmall)
                            if (entry.note.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(entry.note, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Dashboard / Stats ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    moodEntries: List<MoodEntry>,
    techniqueEntries: List<TechniqueEntry>,
    dailyTasks: List<DailyTask>,
    checkedTaskIds: Set<String>,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val latestMood = moodEntries.lastOrNull()?.mood ?: "None"
    val mostCommonMood = moodEntries.groupingBy { it.mood }.eachCount().maxByOrNull { it.value }?.key ?: "None"
    val latestTool = techniqueEntries.lastOrNull()?.technique ?: "None"
    val routineCompletion = if (dailyTasks.isEmpty()) 0 else (checkedTaskIds.size * 100 / dailyTasks.size)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Summary", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Latest Mood: $latestMood")
                    Text("Most Common Mood: $mostCommonMood")
                    Text("Latest Tool: $latestTool")
                    Text("Routine Completion: $routineCompletion%")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Mood Breakdown", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            val moodCounts = moodEntries.groupingBy { it.mood }.eachCount()
            if (moodCounts.isEmpty()) {
                Text("No mood data yet.")
            } else {
                moodCounts.forEach { (mood, count) ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(mood); Text("$count")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Technique Breakdown", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            val techniqueCounts = techniqueEntries.groupingBy { it.technique }.eachCount()
            if (techniqueCounts.isEmpty()) {
                Text("No technique data yet.")
            } else {
                techniqueCounts.forEach { (tech, count) ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(tech); Text("Used $count times")
                        }
                    }
                }
            }
        }
    }
}

// ── Notes ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    noteEntries: List<NoteEntry>,
    onSaveNote: (text: String) -> Unit,
    onClearNotes: () -> Unit,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Log a note", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("What's on your mind?") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { if (noteText.isNotBlank()) { onSaveNote(noteText); noteText = "" } },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Save Note") }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClearNotes, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Clear All Notes")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Recent Notes", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (noteEntries.isEmpty()) {
                Text("No notes yet.")
            } else {
                noteEntries.reversed().forEach { entry ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(entry.text, style = MaterialTheme.typography.bodyMedium)
                            Text(entry.timestamp, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

// ── Daily Routine ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRoutineScreen(
    tasks: List<DailyTask>,
    checkedIds: Set<String>,
    onAddTask: (title: String, time: String) -> Unit,
    onToggleTask: (id: String) -> Unit,
    onEditTask: (id: String, title: String, time: String) -> Unit,
    onDeleteTask: (id: String) -> Unit,
    onClearProgress: () -> Unit,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var taskTime by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<DailyTask?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }

    // Edit dialog
    if (editingTask != null) {
        AlertDialog(
            onDismissRequest = { editingTask = null },
            title = { Text("Edit Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Task name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editTime,
                        onValueChange = { editTime = it },
                        label = { Text("Time (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    editingTask?.let { onEditTask(it.id, editTitle, editTime) }
                    editingTask = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingTask = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Routine") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Task", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Task name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = taskTime,
                        onValueChange = { taskTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Optional time") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (taskName.isNotBlank()) {
                                onAddTask(taskName, taskTime)
                                taskName = ""
                                taskTime = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add to List") }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClearProgress, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Clear Today's Progress")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Today's Checklist", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            tasks.forEach { task ->
                val isChecked = task.id in checkedIds
                Card(
                    onClick = { onToggleTask(task.id) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isChecked, onCheckedChange = { onToggleTask(task.id) })
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, style = MaterialTheme.typography.bodyLarge)
                            if (task.time.isNotEmpty()) {
                                Text(task.time, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        IconButton(onClick = {
                            editTitle = task.title
                            editTime = task.time
                            editingTask = task
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit task", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onDeleteTask(task.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete task", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

// ── Story / Journey ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(onBack: () -> Unit, onOpenMenu: () -> Unit) {
    val context = LocalContext.current
    val storyMap = remember {
        try { loadStoryFromAssets(context) } catch (e: Exception) { emptyMap() }
    }

    var currentSceneId by rememberSaveable { mutableStateOf("start") }
    val history = remember { mutableStateListOf<String>() }
    val routeHistory = remember { mutableStateListOf("start") }
    var lastChoice by rememberSaveable { mutableStateOf("") }
    val currentScene = storyMap[currentSceneId]

    fun navigateTo(sceneId: String, choiceText: String) {
        history.add(currentSceneId)
        routeHistory.add(choiceText)
        lastChoice = choiceText
        currentSceneId = sceneId
    }

    fun goBack() {
        if (history.isNotEmpty()) {
            currentSceneId = history.removeLast()
            if (routeHistory.size > 1) routeHistory.removeLast()
            lastChoice = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (storyMap.isEmpty()) {
                Text("Story not found. Please add a story.json file to the assets folder.")
                return@Column
            }
            if (currentScene == null) {
                Text("Scene not found.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    currentSceneId = "start"
                    history.clear()
                    routeHistory.clear()
                    routeHistory.add("start")
                    lastChoice = ""
                }) {
                    Text("Restart Story")
                }
                return@Column
            }

            // Story map
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                StoryMapView(
                    storyMap = storyMap,
                    visitedSceneIds = history.toList() + currentSceneId,
                    currentSceneId = currentSceneId,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Show what choice led here
            if (lastChoice.isNotEmpty()) {
                Text(
                    "You chose: $lastChoice",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Scene text
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(currentScene.text, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Choices
            currentScene.choices.forEachIndexed { index, choice ->
                Button(
                    onClick = { navigateTo(choice.next, choice.text) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(choice.text)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back through story
                if (history.isNotEmpty()) {
                    TextButton(onClick = { goBack() }) {
                        Text("← Go Back")
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                TextButton(onClick = {
                    currentSceneId = "start"
                    history.clear()
                    routeHistory.clear()
                    routeHistory.add("start")
                    lastChoice = ""
                }) {
                    Text("Restart Story")
                }
            }
        }
    }
}

// ── Settings ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedTheme: AppThemeOption,
    onThemeChange: (AppThemeOption) -> Unit,
    largeText: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Theme", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Choose a colour theme that feels right for you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppThemeOption.entries.forEach { theme ->
                val isSelected = selectedTheme == theme
                Card(
                    onClick = { onThemeChange(theme) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onThemeChange(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(theme.label, style = MaterialTheme.typography.bodyLarge)
                            val description = when (theme) {
                                AppThemeOption.SOFT_LAVENDER -> "Gentle light purples — soft and calming"
                                AppThemeOption.FOREST_CALM   -> "Muted greens — grounded and earthy"
                                AppThemeOption.DARK_NIGHT    -> "Deep navy and lavender — quiet and still"
                            }
                            Text(
                                description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Accessibility ──────────────────────────────────────────────
            Spacer(modifier = Modifier.height(24.dp))
            Text("Accessibility", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Adjust the app to suit your needs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Larger text", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Increases text size throughout the app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = largeText, onCheckedChange = onLargeTextChange)
                }
            }

            // ── Language ───────────────────────────────────────────────────
            Spacer(modifier = Modifier.height(24.dp))
            Text("Language", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Changing language restarts the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppLanguage.entries.forEach { lang ->
                val isSelected = selectedLanguage == lang
                Card(
                    onClick = { onLanguageChange(lang) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = isSelected, onClick = { onLanguageChange(lang) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(lang.label, style = MaterialTheme.typography.bodyLarge)
                            if (lang != AppLanguage.ENGLISH) {
                                Text(
                                    "Translation in progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Breathing ─────────────────────────────────────────────────────────────────

private data class BreathPhase(val name: String, val seconds: Int, val scale: Float)
private data class BreathTechnique(val name: String, val description: String, val phases: List<BreathPhase>)

private val breathingTechniques = listOf(
    BreathTechnique(
        name = "Box Breathing",
        description = "Equal counts across all phases — steadies the nervous system and sharpens focus",
        phases = listOf(
            BreathPhase("Inhale",  4, 1.00f),
            BreathPhase("Hold",    4, 1.00f),
            BreathPhase("Exhale",  4, 0.35f),
            BreathPhase("Hold",    4, 0.35f)
        )
    ),
    BreathTechnique(
        name = "4-7-8",
        description = "Extended exhale activates the parasympathetic system — helpful for anxiety and sleep",
        phases = listOf(
            BreathPhase("Inhale",  4, 1.00f),
            BreathPhase("Hold",    7, 1.00f),
            BreathPhase("Exhale",  8, 0.35f)
        )
    ),
    BreathTechnique(
        name = "Deep Breathing",
        description = "Slow and gentle — good for beginners or moments of overwhelm",
        phases = listOf(
            BreathPhase("Inhale",  4, 1.00f),
            BreathPhase("Exhale",  6, 0.35f)
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(onBack: () -> Unit, onOpenMenu: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var phaseIndex by remember { mutableStateOf(0) }
    var countdown by remember { mutableStateOf(0) }
    val circleScale = remember { Animatable(0.35f) }

    val technique = breathingTechniques[selectedIndex]
    val currentPhase = technique.phases[phaseIndex]
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(isRunning, phaseIndex, selectedIndex) {
        if (!isRunning) {
            circleScale.animateTo(0.35f, tween(700))
            return@LaunchedEffect
        }
        val phase = breathingTechniques[selectedIndex].phases[phaseIndex]
        coroutineScope {
            launch {
                circleScale.animateTo(
                    targetValue = phase.scale,
                    animationSpec = tween(durationMillis = phase.seconds * 1000, easing = LinearEasing)
                )
            }
            launch {
                for (i in phase.seconds downTo 1) {
                    countdown = i
                    delay(1000L)
                }
            }
        }
        phaseIndex = (phaseIndex + 1) % breathingTechniques[selectedIndex].phases.size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breathing") },
                navigationIcon = {
                    IconButton(onClick = { isRunning = false; onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Technique chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                breathingTechniques.forEachIndexed { i, tech ->
                    FilterChip(
                        selected = selectedIndex == i,
                        onClick = {
                            if (selectedIndex != i) {
                                isRunning = false
                                phaseIndex = 0
                                countdown = 0
                                selectedIndex = i
                            }
                        },
                        label = { Text(tech.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Technique description
            Text(
                text = technique.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Phase guide chips
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                technique.phases.forEachIndexed { i, phase ->
                    val isActive = isRunning && phaseIndex == i
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isActive) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "${phase.name} ${phase.seconds}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Breathing circle — tap to start/stop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .clickable {
                        if (isRunning) {
                            isRunning = false
                            phaseIndex = 0
                            countdown = 0
                        } else {
                            phaseIndex = 0
                            isRunning = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val max = size.minDimension / 2
                    val r = max * circleScale.value
                    drawCircle(color = primaryColor.copy(alpha = 0.06f), radius = r * 1.55f)
                    drawCircle(color = primaryColor.copy(alpha = 0.10f), radius = r * 1.30f)
                    drawCircle(color = primaryColor.copy(alpha = 0.20f), radius = r * 1.10f)
                    drawCircle(color = primaryColor.copy(alpha = 0.35f), radius = r)
                    drawCircle(color = primaryColor.copy(alpha = 0.55f), radius = r * 0.65f)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isRunning) {
                        Text(
                            text = currentPhase.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "$countdown",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = if (countdown == 0) "Tap to\nbegin" else "Tap to\nrestart",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
