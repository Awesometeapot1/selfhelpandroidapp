package com.example.selfhelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Choice(
    val text: String,
    val nextSceneId: String
)

data class Scene(
    val text: String,
    val choices: List<Choice> = emptyList()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SelfHelpApp()
            }
        }
    }
}

val story = mapOf(
    "start" to Scene(
        text = """
            You arrive at the edge of a quiet field.
            grass sprouts from the ground like ribbons.
            
            Before you go further, you stop.
            What kind of day is it?
            
            Quick access:
            Hare’s Burrow has three Memory Rooms (Green/Amber/Red).
        """.trimIndent(),
        choices = listOf(
            Choice("Sunny day (steady/okay)", "map_sunny"),
            Choice("Cloudy day (low/heavy)", "map_cloudy"),
            Choice("Rainy day (sad/tearful)", "map_rainy"),
            Choice("Windy day (anxious/restless)", "map_windy"),
            Choice("Daily Check-In", "checkin_intro"),
            Choice("Quick Access: Memory Rooms (Hare’s Burrow)", "burrow_hare")
        )
    ),

    "map_sunny" to Scene(
        text = """
            MAP — SUNNY DAY
            
            Sunlight shines softly onto you.
            The sky is a deep blue. Existing isnt hard.
            
            Where do you go?
        """.trimIndent(),
        choices = listOf(
            Choice("North: Deer Path (panic tools)", "deer_intro"),
            Choice("East: River (Otter / soothing)", "otter_intro"),
            Choice("South: Hedge (Hedgehog / shutdown)", "hedgehog_intro"),
            Choice("West: Stones (Badger / boundaries)", "badger_intro"),
            Choice("Center: Big Tree (Rabbit, Hare, Robin, fox, owl)", "tree_hub"),
            Choice("Daily Check-In", "checkin_intro"),
            Choice("Memory Rooms (Hare’s Burrow)", "burrow_hare"),
            Choice("Change day/weather", "start")
        )
    ),

    "map_cloudy" to Scene(
        text = """
            MAP — CLOUDY DAY
            
            its darker now.
            the slow drawl of time sits with you.
            
            Where do you go?
        """.trimIndent(),
        choices = listOf(
            Choice("North: Deer Path (panic tools)", "deer_intro"),
            Choice("East: River (Otter / soothing)", "otter_intro"),
            Choice("South: Hedge (Hedgehog / shutdown)", "hedgehog_intro"),
            Choice("West: Stones (Badger / boundaries)", "badger_intro"),
            Choice("Center: Big Tree (Rabbit, Hare, Robin, fox, owl)", "tree_hub"),
            Choice("Daily Check-In", "checkin_intro"),
            Choice("Memory Rooms (Hare’s Burrow)", "burrow_hare"),
            Choice("Change day/weather", "start")
        )
    ),

    "map_rainy" to Scene(
        text = """
            MAP — RAINY DAY
            
            Rain sprinkles from the sky .
            The grass is cool, and you feel a pain in your chest.
            
            Where do you go?
        """.trimIndent(),
        choices = listOf(
            Choice("North: Deer Path (panic tools)", "deer_intro"),
            Choice("East: River (Otter / soothing)", "otter_intro"),
            Choice("South: Hedge (Hedgehog / shutdown)", "hedgehog_intro"),
            Choice("West: Stones (Badger / boundaries)", "badger_intro"),
            Choice("Center: Big Tree (Rabbit, Hare, Robin, fox, owl)", "tree_hub"),
            Choice("Daily Check-In", "checkin_intro"),
            Choice("Memory Rooms (Hare’s Burrow)", "burrow_hare"),
            Choice("Change day/weather", "start")
        )
    ),

    "map_windy" to Scene(
        text = """
            MAP — WINDY DAY
            
            Wind whispers its cool breath.
            Your thoughts keep moving, not able to stop.
            
            Where do you go?
        """.trimIndent(),
        choices = listOf(
            Choice("North: Deer Path (panic tools)", "deer_intro"),
            Choice("East: River (Otter / soothing)", "otter_intro"),
            Choice("South: Hedge (Hedgehog / shutdown)", "hedgehog_intro"),
            Choice("West: Stones (Badger / boundaries)", "badger_intro"),
            Choice("Center: Big Tree (Rabbit, Hare, Robin, fox, owl)", "tree_hub"),
            Choice("Daily Check-In", "checkin_intro"),
            Choice("Memory Rooms (Hare’s Burrow)", "burrow_hare"),
            Choice("Change day/weather", "start")
        )
    ),

    "tree_hub" to Scene(
        text = """
            CENTER — THE BIG TREE
            
            Rabbit sits with calm attention.
            Hare looks like it’s halfway into tomorrow.
            A robin watches you with gentle care.
            
            a fox trail nearby.
            
            an owl snoozes on a branch.
            
            Who do you go to?
        """.trimIndent(),
        choices = listOf(
            Choice("Rabbit (feelings + compassion)", "rabbit_intro"),
            Choice("Hare (overthinking + next step)", "hare_intro"),
            Choice("Robin (support)", "robin_intro"),
            Choice("Fox (inner critic)", "fox_intro"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "burrow_hare" to Scene(
        text = """
            HARE’S BURROW
            
            You duck into a quiet burrow.
            The air is calm and still.
            
            Three doors stand in a row:
            GREEN • AMBER • RED
            
            Hare whispers:
            “These are Memory Rooms. Quick to reach when you need them.”
        """.trimIndent(),
        choices = listOf(
            Choice("Green Room (steady)", "memory_green"),
            Choice("Amber Room (early warning)", "memory_amber"),
            Choice("Red Room (urgent)", "memory_red"),
            Choice("Back to Start", "start"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "memory_green" to Scene(
        text = """
            MEMORY ROOM — GREEN
            Signs you’re doing okay / staying steady.
            
            GREEN SIGNS (examples):
            - Sleeping roughly as normal
            - Eating/drinking enough
            - Able to focus sometimes
            - Stress feels manageable
            - You can reality-check worries
            
            KEEP IT STEADY (small actions):
            - Keep a routine (sleep/wake)
            - Eat something simple + drink water
            - Take breaks from scrolling
            - Gentle movement (stretch/walk)
            - Stay connected to 1 person
            
            If you notice things shifting, step into AMBER early.
        """.trimIndent(),
        choices = listOf(
            Choice("Go to Amber Room", "memory_amber"),
            Choice("Back to Burrow", "burrow_hare"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "memory_amber" to Scene(
        text = """
            MEMORY ROOM — AMBER
            Early warning signs (slow down + get support).
            
            AMBER SIGNS (examples):
            - Sleep changing (less, broken, very restless)
            - Feeling unusually anxious, wired, overwhelmed
            - Finding meanings/patterns everywhere (more than usual)
            - Feeling suspicious or on-guard
            - Hearing/seeing things at the edge of perception
            - Thoughts racing or hard to organise
            - Withdrawing from people / feeling disconnected
            
            AMBER PLAN (do these now):
            1) Reduce stress + stimulation (quiet room, dim lights)
            2) Prioritise sleep (wind-down, no caffeine late)
            3) Eat + drink something
            4) Talk to someone you trust today
            5) Contact your GP / mental health team if you have one
            
            If you feel unsafe or things escalate, step into RED.
        """.trimIndent(),
        choices = listOf(
            Choice("Go to Red Room", "memory_red"),
            Choice("Back to Green Room", "memory_green"),
            Choice("Back to Burrow", "burrow_hare"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "memory_red" to Scene(
        text = """
            MEMORY ROOM — RED
            Urgent signs (get help now).
            
            RED SIGNS (examples):
            - Feeling out of control or unable to keep yourself safe
            - Strong beliefs others say aren’t real, and you can’t reality-check
            - Hearing/seeing things that feel very real and distressing
            - Severe agitation or fear
            - Not sleeping for a long time + symptoms intensifying
            
            DO THIS NOW:
            1) Don’t stay alone if possible — contact someone you trust
            2) If you have a crisis line / mental health team, call them
            3) If there’s immediate danger, call emergency services
            
            UK QUICK INFO:
            - Immediate danger: call 999
            - Urgent but not life-threatening: NHS 111
            
            You deserve support. This is a ‘get help’ moment.
        """.trimIndent(),
        choices = listOf(
            Choice("Back to Amber Room", "memory_amber"),
            Choice("Back to Burrow", "burrow_hare"),
            Choice("Back to Start", "start")
        )
    ),

    "checkin_intro" to Scene(
        text = """
            DAILY CHECK-IN
            
            No judgement. Just noticing.
            
            How is your mood right now?
        """.trimIndent(),
        choices = listOf(
            Choice("Good / okay", "checkin_mood_ok"),
            Choice("Low / heavy", "checkin_mood_low"),
            Choice("Anxious / on edge", "checkin_mood_anx"),
            Choice("Numb / disconnected", "checkin_mood_numb")
        )
    ),

    "checkin_mood_ok" to Scene(
        text = "Mood: good / okay.\n\nHow’s your energy?",
        choices = listOf(
            Choice("High", "checkin_energy_high"),
            Choice("Medium", "checkin_energy_mid"),
            Choice("Low", "checkin_energy_low")
        )
    ),

    "checkin_mood_low" to Scene(
        text = "Mood: low / heavy.\n\nThat makes sense.\nHow’s your energy?",
        choices = listOf(
            Choice("High", "checkin_energy_high"),
            Choice("Medium", "checkin_energy_mid"),
            Choice("Low", "checkin_energy_low")
        )
    ),

    "checkin_mood_anx" to Scene(
        text = "Mood: anxious / on edge.\n\nYour body is trying to protect you.\nHow’s your energy?",
        choices = listOf(
            Choice("High", "checkin_energy_high"),
            Choice("Medium", "checkin_energy_mid"),
            Choice("Low", "checkin_energy_low")
        )
    ),

    "checkin_mood_numb" to Scene(
        text = "Mood: numb / disconnected.\n\nSometimes numb is coping.\nHow’s your energy?",
        choices = listOf(
            Choice("High", "checkin_energy_high"),
            Choice("Medium", "checkin_energy_mid"),
            Choice("Low", "checkin_energy_low")
        )
    ),

    "checkin_energy_high" to Scene(
        text = "Energy: high.\n\nWhat do you need most?",
        choices = listOf(
            Choice("Calm", "checkin_need_calm"),
            Choice("Focus", "checkin_need_focus"),
            Choice("Connection", "checkin_need_connect"),
            Choice("Joy", "checkin_need_joy")
        )
    ),

    "checkin_energy_mid" to Scene(
        text = "Energy: medium.\n\nWhat do you need most?",
        choices = listOf(
            Choice("Calm", "checkin_need_calm"),
            Choice("Focus", "checkin_need_focus"),
            Choice("Connection", "checkin_need_connect"),
            Choice("Joy", "checkin_need_joy")
        )
    ),

    "checkin_energy_low" to Scene(
        text = "Energy: low.\n\nLet’s keep it gentle.\nWhat do you need most?",
        choices = listOf(
            Choice("Rest", "checkin_need_rest"),
            Choice("Calm", "checkin_need_calm"),
            Choice("Connection", "checkin_need_connect"),
            Choice("Tiny task", "checkin_need_tiny")
        )
    ),

    "checkin_need_calm" to Scene(
        text = """
            Need: calm.
            
            Pick one:
            - Box breathing
            - 5–4–3–2–1 grounding
            - Panic tools (Deer)
        """.trimIndent(),
        choices = listOf(
            Choice("Box breathing", "breathing_box"),
            Choice("5–4–3–2–1 grounding", "grounding_54321"),
            Choice("Visit Deer (panic tools)", "deer_intro"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "checkin_need_focus" to Scene(
        text = """
            Need: focus.
            
            Try: 2 minutes only.
            Pick ONE tiny step.
            Stop after 2 minutes if you want.
            
            Progress counts when it’s small.
        """.trimIndent(),
        choices = listOf(
            Choice("Visit Hare (next step)", "hare_next_step"),
            Choice("Visit Badger (boundaries)", "badger_intro"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "checkin_need_connect" to Scene(
        text = """
            Need: connection.
            
            Pick ONE:
            - Message someone
            - Sit near someone
            - Hold something soft
            
            Connection can be quiet.
        """.trimIndent(),
        choices = listOf(
            Choice("Visit Robin (support)", "robin_intro"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "checkin_need_joy" to Scene(
        text = """
            Need: joy.
            
            Tiny joy counts.
            - warm drink
            - favourite song
            - step into sunlight
        """.trimIndent(),
        choices = listOf(
            Choice("Visit Otter (joy)", "otter_intro"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "checkin_need_rest" to Scene(
        text = """
            Need: rest.
            
            Permission slip:
            “Rest is allowed.”
            
            One gentle reset:
            - water
            - wash face
            - lie down 10 mins
        """.trimIndent(),
        choices = listOf(
            Choice("Visit Hedgehog (shutdown)", "hedgehog_intro"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "checkin_need_tiny" to Scene(
        text = """
            Need: tiny task.
            
            Pick one micro-step:
            - open a window
            - put one thing away
            - stretch
            
            Small wins are real.
        """.trimIndent(),
        choices = listOf(
            Choice("Visit Hare (next step)", "hare_next_step"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "rabbit_intro" to Scene(
        text = """
            RABBIT
            
            “Feelings are like weather,” Rabbit says.
            “They change. They pass through.”
            
            What would help?
        """.trimIndent(),
        choices = listOf(
            Choice("Name the feeling", "rabbit_name"),
            Choice("Self-compassion sentence", "rabbit_compassion"),
            Choice("Grounding 5–4–3–2–1", "grounding_54321"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "rabbit_name" to Scene(
        text = """
            Pick a word that fits even a little:
            sad • anxious • angry • tired • lonely • overwhelmed
            
            Now add:
            “...and that makes sense.”
            
            Naming it can soften it.
        """.trimIndent(),
        choices = listOf(
            Choice("Self-compassion", "rabbit_compassion"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "rabbit_compassion" to Scene(
        text = """
            Try one kind sentence:
            
            - “This is hard, and I’m doing my best.”
            - “One step at a time.”
            - “I can be imperfect and still worthy.”
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "hare_intro" to Scene(
        text = """
            HARE
            
            “My brain runs ahead,” Hare admits.
            “It tries to protect me with worries.”
            
            Pick a tool:
        """.trimIndent(),
        choices = listOf(
            Choice("Thoughts aren’t facts", "hare_thoughts"),
            Choice("Pick one next step", "hare_next_step"),
            Choice("Box breathing", "breathing_box"),
            Choice("Memory Rooms (Burrow)", "burrow_hare"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "hare_thoughts" to Scene(
        text = """
            Say:
            “I’m having lots of difficult thoughts.”
            
            It creates space.
            You can notice thoughts without obeying them.
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "hare_next_step" to Scene(
        text = """
            Pick ONE small next step today.
            
            Not the whole mountain.
            Just one stone.
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "robin_intro" to Scene(
        text = """
            ROBIN
            
            The robin feels like care you can trust.
            Like your mum noticing you’re not okay.
            
            “You don’t have to carry this alone.”
        """.trimIndent(),
        choices = listOf(
            Choice("Comfort words", "robin_comfort"),
            Choice("Make a small support plan", "robin_plan"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "robin_comfort" to Scene(
        text = """
            Comfort words:
            
            - “I’m here.”
            - “You matter, even on messy days.”
            - “Rest is allowed.”
            
            THE END (comfort found).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "robin_plan" to Scene(
        text = """
            Tiny support plan:
            
            1) One person/place
            2) One calming action
            3) One kind sentence
            
            THE END (plan made).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "badger_intro" to Scene(
        text = """
            WEST — BADGER
            
            Badger peeks from a burrow near stones.
            “Boundaries are doors,” it says.
            
            Pick one:
        """.trimIndent(),
        choices = listOf(
            Choice("Boundary sentences", "badger_sentences"),
            Choice("Guilt vs safety", "badger_guilt"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "badger_sentences" to Scene(
        text = """
            Boundary sentences:
            
            - “I can’t do that today.”
            - “I need time to think.”
            - “No, thank you.”
            - “That doesn’t work for me.”
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "badger_guilt" to Scene(
        text = """
            Badger says:
            
            “Guilt isn’t always proof you’re wrong.
            Sometimes it’s proof you’re changing patterns.”
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "deer_intro" to Scene(
        text = """
            NORTH — DEER
            
            Deer stands in tall grass.
            “Panic is an alarm,” it says.
            “Loud, not always accurate.”
            
            Pick a tool:
        """.trimIndent(),
        choices = listOf(
            Choice("Temperature reset", "deer_temp"),
            Choice("Ride the wave", "deer_wave"),
            Choice("Box breathing", "breathing_box"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "deer_temp" to Scene(
        text = """
            Temperature reset:
            
            - splash cold water
            - hold something cool
            - step into fresh air
            
            THE END (tool learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "deer_wave" to Scene(
        text = """
            Ride the wave:
            
            Panic rises, peaks, falls.
            Say: “This is a wave. I can float.”
            
            Anchor: feel feet on ground.
            
            THE END (tool learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "hedgehog_intro" to Scene(
        text = """
            SOUTH — HEDGEHOG
            
            Hedgehog is curled in a shady hedge.
            “Shutdown happens when it’s too much,” it whispers.
            
            Pick one:
        """.trimIndent(),
        choices = listOf(
            Choice("Gentle restart steps", "hedgehog_restart"),
            Choice("Permission to go slow", "hedgehog_slow"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "hedgehog_restart" to Scene(
        text = """
            Gentle restart:
            
            1) sip water
            2) change temperature (blanket / air)
            3) one tiny task (1 min)
            
            THE END (tool learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "hedgehog_slow" to Scene(
        text = """
            Hedgehog says:
            
            “Slow is still moving.
            Small is still progress.
            Rest is still valid.”
            
            THE END (comfort found).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "otter_intro" to Scene(
        text = """
            EAST — OTTER
            
            Water glittering in sunlight.
            Otter floats on its back.
            
            “Joy isn’t a reward,” it says.
            “It’s fuel.”
            
            Pick one:
        """.trimIndent(),
        choices = listOf(
            Choice("Two-minute nice thing", "otter_nice"),
            Choice("Soothing senses", "otter_senses"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "otter_nice" to Scene(
        text = """
            Two-minute kindness:
            
            - warm drink
            - favourite song
            - step into sunlight
            - stretch
            
            THE END (joy fuel).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "otter_senses" to Scene(
        text = """
            Soothing senses:
            
            Choose one sense:
            - touch (soft fabric)
            - sound (gentle music)
            - smell (tea, soap)
            
            THE END (soothing found).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "fox_intro" to Scene(
        text = """
            FOX TRAIL
            
            Fox sits in a clearing with sharp eyes.
            “That inner critic tries to motivate with fear,” it says.
            
            Pick one:
        """.trimIndent(),
        choices = listOf(
            Choice("Name the critic voice", "fox_name"),
            Choice("Rewrite the thought kindly", "fox_rewrite"),
            Choice("Return to Map", "Return to Map")
        )
    ),

    "fox_name" to Scene(
        text = """
            Give the critic a nickname.
            When it appears, say:
            “Oh, it’s YOU again.”
            
            Distance helps.
            
            THE END (tool learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "fox_rewrite" to Scene(
        text = """
            Kind rewrite:
            
            Critic: “I’m failing.”
            Rewrite: “I’m struggling and learning.”
            
            Critic: “I can’t cope.”
            Rewrite: “I can cope in small chunks.”
            
            THE END (tool learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "grounding_54321" to Scene(
        text = """
            Grounding: 5–4–3–2–1
            
            Name:
            5 things you can see
            4 things you can feel
            3 things you can hear
            2 things you can smell
            1 thing you can taste
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    ),

    "breathing_box" to Scene(
        text = """
            Box breathing (4–4–4–4)
            
            In 4
            Hold 4
            Out 4
            Hold 4
            
            Repeat 3–5 rounds.
            
            THE END (skill learned).
        """.trimIndent(),
        choices = listOf(
            Choice("Return to Map", "Return to Map"),
            Choice("Change day/weather", "start")
        )
    )
)

val mapSceneIds = setOf(
    "map_sunny",
    "map_cloudy",
    "map_rainy",
    "map_windy"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfHelpApp() {
    var currentSceneId by rememberSaveable { mutableStateOf("start") }
    var lastMapSceneId by rememberSaveable { mutableStateOf("start") }

    val currentScene = story[currentSceneId] ?: story["start"]!!

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SelfHelp") },
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
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = currentScene.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                currentScene.choices.forEach { choice ->
                    Button(
                        onClick = {
                            when (choice.nextSceneId) {
                                "Return to Map" -> {
                                    currentSceneId = if (lastMapSceneId in story) {
                                        lastMapSceneId
                                    } else {
                                        "start"
                                    }
                                }

                                else -> {
                                    if (choice.nextSceneId in mapSceneIds) {
                                        lastMapSceneId = choice.nextSceneId
                                    }
                                    currentSceneId = choice.nextSceneId
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
                        currentSceneId = "start"
                        lastMapSceneId = "start"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restart")
                }
            }
        }
    }
}