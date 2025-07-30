package com.motgolla.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.domain.departmentstore.api.repository.DepartmentStoreRepository
import com.motgolla.domain.departmentstore.data.DepartmentStoreResponse
import com.motgolla.util.PreferenceUtil
import com.google.android.gms.location.*
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "LocationViewModel"
        private const val LOCATION_CHANGE_DISTANCE = 500f  // 위치 변화 감지 임계 거리 (미터)
        private const val NEARBY_DISTANCE_METERS = 500f   // 근처 백화점 거리 임계값 (미터)
    }

    //위치서비스 처리 및 서버에서 백화점 데이터 조회
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val repository = DepartmentStoreRepository(getApplication())

    private val _lastKnownLocation = mutableStateOf<Location?>(null)
    val lastKnownLocation: State<Location?> = _lastKnownLocation

    //백화점 기본 값
    private val _departmentName = mutableStateOf("현대백화점 압구정본점")
    val departmentName: State<String> = _departmentName

    private val _locationChanged = mutableStateOf(false)
    val locationChanged: State<Boolean> = _locationChanged

    private val _isManualSelection = mutableStateOf(false)
    val isManualSelection: State<Boolean> = _isManualSelection

    private var previousLocation: Location? = null
    private var pendingDepartmentName: String? = null
    private var isFetchingStore = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            handleLocationChange(location)
        }
    }

    init {
        val savedName = PreferenceUtil.getDepartmentName(application)
        _departmentName.value = savedName ?: "현대백화점 압구정본점"

        _isManualSelection.value = PreferenceUtil.getManualSelection(application)

        if (savedName == null) {
            PreferenceUtil.saveDepartmentName(application, _departmentName.value)
            PreferenceUtil.saveManualSelection(application, false)
        }
    }


    fun selectDepartmentManually(name: String) {
        _departmentName.value = name
        _isManualSelection.value = true
        PreferenceUtil.saveDepartmentName(getApplication(), name)
        PreferenceUtil.saveManualSelection(getApplication(), true)
        resetPendingState()
        Log.d(TAG, "수동으로 백화점 선택됨: $name")
    }

    private fun handleLocationChange(newLocation: Location) {
        if (_isManualSelection.value) {
            Log.d(TAG, "수동 선택 모드 - 위치 변경 무시")
            return
        }

        if (isFetchingStore) {
            Log.d(TAG, "서버 호출 중이므로 위치 변경 처리 중복 방지")
            return
        }

        val changed = previousLocation?.let { prev ->
            newLocation.distanceTo(prev) > LOCATION_CHANGE_DISTANCE
        } ?: true

        if (changed) {
            Log.d(TAG, "위치 변화 감지: ${newLocation.latitude}, ${newLocation.longitude}")
            isFetchingStore = true
            fetchNearestStoreFromServer(newLocation)
            previousLocation = newLocation
        }
    }

    fun initPreviousLocation(onComplete: () -> Unit = {}) {
        if (!checkLocationPermission()) {
            Log.d(TAG, "위치 권한 없음")
            onComplete()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                previousLocation = location
                Log.d(TAG, "초기 위치 설정: ${location.latitude}, ${location.longitude}")
            } else {
                Log.d(TAG, "초기 위치 없음")
            }
            onComplete()
        }.addOnFailureListener {
            Log.d(TAG, "초기 위치 가져오기 실패: ${it.message}")
            onComplete()
        }
    }

    fun startLocationUpdates() {
        if (!checkLocationPermission()) {
            Log.d(TAG, "위치 권한 없음")
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMinUpdateDistanceMeters(10f)
            .build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, null)
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationPermission(): Boolean {
        val context = getApplication<Application>()
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun resetPendingState() {
        pendingDepartmentName = null
        _locationChanged.value = false
    }

    private fun fetchNearestStoreFromServer(location: Location, forceUpdate: Boolean = false) {
        viewModelScope.launch {
            try {
                val store = repository.fetchNearestStore(location.latitude, location.longitude)

                if (store != null) {
                    Log.d(TAG, "서버에서 받은 백화점: ${store.name}, 거리: ${store.distance}m")

                    if (store.distance <= NEARBY_DISTANCE_METERS) {
                        _lastKnownLocation.value = location
                        val oldDeptName = _departmentName.value

                        if (forceUpdate) {
                            _departmentName.value = store.name
                            PreferenceUtil.saveDepartmentName(getApplication(), store.name)
                            _isManualSelection.value = false
                            resetPendingState()
                            Log.d(TAG, "강제 GPS 업데이트로 백화점 이름 갱신: ${store.name}")
                        } else {
                            if (store.name != oldDeptName) {
                                pendingDepartmentName = store.name
                                _locationChanged.value = true
                                Log.d(TAG, "백화점 변경 감지됨, 안내문 표시")
                            } else {
                                resetPendingState()
                                Log.d(TAG, "백화점 동일, 안내문 표시 안함")
                            }
                        }
                    } else {
                        resetPendingState()
                        Log.d(TAG, "백화점 근처 아님")
                    }
                } else {
                    resetPendingState()
                    Log.d(TAG, "서버에서 백화점 정보 가져오기 실패")
                }

            } catch (e: Exception) {
                Log.e(TAG, "서버 호출 중 오류 발생", e)
                resetPendingState()
            } finally {
                isFetchingStore = false
            }
        }
    }

    fun checkIfLocationChangedOnEnter() {
        val currentLoc = previousLocation ?: return

        viewModelScope.launch {
            try {
                val store = repository.fetchNearestStore(currentLoc.latitude, currentLoc.longitude)
                val savedDept = departmentName.value

                if (store != null && store.distance <= NEARBY_DISTANCE_METERS && store.name != savedDept) {
                    _locationChanged.value = true
                    pendingDepartmentName = store.name
                    Log.d(TAG, "백화점 변경 + 근처 => 안내문구 띄움 ($savedDept -> ${store.name})")
                } else {
                    resetPendingState()
                    Log.d(TAG, "백화점 변경 없음 또는 반경 밖")
                }
            } catch (e: Exception) {
                Log.e(TAG, "진입시 위치 변경 체크 중 오류", e)
                resetPendingState()
            }
        }
    }

    fun forceUpdateFromGPS() {
        Log.d(TAG, "모달에서 GPS로 강제 백화점 재탐색 실행")

        if (!checkLocationPermission()) {
            Log.d(TAG, "위치 권한 없음 - forceUpdateFromGPS")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d(TAG, "GPS 위치 가져옴 (강제): ${location.latitude}, ${location.longitude}")
                previousLocation = location
                _isManualSelection.value = false
                PreferenceUtil.saveManualSelection(getApplication(), false)
                fetchNearestStoreFromServer(location, forceUpdate = true) // 즉시 서버 재조회
            } else {
                Log.d(TAG, "위치 가져오기 실패 - 위치 null")
            }
        }.addOnFailureListener {
            Log.e(TAG, "위치 가져오기 오류: ${it.message}")
        }
    }

    fun useDefaultLocationAndStore() {
        if (_isManualSelection.value) {
            Log.d(TAG, "수동 선택 모드 - 기본 백화점 세팅 생략")
            return
        }

        val defaultName = "현대백화점 압구정본점"
        _departmentName.value = defaultName
        _isManualSelection.value = false
        _lastKnownLocation.value = null

        PreferenceUtil.saveDepartmentName(getApplication(), defaultName)
        PreferenceUtil.saveManualSelection(getApplication(), false)

        Log.d(TAG, "위치 권한 거부됨. 기본 백화점 설정: $defaultName")
    }
}