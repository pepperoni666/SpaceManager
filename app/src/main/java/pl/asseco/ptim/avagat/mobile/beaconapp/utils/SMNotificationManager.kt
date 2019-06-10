package pl.asseco.ptim.avagat.mobile.beaconapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.MainActivity

class SMNotificationManager(private val context:Context) {

    val CHANNEL_ID: String = "SpaceManager_notification_channel1"
    val NOTIFICATION_ID: Int = 222333

    init {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = CHANNEL_ID
            // The user-visible name of the channel.
            val name = "ApplicationStatus"
            // The user-visible description of the channel.
            val description = "Displays application status"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.setSound(null, null)
            mChannel.enableVibration(false)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun getNotification(): Notification{
        val pendingIntent =
            PendingIntent.getActivity(context.applicationContext, 123, Intent(context.applicationContext, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) //your app icon
            .setContentTitle("Space Manager is running in the background")
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            //.setContentText("contect text")
            //                .setStyle(new NotificationCompat.BigTextStyle()
            //                        .bigText("text\ntext\ntexttext"))
            .setWhen(System.currentTimeMillis())

        return notificationBuilder.build()
    }
}