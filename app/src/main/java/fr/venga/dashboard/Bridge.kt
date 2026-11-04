package fr.venga.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Base64
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/** Pont JS <-> Android : lancer une autre appli (Auto Link, Android Auto...) depuis le dashboard. */
class Bridge(private val a: Activity, private val web: WebView) {
    private var gpsOn = false
    private val listener = object : LocationListener {
        override fun onLocationChanged(l: Location) {
            val sp = if (l.hasSpeed()) l.speed * 3.6 else -1.0
            val br = if (l.hasBearing()) l.bearing.toDouble() else -1.0
            val alt = if (l.hasAltitude()) l.altitude.toString() else "null"
            val acc = if (l.hasAccuracy()) l.accuracy.toDouble() else 99.0
            web.post { web.evaluateJavascript("window.__gps&&window.__gps($sp,$br,$alt,$acc)", null) }
        }
        @Deprecated("Deprecated in Java") override fun onStatusChanged(p: String?, s: Int, e: android.os.Bundle?) {}
        override fun onProviderEnabled(p: String) {}
        override fun onProviderDisabled(p: String) {}
    }


    private fun launcherApps(): List<Pair<String, String>> {
        val pm = a.packageManager
        val q = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(q, 0)
            .map { it.loadLabel(pm).toString() to it.activityInfo.packageName }
            .filter { it.second != a.packageName }
            .distinctBy { it.second }
            .sortedBy { it.first.lowercase() }
    }

    private fun norm(s: String) = s.lowercase().replace(Regex("[\\s_\\-.]"), "")

    /** Liste JSON [{l: nom, p: paquet}] des applis lançables. */
    @JavascriptInterface
    fun apps(): String = JSONArray(launcherApps().map { JSONObject().put("l", it.first).put("p", it.second) }).toString()

    /** Cherche une appli par mots-clés (séparés par des virgules), par ordre de priorité. Renvoie le paquet ou "". */
    @JavascriptInterface
    fun find(keywords: String): String {
        val list = launcherApps()
        for (kw in keywords.split(",").map { norm(it) }.filter { it.isNotEmpty() }) {
            list.firstOrNull { norm(it.first).contains(kw) || norm(it.second).contains(kw) }?.let { return it.second }
        }
        return ""
    }

    /** Ouvre l'appli. Renvoie "ok" ou "notfound". */
    @JavascriptInterface
    fun launch(pkg: String): String {
        val i = a.packageManager.getLaunchIntentForPackage(pkg) ?: return "notfound"
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        a.runOnUiThread { try { a.startActivity(i) } catch (e: Exception) {} }
        return "ok"
    }

    /** Icône réelle de l'appli en PNG base64 (ou ""). */
    @JavascriptInterface
    fun icon(pkg: String): String = try {
        val d = a.packageManager.getApplicationIcon(pkg)
        val bmp = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
        d.setBounds(0, 0, 96, 96); d.draw(Canvas(bmp))
        val o = ByteArrayOutputStream(); bmp.compress(Bitmap.CompressFormat.PNG, 100, o)
        Base64.encodeToString(o.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) { "" }

    @JavascriptInterface
    fun keepOn(on: Boolean) {
        a.runOnUiThread {
            if (on) a.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else a.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    /** Démarre le GPS (sans internet). Renvoie "ok", "denied" (permission demandée) ou "nogps". */
    @JavascriptInterface
    fun startGps(): String {
        if (gpsOn) return "ok"
        if (a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            a.runOnUiThread { a.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 7) }
            return "denied"
        }
        val lm = a.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.allProviders.contains(LocationManager.GPS_PROVIDER)) return "nogps"
        gpsOn = true
        a.runOnUiThread {
            try { lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, listener, Looper.getMainLooper()) }
            catch (e: SecurityException) { gpsOn = false }
        }
        return "ok"
    }

    fun stopGps() {
        val lm = a.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try { lm.removeUpdates(listener) } catch (e: Exception) {}
        gpsOn = false
    }
}
