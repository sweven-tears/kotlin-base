package pers.sweven.common.helper

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.io.File
import java.lang.ref.WeakReference

/**
 * 文件下载辅助类，封装 Android 系统下载能力并提供生命周期感知
 *
 * ### 核心功能
 * 1. 使用 [DownloadManager] 实现后台下载
 * 2. 自动处理下载完成广播接收
 * 3. 支持下载前/后的回调操作（before/after）
 * 4. 通过 [FileProvider] 安全传递文件 URI
 * 5. 生命周期自动清理（基于 [LifecycleObserver]）
 *
 * ### 安全要求
 * 1. 必须配置 FileProvider（见示例配置）
 * 2. 若需通过 Intent 打开文件，Android 7.0+ 必须使用 content:// URI
 *
 * ### 必要配置
 * 1. 在 AndroidManifest.xml 添加 FileProvider 配置：
 * ```xml
 * <provider
 *     android:name="androidx.core.content.FileProvider"
 *     android:authorities="${applicationId}.fileprovider"
 *     android:exported="false"
 *     android:grantUriPermissions="true">
 *     <meta-data
 *         android:name="android.support.FILE_PROVIDER_PATHS"
 *         android:resource="@xml/file_paths_public" />
 * </provider>
 * ```
 *
 * 2. 创建路径配置文件 res/xml/file_paths_public.xml：
 * ```xml
 * <paths>
 *     <!-- 映射系统下载目录：/storage/emulated/0/Download -->
 *     <external-path name="downloads" path="Download/" />
 * </paths>
 * ```
 *
 * ### 使用示例
 * ```kotlin
 * val downloadHelper = DownloadHelper(context, lifecycle)
 *     .setDownloadBefore { showProgress() }
 *     .setDownloadAfter { hideProgress() }
 *     .setDownloadResult { uri ->
 *         if (uri != null) {
 *             openFileWithUri(uri) // 使用 content:// URI 打开文件
 *         }
 *     }
 * downloadHelper.start(url, "filename.pdf")
 * ```
 *
 * @param context 上下文对象（自动弱引用处理）
 * @param lifecycle 可选的生命周期组件，用于自动资源回收
 *
 * @see FileProvider 文件安全访问提供者
 * @see DownloadManager 系统下载服务
 *
 * Created by Sweven on 2025/4/7
 * Email: sweventears@163.com
 */
class DownloadHelper(
    context: Context,
    lifecycle: Lifecycle? = null,
) : LifecycleObserver {

    private val weakContext = WeakReference(context)
    var downloadId: Long = -1L
    private var before = {}
    private var after = {}
    private var result: ((Uri?) -> Unit)? = null
    private var publicDir = true

    init {
        lifecycle?.addObserver(this)
    }

    fun setDownloadBefore(before: () -> Unit): DownloadHelper {
        this.before = before
        return this
    }

    fun setDownloadAfter(after: () -> Unit): DownloadHelper {
        this.after = after
        return this
    }

    fun setDownloadResult(result: (Uri?) -> Unit): DownloadHelper {
        this.result = result
        return this
    }

    fun setPublicDir(public: Boolean): DownloadHelper {
        this.publicDir = public
        return this
    }

    fun start(url: String, filename: String) {
        val context = weakContext.get() ?: return
        before()

        // 注册
        context.registerReceiver(downloadCompleteReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(filename)
            setDescription("开始下载...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            if (publicDir) {
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            } else {
                setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, filename)
            }
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
    }


    val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    after()
                    checkDownloadStatus(id)
                    unregisterReceiver()
                }
            }
        }
    }

    private fun checkDownloadStatus(downloadId: Long) {
        val context = weakContext.get() ?: return
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(query).use { cursor ->
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val str = cursor.getString(columnIndex)
                        val uri = Uri.parse(str)
                        statusOk(context, uri)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        result?.invoke(null) ?: Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun statusOk(context: Context, uri: Uri) {
        val contentUri = try {
            val file = File(uri.path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else {
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            uri
        }
        result?.invoke(contentUri) ?: Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()
    }

    private fun unregisterReceiver() {
        val context = weakContext.get() ?: return
        try {
            context.unregisterReceiver(downloadCompleteReceiver)
        } catch (e: IllegalArgumentException) {
            // 接收器未注册，无需处理
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        unregisterReceiver()
        weakContext.clear()
    }

    fun registerReceiver(register: (receiver: BroadcastReceiver, filter: IntentFilter) -> Unit) {
        register(downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun unregisterReceiver(unregister: (receiver: BroadcastReceiver) -> Unit) {
        unregister(downloadCompleteReceiver)
    }

    companion object {

        /**
         * 打开文件
         * @param [context] 上下文
         * @param [uri] URI
         */
        @JvmStatic
        fun openFile(context: Context, uri: Uri) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && uri.scheme == "file") {
                throw SecurityException("禁止直接传递 file:// URI（需配置 FileProvider）")
            }
            // 通过 Intent 打开文件
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, getMimeType(context, uri))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "没有支持打开此文件的应用", Toast.LENGTH_SHORT).show()
            }
        }

        // 辅助方法：获取 MIME 类型
        private fun getMimeType(context: Context, uri: Uri): String? {
            return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                context.contentResolver.getType(uri)
            } else {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
            }
        }
    }
}