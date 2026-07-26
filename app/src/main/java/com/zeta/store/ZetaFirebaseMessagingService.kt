package com.zeta.store

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ZetaFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        getSharedPreferences(PUSH_PREFS, MODE_PRIVATE)
            .edit()
            .putString(PUSH_TOKEN_KEY, token)
            .apply()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Nuevo pedido"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Tienes un pedido nuevo para revisar."

        showPushNotification(title, body, message.data["notification_id"] ?: message.messageId.orEmpty())
    }

    private fun showPushNotification(title: String, body: String, notificationId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        ensurePushChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, PUSH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_zeta)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId.hashCode(), notification)
    }

    private fun ensurePushChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(PUSH_CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            PUSH_CHANNEL_ID,
            "Pedidos admin",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Alertas para pedidos nuevos de Zeta Dorada"
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val PUSH_PREFS = "zeta_push"
        const val PUSH_TOKEN_KEY = "fcm_token"
        const val PUSH_CHANNEL_ID = "admin_orders"
    }
}
