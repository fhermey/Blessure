package com.xeflo.blessure

import android.media.AudioAttributes

import android.app.NotificationChannel

import android.app.NotificationManager

import android.os.Build.VERSION_CODES

import android.os.Build.VERSION

import android.graphics.BitmapFactory

import androidx.core.content.ContextCompat

import androidx.core.app.NotificationCompat

import android.media.RingtoneManager

import android.graphics.Bitmap

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context

import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.databinding.DataBindingUtil.setContentView
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.net.URL


class BlessureFirebaseMessagingService() : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("Notification", "From : " + remoteMessage.getFrom())
        Log.e("Notification", "Data : " + remoteMessage.getData().toString())
        val type: String? = remoteMessage.data.get("notificationType")
        try {
            sendNotification(remoteMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    @Throws(UnsupportedEncodingException::class)
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title: String
        val message: String
        title = "" + remoteMessage.getData().get("title")
        message = "" + remoteMessage.getData()
            .get("body")

        // Pending intent will perform redirection on clicking Notification
        val intent = Intent(this, BlessureActivity::class.java)
        intent.putExtra("fromNotification", true)
        val contentIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                    or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        val channelId = "your_app_notificaiton_channel_id"
        val channelName: CharSequence = "Your App Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.setShowBadge(true)
        assert(notificationManager != null)
        notificationManager!!.createNotificationChannel(notificationChannel)

        // Let's create notification to be displayed
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setColor(
                getColor(R.color.colorPrimary)
            )
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        notificationManager.notify(
            System.currentTimeMillis().toInt() /* ID of notification */,
            builder.build()
        )
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("BlessaureFirebaseMessagingService", "Refreshed token: $token")

        /*if (bloodPressureService != null) {
            bloodPressureService!!.setFirebaseToken(token)
        }*/
    }
}