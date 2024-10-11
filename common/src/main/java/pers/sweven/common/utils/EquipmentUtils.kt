package pers.sweven.common.utils

import android.Manifest
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresPermission


/**
 * Created by Sweven on 2024/9/13--14:01.
 * Email: sweventears@163.com
 */
object EquipmentUtils {

    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getCurrentWiFiBSSID(context: Context): String? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.bssid
    }

    fun getCurrentWifiSSID(context: Context): String? {
        var ssid: String? = null
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val connectionInfo = wifiManager.connectionInfo
            if (connectionInfo!= null && isConnectedToWifi(wifiManager)) {
                ssid = connectionInfo.ssid
                if (ssid.startsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length - 1)
                    if (ssid.endsWith("\"")) {
                        ssid = ssid.substring(0, ssid.length - 1)
                    }
                }
            }
        } else {
            val wifiInfo: WifiInfo? = wifiManager.connectionInfo
            if (wifiInfo!= null && isConnectedToWifi(wifiManager)) {
                ssid = wifiInfo.ssid
            }
        }
        return ssid
    }

    private fun isConnectedToWifi(wifiManager: WifiManager): Boolean {
        val networkInfo = wifiManager.connectionInfo
        return networkInfo!= null && networkInfo.networkId!= -1
    }
}