package pers.sweven.common.utils

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity


/**
 * Created by Sweven on 2024/4/23--11:13.
 * Email: sweventears@163.com
 */
object SPermission

/**
 * 是否获得权限
 * @param [permission] 许可
 * @return [Boolean]
 */
fun FragmentActivity.hasPermission(permission: String): Boolean {
    val granted = PackageManager.PERMISSION_GRANTED
    return ActivityCompat.checkSelfPermission(this, permission) == granted
}

/**
 * 请求权限
 * @param [permission] 许可
 * @param [requestCode] 请求代码
 */
fun FragmentActivity.requestPermission(permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
}
