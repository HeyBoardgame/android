package com.project.heyboardgame.auth

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.project.heyboardgame.databinding.ActivityAuthBinding
import com.project.heyboardgame.main.MainActivity
import com.project.heyboardgame.utils.CustomDialog


class AuthActivity : AppCompatActivity() {

    private val viewModel : AuthViewModel by viewModels() // viewModel
    private lateinit var binding : ActivityAuthBinding // viewBinding


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkAccessToken()
        viewModel.token.observe(this, Observer {
            if (it.isEmpty()) { // 로그인이 안된 유저
                binding.authNavContainerView.visibility = View.VISIBLE
            } else { // 로그인이 되어있는 유저
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

        viewModel.checkFirstRun()
        viewModel.firstRun.observe(this, Observer {
            if (it) { // 앱 처음 실행한 유저
                createChannels()
                showPermissionDialog()

                viewModel.setIsFirstRun()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionDialog() {
        val dialog = CustomDialog(this) {
            requestPermissons()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissons() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val permissionStates = permissions.map { permission ->
            ActivityCompat.checkSelfPermission(this, permission)
        }.toTypedArray()

        if (permissionStates.any { it == PackageManager.PERMISSION_DENIED }) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun createChannels() {
        // 알림 채널 생성
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "chatting"
            val channelName = "채팅"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "friend"
            val channelName = "친구 요청"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "rating"
            val channelName = "평가"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                if (allPermissionsGranted) {
                    Toast.makeText(this, "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                    viewModel.setIsNotificationAllowed(true)
                } else {
                    Toast.makeText(this, "일부 또는 모든 권한이 거부되었습니다. 설정에서 변경하실 수 있습니다.", Toast.LENGTH_SHORT).show()

                    for (i in permissions.indices) {
                        val permission = permissions[i]
                        val grantResult = grantResults[i]

                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            when (permission) {
                                Manifest.permission.POST_NOTIFICATIONS -> {
                                    viewModel.setIsNotificationAllowed(true)
                                }
                                Manifest.permission.READ_MEDIA_IMAGES -> {
                                    // 생략
                                }
                                Manifest.permission.ACCESS_FINE_LOCATION -> {
                                    // 생략
                                }
                            }
                        } else {
                            // 해당 권한이 거부되었을 때의 처리
                            when (permission) {
                                Manifest.permission.POST_NOTIFICATIONS -> {
                                    viewModel.setIsNotificationAllowed(false)
                                }
                                Manifest.permission.READ_MEDIA_IMAGES -> {
                                    // 생략
                                }
                                Manifest.permission.ACCESS_FINE_LOCATION -> {
                                    // 생략
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}