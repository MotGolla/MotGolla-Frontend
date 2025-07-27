package com.motgolla.domain.notification.api.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.motgolla.MainActivity
import com.motgolla.R
import kotlin.apply
import kotlin.jvm.java
import kotlin.let

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_SERVICE", "onMessageReceived called")
        Log.d("FCM_SERVICE", "From: ${remoteMessage.from}")
        Log.d("FCM_SERVICE", "Data: ${remoteMessage.data}")

        remoteMessage.notification?.let {
            Log.d("FCM_SERVICE", "Notification Title: ${it.title}")
            Log.d("FCM_SERVICE", "Notification Body: ${it.body}")
            showNotification(it.title ?: "알림", it.body ?: "")
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = CHANNEL_ID

        // 채널 생성 (중요도 HIGH, 헤드업 가능)
        createNotificationChannel(notificationManager, channelId)

        // PendingIntent 생성 (알림 클릭 시 열릴 액티비티)
        val pendingIntent = createPendingIntent()

        // Notification 빌드
        val notification = buildNotification(title, message, channelId, pendingIntent)

        // 알림 표시 (매번 다른 ID를 주는 게 중복 안 생김)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "기본 채널", NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildNotification(
        title: String,
        message: String,
        channelId: String,
        pendingIntent: PendingIntent
    ): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 헤드업 알림을 위한 중요도
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // 소리, 진동 등 기본 설정
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "default_channel"
    }
}
