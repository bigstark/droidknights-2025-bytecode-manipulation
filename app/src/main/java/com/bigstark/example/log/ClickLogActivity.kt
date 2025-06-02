package com.bigstark.example.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.bigstark.example.databinding.ActivitySampleBinding

class ClickLogActivity : ComponentActivity() {

    private lateinit var binding: ActivitySampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySampleBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.button.text = "log click"
        binding.button.setOnClickListener @Loggable("clicked_btn_show_toast") {
            Log.v("TAG", "button clicked")
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, ClickLogActivity::class.java)
        }
    }
}

annotation class Loggable(val name: String)