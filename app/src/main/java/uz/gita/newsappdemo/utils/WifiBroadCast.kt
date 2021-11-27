package uz.gita.newsappdemo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

class WifiBroadCast : BroadcastReceiver() {

    private var listener: ((Boolean) -> Unit)? = null
    fun setOnChangeListener(block: (Boolean) -> Unit) {
        listener = block
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val myWiFiManager: WifiManager =
            context?.getApplicationContext()?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (myWiFiManager.wifiState == 3) {
            listener?.invoke(true)
        } else if (myWiFiManager.wifiState == 1)
            listener?.invoke(false)
    }
}