package com.project.heyboardgame.main.recommend

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentRecommendBinding
import com.project.heyboardgame.main.MainActivity
import kotlin.math.sqrt


class RecommendFragment : Fragment(), SensorEventListener {

    // 뒤로 가기 이벤트를 위한 변수들
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentRecommendBinding? = null
    private val binding get() = _binding!!

    // shaking 감지를 위한 변수들
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var accelerometer: Sensor? = null
    private var isSensorRegistered = false // SensorEventListener 등록 여부를 저장하는 변수


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 화면에서 뒤로 가기를 두 번 눌렀을 때 종료시켜주는 함수
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2500) {
                    activity?.finish()
                    return
                }
                Toast.makeText(activity, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        binding.apply {
            addFriendBtn.setOnClickListener {
                findNavController().navigate(R.id.action_recommendFragment_to_addFriendFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isSensorRegistered) {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            isSensorRegistered = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSensorRegistered) {
            sensorManager?.unregisterListener(this)
            isSensorRegistered = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager?.unregisterListener(this)
        _binding = null
    }

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    // 흔들림 감지 시 matchFragment로 화면 전환해주는 함수
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        if (acceleration > 10) {
            Toast.makeText(activity, "매칭 성공!", Toast.LENGTH_SHORT).show()
            binding.apply {
                findNavController().navigate(R.id.action_recommendFragment_to_matchFragment)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
