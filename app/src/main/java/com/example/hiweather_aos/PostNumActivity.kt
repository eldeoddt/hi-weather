package com.example.hiweather_aos

import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.databinding.ActivityPostNumBinding

class PostNumActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostNumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostNumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView: WebView = binding.locationSearchWebview

        webView.clearCache(true)
        webView.settings.javaScriptEnabled = true

        webView.addJavascriptInterface(BridgeInterface(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:sample2_execDaumPostcode();")
            }
        }

        webView.loadUrl("https://mobilefinal-85898.web.app")
    }

    inner class BridgeInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        fun processDATA(fullRoadAddr: String, jibunAddr: String) {
            runOnUiThread {
                binding.tvFullRoadAddr.text = fullRoadAddr
                binding.tvJibunAddr.text = jibunAddr
                Log.d("postlog", jibunAddr)
                Log.d("postlog", fullRoadAddr)
            }
        }
    }
}
