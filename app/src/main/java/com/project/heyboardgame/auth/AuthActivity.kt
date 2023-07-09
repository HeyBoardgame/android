package com.project.heyboardgame.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.project.heyboardgame.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    private val viewModel : AuthViewModel by viewModels() // viewModel
    private lateinit var binding : ActivityAuthBinding // viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}