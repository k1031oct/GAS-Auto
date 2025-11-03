package com.gws.auto.mobile.android.domain.model

/**
 * A collection of modules to be executed sequentially.
 */
data class Workflow(
    val id: String,
    val name: String,
    val description: String,
    val modules: List<Module>
)
