# SelfHelp

A mental health and wellness Android app built with Kotlin and Jetpack Compose. Originally prototyped on a Raspberry Pi Pico, this app helps users track their emotional wellbeing through guided storytelling, mood tracking, breathing exercises, and daily routines.

---

## Features

### Guided Story
An interactive choose-your-own-path narrative set in a metaphorical meadow with animal characters. Each choice leads through locations like the Field, the Woods, Hare's Burrow, and colour-coded Memory Rooms (Green / Amber / Red) that offer guidance on emotional states and when to seek help. A visual story map shows your progression and branching paths.

### Mood Tracker
Log how you're feeling with a quick mood check-in (Good/okay, Low/heavy, Anxious/on edge, Numb/disconnected) plus an optional note. View your full mood history with timestamps.

### Daily Routine
A customisable daily checklist pre-loaded with wellness reminders (medication, meals, water, outdoor time, etc.). Add your own tasks, mark them complete, and reset at the start of each day.

### Breathing Exercises
Three guided breathing techniques with an animated visual:
- **Box Breathing** — 4-4-4-4 for nervous system regulation
- **4-7-8 Breathing** — extended exhale for anxiety and sleep
- **Deep Breathing** — gentle 4-6 pattern for beginners

### Notes
A private journaling space to write and save timestamped notes.

### Progress Dashboard
Summary stats including latest mood, most common mood, coping technique usage, and daily routine completion rate.

### Themes & Accessibility
- Three colour themes: **Soft Lavender**, **Forest Calm**, **Dark Night**
- Large text mode (scales all text by 1.3×)
- Multi-language support: English, French, Spanish, Welsh (translations in progress)

---

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Persistence**: SharedPreferences (JSON-serialised data)
- **Min SDK**: 23 / Target SDK: 36

---

## Background

This project started as a hardware prototype using a Raspberry Pi Pico, MicroPython, and a touchscreen interface. The Android app recreates and expands on that original concept.

---

## Author

GitHub: [Awesometeapot1](https://github.com/Awesometeapot1)

## License

GPL-2.0
