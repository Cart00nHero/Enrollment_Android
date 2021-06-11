package com.cartoonhero.source.enrollment_android.scene.formWebView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.R
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class WebViewFragment: Fragment() {
    private val scenario = WebViewScenario()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_webview,container,false)
    }

    override fun onStart() {
        super.onStart()
        scenario.toBeCollectUrl {
            this.form_webView.loadUrl(it)
        }
    }

}