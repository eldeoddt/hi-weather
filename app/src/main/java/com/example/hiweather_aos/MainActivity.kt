package com.example.hiweather_aos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.hiweather_aos.databinding.ActivityMainBinding
import com.example.hiweather_aos.post.PostFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val bottomNavigationView = binding.bottomNavigation

        val fragments = listOf(
            HomeFragment(),
            CalenderFragment(),
            PostFragment(),
            SettingFragment()
        )

        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_calendar -> viewPager.currentItem = 1
                R.id.nav_post -> viewPager.currentItem = 2
                R.id.nav_settings -> viewPager.currentItem = 3
            }
            true
        }
    }
}

class ViewPagerAdapter(
    activity: AppCompatActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(activity) {
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}