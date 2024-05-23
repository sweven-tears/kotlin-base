package pers.sweven.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.io.IOException

/**
 * Created by Sweven on 2024/4/23--11:22.
 * Email: sweventears@163.com
 */
object LocationHelper

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
                    .append(address.adminArea?:"")
                    .append(address.locality?:"")
                    .append(address.subLocality?:"")
                    .append(address.thoroughfare?:"")
                    .append(address.subThoroughfare?:"")
                    .append(address.featureName?:"")
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
 * 获取本地地址
 * @return [Array<Double>?] ==null时代表权限不够
 */
@SuppressLint("MissingPermission")
fun FragmentActivity.getLocalAddress(): Array<Double>? {
    val manager = ContextCompat.getSystemService(this, LocationManager::class.java) // 位置

    val fine = Manifest.permission.ACCESS_FINE_LOCATION
    val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

    if (this.hasPermission(fine) && this.hasPermission(coarse)) {
        val location = manager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: return arrayOf(0.0, 0.0)
        return arrayOf(location.latitude, location.longitude)
    }
    return null
}