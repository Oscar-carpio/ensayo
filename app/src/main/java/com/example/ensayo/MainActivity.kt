package com.example.ensayo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ensayo.ui.navigation.EnsayoNavHost
import com.example.ensayo.ui.theme.EnsayoTheme
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnsayoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EnsayoNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

