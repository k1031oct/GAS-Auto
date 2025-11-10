package com.gws.auto.mobile.android.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gws.auto.mobile.android.domain.model.Module

class ModuleListConverter {
    @TypeConverter
    fun fromString(value: String): List<Module> {
        val listType = object : TypeToken<List<Module>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Module>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}