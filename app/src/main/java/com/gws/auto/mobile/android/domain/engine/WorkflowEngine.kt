package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.domain.model.Module
import timber.log.Timber
import javax.inject.Inject

class WorkflowEngine @Inject constructor(
    private val moduleExecutors: Map<String, @JvmSuppressWildcards ModuleExecutor>
) {

    suspend fun execute(modules: List<Module>) {
        val variables = mutableMapOf<String, Any>()
        for (module in modules) {
            val context = ExecutionContext(module, variables)
            val executor = moduleExecutors[module.type]

            if (executor == null) {
                Timber.e("No executor found for module type: ${module.type}")
                continue
            }

            try {
                val result = executor.execute(context)
                if (!result.isSuccess) {
                    Timber.e("Module execution failed: ${result.outputMessage}")
                    // Optionally, you can decide to stop the workflow here
                    break
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception during module execution: ${module.type}")
                break // Stop workflow on critical error
            }
        }
    }
}
