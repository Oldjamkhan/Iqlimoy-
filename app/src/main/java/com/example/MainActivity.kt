package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.IqlimoyApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.IqlimoyViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val iqlimoyViewModel: IqlimoyViewModel = viewModel()
        IqlimoyApp(
            viewModel = iqlimoyViewModel,
            modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
