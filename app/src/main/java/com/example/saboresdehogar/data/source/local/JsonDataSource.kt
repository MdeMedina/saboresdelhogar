package com.example.saboresdehogar.data.source.local

import android.content.Context
import com.google.gson.Gson
import android.util.Log
import android.widget.Toast
import com.google.gson.reflect.TypeToken
import java.io.IOException


class JsonDataSource(private val context: Context) {

    private val gson = Gson()

    /**
     * Lee un archivo JSON desde la carpeta assets
     */

    /**
     * Lee un archivo JSON desde la carpeta assets (MODIFICADO PARA DEBUG)
     */
    fun <T> readJsonFromAssets(fileName: String, typeToken: TypeToken<T>): T? {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            gson.fromJson<T>(jsonString, typeToken.type)
        } catch (e: IOException) {
            // --- Muestra error de lectura ---
            Toast.makeText(context, "Error al leer $fileName: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace() // Mantenemos esto por si acaso
            null
        } catch (e: Exception) { // Captura JsonSyntaxException, etc.
            // --- Muestra error de parseo ---
            Toast.makeText(context, "Error al parsear $fileName: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            null
        }
    }
    /**
     * Lee un archivo JSON desde res/raw
     */
    fun <T> readJsonFromRaw(resourceId: Int, typeToken: TypeToken<T>): T? {
        return try {
            val jsonString = context.resources.openRawResource(resourceId)
                .bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, typeToken.type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Guarda datos en SharedPreferences (para carrito, sesión, etc.)
     */
    fun saveToPreferences(key: String, data: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString(key, data).apply()
    }

    /**
     * Lee datos de SharedPreferences
     */
    fun readFromPreferences(key: String): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    /**
     * Elimina datos de SharedPreferences
     */
    fun removeFromPreferences(key: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove(key).apply()
    }

    /**
     * Limpia todas las preferencias
     */
    fun clearPreferences() {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}