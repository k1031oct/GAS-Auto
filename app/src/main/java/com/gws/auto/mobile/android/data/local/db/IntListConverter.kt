package com.gws.auto.mobile.android.data.local.db

import androidx.room.TypeConverter

class IntListConverter {
    @TypeConverter
    fun fromString(stringListString: String): List<Int> {
        return stringListString.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun toString(intList: List<Int>): String {
        return intList.joinToString(separator = ",")
    }
}
