package pers.sweven.common.helper.photo

import android.net.Uri
import java.util.*

/**
 * Created by Sweven on 2025/4/1--16:52.
 * Email: sweventears@163.com
 */
class PhotosHelper private constructor(val factory: Factory) {

    fun setCount(selectorMaxCount: Int): PhotosHelper {
        factory.count = selectorMaxCount
        return this
    }


    fun setSelectedPhotoPaths(
        selectedPhotoPaths: ArrayList<PhotoData>,
    ): PhotosHelper {
        factory.selected = selectedPhotoPaths
        return this
    }

    fun build(): PhotosHelper {
        return this
    }

    fun start(
        result: (photos: List<PhotoData>) -> Unit,
        cancel: (() -> Unit)? = null,
    ) {
        factory.result = result
        factory.cancel = { cancel?.invoke() }
        factory.start()
    }

    fun start(result: (photos: List<PhotoData>) -> Unit) {
        factory.result = result
        factory.start()
    }

    companion object {

        fun get(factory: Factory): PhotosHelper {
            return PhotosHelper(factory)
        }
    }

    interface Factory {
        var count: Int
        var hasVideo: Boolean
        var camera: Boolean
        var selected: List<PhotoData>
        var result: (List<PhotoData>) -> Unit
        var cancel: () -> Unit

        fun start()
        fun create()
    }

    data class PhotoData(
        var name: String = "",
        var filePath: String = "",
        var uriPath: String = "",
        var type: String = "",
        var width: Int = 0,
        var height: Int = 0,
        var size: Long = 0,
        var duration: Long = 0,
        var time: Long = 0,
    ) {
        val uri: Uri get() = Uri.parse(uriPath)
    }

    class DefaultFactory : Factory {
        override var count: Int = 1
        override var hasVideo: Boolean = false
        override var camera: Boolean = false
        override var selected: List<PhotoData> = emptyList()
        override var result: (List<PhotoData>) -> Unit = {}
        override var cancel: () -> Unit = {}

        override fun create() {
        }

        override fun start() {
        }
    }
}