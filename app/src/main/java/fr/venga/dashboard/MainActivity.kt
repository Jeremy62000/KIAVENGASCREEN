package fr.venga.dashboard

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebView

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var bridge: Bridge
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        web = WebView(this)
        bridge = Bridge(this, web)
        web.apply {
            setBackgroundColor(Color.parseColor("#0f1319"))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.blockNetworkLoads = true // 100 % hors ligne
            addJavascriptInterface(bridge, "VengaNative")
            loadUrl("file:///android_asset/index.html")
        }
        setContentView(web)
        hideBars()
    }
    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, res: IntArray) {
        super.onRequestPermissionsResult(code, perms, res)
        web.evaluateJavascript("window.__gpsStart&&window.__gpsStart()", null)
    }
    override fun onDestroy() { bridge.stopGps(); super.onDestroy() }
    private fun hideBars() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
    override fun onWindowFocusChanged(f: Boolean) { super.onWindowFocusChanged(f); if (f) hideBars() }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { if (web.canGoBack()) web.goBack() else super.onBackPressed() }
}
