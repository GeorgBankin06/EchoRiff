package com.echoriff.echoriff.common.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.echoriff.echoriff.radio.domain.Category
import com.echoriff.echoriff.radio.domain.Radio
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_role")

class UserPreferences(private val context: Context) {

    private val ROLE_KEY = stringPreferencesKey("user_role")

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
}