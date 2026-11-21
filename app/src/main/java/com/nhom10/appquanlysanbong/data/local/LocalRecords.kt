package com.nhom10.appquanlysanbong.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

// Lớp lưu trữ cho bản demo. Các nghiệp vụ nhận dữ liệu qua repository của từng tính năng.
internal class LocalRecords(context: Context) {
    private val prefs = context.getSharedPreferences("court_demo_v1", Context.MODE_PRIVATE)

    fun <T> read(key: String, decode: (JSONObject) -> T): List<T> {
        val json = JSONArray(prefs.getString(key, "[]"))
        return List(json.length()) { decode(json.getJSONObject(it)) }
    }

    fun <T> write(key: String, records: List<T>, encode: (T) -> JSONObject) {
        val json = JSONArray()
        records.forEach { json.put(encode(it)) }
        prefs.edit().putString(key, json.toString()).apply()
    }
}

internal fun json(vararg fields: Pair<String, Any?>) =
    JSONObject().apply { fields.forEach { (key, value) -> put(key, value ?: JSONObject.NULL) } }
