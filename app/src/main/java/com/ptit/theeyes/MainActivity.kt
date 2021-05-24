package com.ptit.theeyes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ptit.theeyes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}