package com.bigstark.example.composable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class ComposableClickLogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleScreen()
        }
    }

    @Composable
    fun ExampleScreen() {
        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(
                            color = Color.Green,
                            shape = CircleShape
                        )
                        .clickable {
                            toast()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Click Me!")
                }
            }
        }
    }

    private fun toast() {
        Toast.makeText(this, "Hello World droid knights!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, ComposableClickLogActivity::class.java)
        }
    }
}