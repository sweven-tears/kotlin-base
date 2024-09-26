package pers.sweven.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.io.IOException

/**
 * Created by Sweven on 2024/4/23--11:22.
 * Email: sweventears@163.com
 */
object Location

fun FragmentActivity.getAddress(latitude: Double, longitude: Double): Array<String> {
    val geocoder = Geocoder(this)
    val flag = Geocoder.isPresent()
    Log.e("LocationHelper", "the flag is $flag")
    val arrays = arrayListOf<String>()
    try {

        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 10)
        if (addresses.isNotEmpty()) {
            for (address in addresses) {
                val area = StringBuilder()
                    .append(address.adminArea ?: "")
                    .append(address.locality ?: "")
                    .append(address.subLocality ?: "")
                    .append(address.thoroughfare ?: "")
                    .append(address.subThoroughfare ?: "")
                    .append(address.featureName ?: "")
                    .toString()
                arrays.add(area)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return arrays.toArray(Array(arrays.size) { "" })
}

/**
 * 获取本地地址 (0 to null)=权限不足  (-1 to (0.0 to 0.0))=无可用位置提供器 (-2 to (0.0 to 0.0))=定位信息获取失败 (0 to (x to y))=正常
 * @param [locationListener] 位置侦听器
 * @return [Pair<Int,Pair<Double, Double>?>]
 */
@SuppressLint("MissingPermission")
fun FragmentActivity.getLocalAddress(locationListener: LocationListener): Pair<Int, Pair<Double, Double>?> {
    // 获取位置管理器
    val manager = ContextCompat.getSystemService(this, LocationManager::class.java) // 位置

    // 权限别名
    val fine = Manifest.permission.ACCESS_FINE_LOCATION
    val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

    // 判断权限是否存在，位置管理器是否获取失败
    if (this.hasPermission(fine) && this.hasPermission(coarse) && manager != null) {
        val gpsProvider = LocationManager.GPS_PROVIDER
        val networkProvider = LocationManager.NETWORK_PROVIDER

        if (manager.isProviderEnabled(gpsProvider)) {
            // 优先使用GPS
            manager.requestLocationUpdates(gpsProvider, 0, 0f, locationListener)
        } else if (manager.isProviderEnabled(networkProvider)) {
            // 再次使用网络
            manager.requestLocationUpdates(networkProvider, 0, 0f, locationListener)
        } else {
            // 无可用位置提供器
            return -1 to (0.0 to 0.0)
        }
        val location = manager.getLastKnownLocation(gpsProvider)
            ?: manager.getLastKnownLocation(networkProvider)
            ?: return -2 to (0.0 to 0.0)
        // 获取经纬度并返回
        return 0 to (location.latitude to location.longitude)
    }
    // 权限不够，返回null
    return 0 to null
}
