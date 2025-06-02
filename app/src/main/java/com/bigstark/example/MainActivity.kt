package com.bigstark.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bigstark.example.composable.ComposableClickLogActivity
import com.bigstark.example.count.ClickCountActivity
import com.bigstark.example.log.ClickLogActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    Button(onClick = {
                        navigateToClickCount()
                    }) {
                        Text(text = "Navigate to Click Count")
                    }

                    Button(onClick = {
                        navigateToClickLog()
                    }) {
                        Text(text = "Navigate to Click Log")
                    }

                    Button(onClick = {
                        navigateToComposableClickLog()
                    }) {
                        Text(text = "Navigate to Composable Click Log")
                    }
                }
            }
        }
    }

    private fun navigateToClickCount() {
        startActivity(ClickCountActivity.intent(this))
    }

    private fun navigateToClickLog() {
        startActivity(ClickLogActivity.intent(this))
    }

    private fun navigateToComposableClickLog() {
        startActivity(ComposableClickLogActivity.intent(this))
    }
}