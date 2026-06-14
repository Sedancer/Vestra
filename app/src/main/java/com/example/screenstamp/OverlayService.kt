package com.example.screenstamp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import java.io.File

class OverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: ImageView? = null
    private lateinit var prefs: PrefsManager
    private val handler = Handler(Looper.getMainLooper())
    private var showRunnable: Runnable? = null

    companion object {
        const val CHANNEL_ID = "OverlayServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vestra Active")
            .setContentText("Overlay is running")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
            
        startForeground(NOTIFICATION_ID, notification)

        when (intent?.action) {
            ACTION_START -> scheduleShowOverlay()
            ACTION_STOP -> stopSelf()
            else -> scheduleShowOverlay() // Default
        }

        return START_STICKY
    }

    private fun scheduleShowOverlay() {
        showRunnable?.let { handler.removeCallbacks(it) }
        val delayMs = prefs.timerDelay * 1000L
        val runnable = Runnable { showOverlay() }
        showRunnable = runnable
        handler.postDelayed(runnable, delayMs)
    }

    private fun showOverlay() {
        if (overlayView != null) {
            windowManager?.removeView(overlayView)
        }

        val imagePath = prefs.imagePath
        if (imagePath.isNullOrEmpty() || !File(imagePath).exists()) {
            return
        }

        val bitmap = BitmapFactory.decodeFile(imagePath)

        overlayView = ImageView(this).apply {
            setImageBitmap(bitmap)
            scaleType = ImageView.ScaleType.FIT_XY
            
            var clickCount = 0
            var lastClickTime = 0L

            setOnClickListener {
                val currentTime = System.currentTimeMillis()
                val interval = prefs.hideClickInterval
                
                if (currentTime - lastClickTime <= interval) {
                    clickCount++
                } else {
                    clickCount = 1
                }
                lastClickTime = currentTime

                if (clickCount >= prefs.hideClickCount) {
                    stopSelf()
                    clickCount = 0
                }
            }
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            prefs.width,
            prefs.height,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.RGBA_8888
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.x
            y = prefs.y
            alpha = 1.0f
        }

        windowManager?.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (e: Exception) {
                // View might not be attached
            }
            overlayView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "ScreenStamp Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}
