package com.example.medinahotel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.medinahotel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.signupBtn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this,HotelActivity::class.java))
        }
    }
}