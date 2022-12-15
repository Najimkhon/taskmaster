package com.hfad.taskmaster2.fcm


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.activities.MainActivity
import com.hfad.taskmaster2.activities.SignInActivity
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.utils.Constants
import org.checkerframework.checker.interning.qual.InternMethod

class MyFirebaseInstanceIDService:FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "FROM: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            sendNotification(message, title)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message data payload: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refresh token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?){
        //TO DO Implement
    }

    private fun sendNotification(message: String, title: String){
        val intent = if (FirestoreClass().getCurrentUserId().isNotEmpty()){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setSmallIcon(R.drawable.ic_done)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId,
                "Channel Taskmaster title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object{
        private const val TAG = "MyFirebaseMsgService"
    }

}