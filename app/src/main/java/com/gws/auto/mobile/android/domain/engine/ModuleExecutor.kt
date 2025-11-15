package com.gws.auto.mobile.android.domain.engine

/**
 * An interface for a single executable module.
 */
interface ModuleExecutor {
    suspend fun execute(context: ExecutionContext): ExecutionResult
}

/**
 * Represents the result of a module's execution.
 */
data class ExecutionResult(
    val isSuccess: Boolean,
    val outputMessage: String? = null
)
