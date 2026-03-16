package com.example.selfhelp

import android.content.Context
import org.json.JSONObject

fun loadStoryFromAssets(context: Context): Map<String, Scene> {
    val jsonString = context.assets.open("story.json")
        .bufferedReader()
        .use { it.readText() }

    val jsonObject = JSONObject(jsonString)
    val storyMap = mutableMapOf<String, Scene>()

    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val sceneId = keys.next()
        val sceneObject = jsonObject.getJSONObject(sceneId)

        val text = sceneObject.getString("text")
        val choicesArray = sceneObject.getJSONArray("choices")

        val choices = mutableListOf<Choice>()
        for (i in 0 until choicesArray.length()) {
            val choiceObject = choicesArray.getJSONObject(i)
            choices.add(
                Choice(
                    text = choiceObject.getString("text"),
                    next = choiceObject.getString("next")
                )
            )
        }

        storyMap[sceneId] = Scene(
            text = text,
            choices = choices
        )
    }

    return storyMap
}