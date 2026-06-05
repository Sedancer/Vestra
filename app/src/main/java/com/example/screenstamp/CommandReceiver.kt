package com.example.screenstamp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class CommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        
        val serviceIntent = Intent(context, OverlayService::class.java)

        when (action) {
            "com.screenstamp.ACTION_SHOW" -> {
                serviceIntent.action = OverlayService.ACTION_START
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
            "com.screenstamp.ACTION_HIDE" -> {
                serviceIntent.action = OverlayService.ACTION_STOP
                context.startService(serviceIntent)
            }
        }
    }
}
