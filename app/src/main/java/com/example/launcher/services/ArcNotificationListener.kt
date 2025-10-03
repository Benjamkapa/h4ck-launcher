package com.example.launcher.services

// In app/src/main/java/com/example/launcher/services/ArcNotificationListener.kt

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

@RequiresApi(Build.VERSION_CODES.M)
class ArcNotificationListener : NotificationListenerService() {

    companion object {
        const val ACTION_SEND_NOTIFICATIONS = "com.example.launcher.SEND_NOTIFICATIONS"
        const val EXTRA_NOTIFICATIONS = "extra_notifications"
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        // When the listener starts, get all current active notifications and broadcast them
        sendCurrentNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        // A new notification has appeared, broadcast the updated list
        sendCurrentNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // A notification has been dismissed, broadcast the updated list
        sendCurrentNotifications()
    }

    private fun sendCurrentNotifications() {
        // Filter out ongoing notifications that aren't useful (like "Spotify is running")
        val activeNotifications = activeNotifications.filter {
            !it.isOngoing && it.notification.flags and Notification.FLAG_FOREGROUND_SERVICE == 0
        }


// ... (code to filter notifications) ...
        val notificationIcons = activeNotifications.mapNotNull { it.notification.smallIcon }

        // Create an Intent to broadcast the icons
        val intent = Intent(ACTION_SEND_NOTIFICATIONS)
        // We can't send Icon objects directly, so we send them in a Bundle
        val bundle = Bundle()
        bundle.putParcelableArrayList(EXTRA_NOTIFICATIONS, ArrayList(notificationIcons))
        intent.putExtras(bundle)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}