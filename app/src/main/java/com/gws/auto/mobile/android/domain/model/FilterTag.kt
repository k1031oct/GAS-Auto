package com.gws.auto.mobile.android.domain.model

data class FilterTag(
    override val displayName: String,
    val type: FilterType
) : DisplayTag {
    override val isFilter: Boolean = true
}

enum class FilterType {
    FAVORITE,
    BOOKMARK
}
