package com.example.idsign

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.idsign.AppViewModelProvider.Factory
import com.example.idsign.viewModel.NetworkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Loading()

            val intent: Intent = getIntent()
            val id: String? = intent.getStringExtra("ID")
            val networkViewModel = ViewModelProvider(this, Factory).get(NetworkViewModel::class.java)

            if (!isInternetAvailable(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
                return@setContent
            }

            val hash by networkViewModel.hash.collectAsStateWithLifecycle()

            if (id == "Signer") {
                networkViewModel.getHash("signerapp@hcecard.com")
            } else if (id == "Signee") {
                networkViewModel.getHash("signeeapp@hcereader.com")
            }

            LaunchedEffect(hash) {
                if (hash.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            networkViewModel.getPrivateKey(hash)
                        } catch (e: Exception) {
                            handleNetworkError(e)
                        }
                    }
                }
            }

            val publicKey by networkViewModel.publicKey.collectAsStateWithLifecycle()

            if (publicKey.isNotEmpty() && hash.isNotEmpty()) {
                Log.d("hash", hash)
                Log.d("PUBLIC KEY", publicKey)

                if (id == "Signer") {
                    val intent = Intent(this, SignerActivity::class.java)
                    intent.putExtra("privateKey", publicKey)
                    intent.putExtra("hash", hash)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else if (id == "Signee") {
                    val intent = Intent(this, SigneeActivity::class.java)
                    intent.putExtra("privateKey", publicKey)
                    intent.putExtra("hash", hash)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun handleNetworkError(e: Exception) {
        when (e) {
            is java.net.UnknownHostException -> {
                Toast.makeText(this, "Could not connect to server. Check your network.", Toast.LENGTH_LONG).show()
            }
            is java.net.SocketTimeoutException -> {
                Toast.makeText(this, "Connection timed out. Check if the server is reachable.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.primary,
            strokeWidth = 4.dp
        )
    }
}
