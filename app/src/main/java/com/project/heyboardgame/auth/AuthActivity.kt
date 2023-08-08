package com.project.heyboardgame.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.project.heyboardgame.databinding.ActivityAuthBinding
import com.project.heyboardgame.main.MainActivity


class AuthActivity : AppCompatActivity() {

    private val viewModel : AuthViewModel by viewModels() // viewModel
    private lateinit var binding : ActivityAuthBinding // viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkAccessToken()
        viewModel.token.observe(this, Observer {
            if(it.isEmpty()){ // 로그인이 안된 유저
                binding.authNavContainerView.visibility = View.VISIBLE
            } else { // 로그인이 되어있는 유저
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

    }


}