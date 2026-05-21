package com.jnetaol.screenai

import android.app.Application
import com.jnetaol.screenai.data.db.AppDatabase
import com.jnetaol.screenai.logger.DebugLogger

class ScreenAIApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        DebugLogger.init(filesDir)
        DebugLogger.i("ScreenAIApp", "ScreenAI v1.0.1 starting")
    }
}
