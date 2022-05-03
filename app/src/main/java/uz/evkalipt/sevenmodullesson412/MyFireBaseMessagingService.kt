package uz.evkalipt.sevenmodullesson412

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFireBaseMessagingService: FirebaseMessagingService() {
    private val TAG = "MyFireBaseMessagingServ"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        var id = Math.random()*1000

        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", id)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (remoteMessage.data.isNotEmpty()) {
            val builder = NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_baseline_send_24)
                .setContentTitle("New message ${remoteMessage.data["title"]}")
                .setContentText(remoteMessage.data["body"])
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.app_name)
                val descriptionText = getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                var channel = NotificationChannel("1", name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)

            }

            notificationManager.notify(id.toInt(), builder.build())

        }

    }

}