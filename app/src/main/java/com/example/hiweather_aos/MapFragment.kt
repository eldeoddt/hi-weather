package com.example.hiweather_aos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.hiweather_aos.databinding.FragmentMapBinding
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback

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
        // 지도 사용을 위한 추가 설정 코드 작성 가능
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
