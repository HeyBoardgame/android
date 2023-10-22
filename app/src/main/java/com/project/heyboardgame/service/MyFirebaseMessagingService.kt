package com.project.heyboardgame.service


import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.heyboardgame.App
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.main.MainActivity
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()

    // 새로운 토큰이 생성 될 때 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        runBlocking { myDataStore.setFcmToken(token) }
        Timber.d("FCM token : $token")
    }

    // 메세지가 수신되면 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationAllowed = runBlocking { myDataStore.getNotificationAllowed() }

        if (notificationAllowed) {
            val pushAlarmType = remoteMessage.data["pushAlarmType"]
            val id = remoteMessage.data["id"]
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]

            when (pushAlarmType) {
                "chatting" -> { // 채팅 관련 알림
                    val imageUrl = remoteMessage.data["image"]
                    val profileImg : String?
                    if (imageUrl == null) {
                        profileImg = null
                    } else {
                        profileImg = imageUrl.toString()
                    }
                    val friendInfo = Friend(id = id!!.toInt(), image = profileImg, nickname = title!!)
                    val bundle = bundleOf("friend" to friendInfo)

                    val pendingIntent = NavDeepLinkBuilder(App.getContext())
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.main_nav)
                        .setDestination(R.id.chatFragment)
                        .setArguments(bundle)
                        .createPendingIntent()

                    val notificationManager = App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val builder = NotificationCompat.Builder(App.getContext(), "chatting")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    val notificationId = 1
                    notificationManager.notify(notificationId, builder.build())
                }
                "friendRequest" -> { // 친구 요청 수신 알림
                    val pendingIntent = NavDeepLinkBuilder(App.getContext())
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.main_nav)
                        .setDestination(R.id.socialFragment)
                        .setArguments(bundleOf())
                        .createPendingIntent()

                    val notificationManager = App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val builder = NotificationCompat.Builder(App.getContext(), "friend")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    val notificationId = 2
                    notificationManager.notify(notificationId, builder.build())
                }
                "friendAccept" -> { // 친구 요청 수락 알림
                    val pendingIntent = NavDeepLinkBuilder(App.getContext())
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.main_nav)
                        .setDestination(R.id.socialFragment)
                        .setArguments(bundleOf())
                        .createPendingIntent()

                    val notificationManager = App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val builder = NotificationCompat.Builder(App.getContext(), "friend")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    val notificationId = 3
                    notificationManager.notify(notificationId, builder.build())
                }
                "rating" -> { // 평가 요청 알림
                    val pendingIntent = NavDeepLinkBuilder(App.getContext())
                        .setComponentName(MainActivity::class.java)
                        .setGraph(R.navigation.main_nav)
                        .setDestination(R.id.profileFragment)
                        .setArguments(bundleOf())
                        .createPendingIntent()

                    val notificationManager = App.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val builder = NotificationCompat.Builder(App.getContext(), "rating")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    val notificationId = 4
                    notificationManager.notify(notificationId, builder.build())
                }
                else -> {

                }
            }
        }
    }
}