package com.project.heyboardgame.main.recommend

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.GroupMatchData
import com.project.heyboardgame.databinding.FragmentRecommendBinding
import com.project.heyboardgame.main.MainViewModel
import kotlin.math.round
import kotlin.math.sqrt


class RecommendFragment : Fragment(), SensorEventListener {
    // 뒤로 가기 이벤트를 위한 변수들
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // shaking 감지를 위한 변수들
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var accelerometer: Sensor? = null
    private var isSensorRegistered = false // SensorEventListener 등록 여부를 저장하는 변수
    private var lastVibrationTime = 0L // 진동을 마지막으로 감지한 시간을 저장하는 변수
    private val vibrationThreshold = 3000 // 진동을 다시 감지하기 전의 시간 간격 (밀리초)
    // 위치 정보를 얻기 위한 변수들
    private lateinit var locationManager : LocationManager
    private var longitude : Double = 0.0
    private var latitude : Double = 0.0


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

        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recommendFragment_to_addFriendFragment)
        }

        binding.recordListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recommendFragment_to_recListFragment)
        }

        getMyLocation()
    }

    private val gpsLocationListener = LocationListener { location ->
        longitude = location.longitude
        latitude = location.latitude
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                Toast.makeText(requireContext(), "위치 접근 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                getMyLocation()
            } else
                Toast.makeText(requireContext(), "위치 접근 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
        }

    private fun getMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            showLocationAllowedDialog()
        } else {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                longitude = location.longitude
                latitude = location.latitude
            }

            // 위치 정보를 원하는 시간, 거리마다 갱신하는 코드
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, gpsLocationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, gpsLocationListener)
        }
    }

    private fun showLocationAllowedDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("위치 권한을 허용해 주세요.")
            .setMessage("주변 친구와 매칭하려면 위치 권한이 필요합니다. '확인'버튼을 누르신 후 위치 권한을 허용해 주세요. '거부'를 누르시면 해당 기능 사용에 제한이 있을 수 있습니다.")
            .setPositiveButton("확인") { _, _ ->
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("거부") { _, _ ->
                Toast.makeText(requireContext(), "위치 접근 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }

    private fun requestGroupMatch(latitude: Double, longitude: Double) {
        val groupMatchData = GroupMatchData(latitude, longitude)
        binding.loading.visibility = View.VISIBLE
        mainViewModel.requestGroupMatch(groupMatchData,
            onSuccess = {
                binding.loading.visibility = View.GONE
                val action = RecommendFragmentDirections.actionRecommendFragmentToMatchFragment(it)
                findNavController().navigate(action)
            },
            onFailure = {
                binding.loading.visibility = View.GONE
                if (it == 400) {
                    Toast.makeText(requireContext(), "최대 10명까지만 매칭 가능합니다.", Toast.LENGTH_SHORT).show()
                } else if (it == 404) {
                    Toast.makeText(requireContext(), "주변에 매칭 가능한 친구가 감지되지 않습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                }
            },
            onErrorAction = {
                binding.loading.visibility = View.GONE
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )
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

    // 흔들림 감지하는 함수
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        if (acceleration > 10 && System.currentTimeMillis() - lastVibrationTime > vibrationThreshold) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                showLocationAllowedDialog()
            } else {
                requestGroupMatch(roundToTwoDecimals(latitude), roundToTwoDecimals(longitude))
                lastVibrationTime = System.currentTimeMillis()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
