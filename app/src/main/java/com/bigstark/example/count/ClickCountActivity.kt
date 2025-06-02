package com.bigstark.example.count

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.bigstark.example.databinding.ActivitySampleBinding

class ClickCountActivity : ComponentActivity() {

    private lateinit var binding: ActivitySampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySampleBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.button.text = "increase count"
        binding.button.setOnClickListener {
            Toast.makeText(this, "Hello World droid knights!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, ClickCountActivity::class.java)
        }
    }
}
