package com.project.heyboardgame.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel : MainViewModel by viewModels() // viewModel
    private lateinit var binding : ActivityMainBinding // viewBinding

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션 연결
        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavContainerView) as NavHostFragment
        navController = navHostFragment.navController
        // 바텀 네비게이션 연결
        binding.bottomNavView.setupWithNavController(navController)

        showHideBottomNav()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // 바텀 네비게이션을 fragment에 따라 보여주거나 숨겨주는 함수
    private fun showHideBottomNav() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 바텀 네비게이션이 표시되는 Fragment
            if(destination.id == R.id.homeFragment || destination.id == R.id.recommendFragment || destination.id == R.id.profileFragment
                || destination.id == R.id.socialFragment || destination.id == R.id.matchFragment || destination.id == R.id.addFriendFragment
                || destination.id == R.id.chatListFragment){
                binding.bottomNavView.visibility = View.VISIBLE
            }
            // 바텀 네비게이션이 표시되지 않는 Fragment
            else{
                binding.bottomNavView.visibility = View.GONE
            }
        }
    }
}