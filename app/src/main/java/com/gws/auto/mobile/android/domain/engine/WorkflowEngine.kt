package com.gws.auto.mobile.android.domain.engine

import android.content.Context
import android.widget.Toast
import com.gws.auto.mobile.android.domain.model.Module
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class WorkflowEngine @Inject constructor(@ActivityContext private val context: Context) {

    suspend fun execute(modules: List<Module>) {
        for (module in modules) {
            executeModule(module)
        }
    }

    private suspend fun executeModule(module: Module) {
        when (module.type) {
            "LOG_MESSAGE" -> {
                Timber.d("Executing LOG_MESSAGE: ${module.parameters["message"]}")
            }
            "SHOW_TOAST" -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, module.parameters["message"], Toast.LENGTH_SHORT).show()
                }
            }
            // TODO: Implement other module types
        }
    }
}
