package com.example.medinahotel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.medinahotel.databinding.ActivityHotelBinding


class HotelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHotelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}