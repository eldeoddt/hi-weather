package com.example.hiweather_aos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hiweather_aos.databinding.FragmentMapBinding
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.geometry.LatLng

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var myNaverMap: NaverMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 네이버 지도 API 키 설정
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient("bjnkdg8cfh")

        // 네이버 지도 객체 가져오기
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        myNaverMap = naverMap

        // 특정 좌표에 마커 추가
        val marker = Marker()
        marker.position = LatLng(37.5666102, 126.9783881) // 예시 좌표 (서울시청)
        marker.map = naverMap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
