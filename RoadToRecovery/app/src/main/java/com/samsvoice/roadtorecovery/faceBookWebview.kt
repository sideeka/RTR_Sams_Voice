package com.samsvoice.roadtorecovery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class faceBookWebview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_book_webview)

        val webView=findViewById<WebView>(R.id.webView)

        webView.webViewClient=WebViewClient()

        //val url="https://ebird.org/region/saf"
        val url="https://www.facebook.com/samsvoicesa/"
        webView.loadUrl(url)


    }// end on create
}// end class