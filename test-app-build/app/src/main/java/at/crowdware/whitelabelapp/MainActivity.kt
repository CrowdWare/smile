package at.crowdware.whitelabelapp

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
import at.crowdware.whitelabelapp.pages.SamplePage
import at.crowdware.whitelabelapp.ui.theme.WhiteLabelAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhiteLabelAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SamplePage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

