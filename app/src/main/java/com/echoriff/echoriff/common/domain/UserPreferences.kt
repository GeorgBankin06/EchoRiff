package com.echoriff.echoriff.common.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.echoriff.echoriff.radio.domain.Recording
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_role")

class UserPreferences(private val context: Context) {
    private val prefs = context.getSharedPreferences("recordings", Context.MODE_PRIVATE)

    private val ROLE_KEY = stringPreferencesKey("user_role")
    private val recordKey = "record_list"

    private val PREF_NAME = "played_radios_prefs"
    private val KEY_PLAYED_RADIOS = "last_played_radios"
    private val MAX_SIZE = 10
    private val gson = Gson()

    fun saveRadio(radio: Radio) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val radios = getRadios().toMutableList()

        // Remove if it already exists
        radios.removeAll { it.title == radio.title }

        // Add to start
        radios.add(0, radio)

        // Limit size to 10
        val trimmed = radios.take(MAX_SIZE)

        // Save back
        val json = gson.toJson(trimmed)
        prefs.edit().putString(KEY_PLAYED_RADIOS, json).apply()
    }

    fun getRadios(): List<Radio> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PLAYED_RADIOS, null) ?: return emptyList()
        val type = object : TypeToken<List<Radio>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_PLAYED_RADIOS).apply()
    }

    fun saveRecord(record: Recording) {
        val recordJson = JSONObject().apply {
            put("name", record.fileName)
            put("path", record.filePath)
            put("date", record.date)
            put("duration", record.duration)
        }

        val current =
            prefs.getStringSet(recordKey, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        current.add(recordJson.toString())
        prefs.edit().putStringSet(recordKey, current).apply()
    }

    fun deleteRecordByPath(filePath: String) {
        val current =
            prefs.getStringSet(recordKey, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val updated = current.filterNot {
            val json = JSONObject(it)
            json.optString("path") == filePath
        }.toMutableSet()

        prefs.edit().putStringSet(recordKey, updated).apply()
        // Also delete the file from disk
        val file = File(filePath)
        if (file.exists()) file.delete()
    }

    fun loadRecordings(context: Context): List<Recording> {
        val prefs = context.getSharedPreferences("recordings", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("record_list", emptySet()) ?: emptySet()

        return set.mapNotNull {
            try {
                val json = JSONObject(it)
                Recording(
                    fileName = json.getString("name"),
                    filePath = json.getString("path"),
                    date = json.getString("date"),
                    duration = json.getString("duration")
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    val userRole: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ROLE_KEY]
        }

    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role
        }
    }

    suspend fun clearUserRole() {
        context.dataStore.edit { preferences ->
            preferences.remove(ROLE_KEY)
        }
    }

    fun saveLastPlayedRadioWithCategory(context: Context, radio: Radio, category: Category) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Serialize the Radio and Category objects to JSON
        val radioJson = gson.toJson(radio)
        val categoryJson = gson.toJson(category)

        // Save them in SharedPreferences
        editor.putString("last_played_radio", radioJson)
        editor.putString("last_played_category", categoryJson)
        editor.apply()
    }

    fun getLastPlayedRadioWithCategory(context: Context): Pair<Radio?, Category?> {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val radioJson = sharedPreferences.getString("last_played_radio", null)
        val categoryJson = sharedPreferences.getString("last_played_category", null)

        val gson = Gson()

        // Deserialize the JSON strings back into Radio and Category objects
        val radio = if (radioJson != null) gson.fromJson(radioJson, Radio::class.java) else null
        val category =
            if (categoryJson != null) gson.fromJson(categoryJson, Category::class.java) else null

        return Pair(radio, category)
    }

    fun clearLastPlayedRadio() {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            remove("last_played_radio")
            remove("last_played_category")
            apply()
        }
    }

    fun saveSelectedCategory(categoryId: String) {
        val sharedPreferences = context.getSharedPreferences("EchoRiffPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("LAST_SELECTED_CATEGORY", categoryId).apply()
    }

    fun getSelectedCategory(): String? {
        val sharedPreferences = context.getSharedPreferences("EchoRiffPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("LAST_SELECTED_CATEGORY", null)
    }

    fun clearSelectedCategory() {
        val sharedPreferences = context.getSharedPreferences("EchoRiffPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("LAST_SELECTED_CATEGORY").apply()
    }
}