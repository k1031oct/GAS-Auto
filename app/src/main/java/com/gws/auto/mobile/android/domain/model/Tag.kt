package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey
    val name: String
) : DisplayTag {
    override val displayName: String
        get() = name
    override val isFilter: Boolean
        get() = false
}
