package pers.sweven.common.repository.entity.request

import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName
import pers.sweven.common.repository.entity.request.BaseRequest.Param
import java.lang.reflect.Field

/**
 * 基本请求参数构建
 *
 * [Param]=别名+不加入参数特例+是否加入参数
 *
 * Created by Sweven on 2023/12/05--14:38.
 * Email: sweventears@163.com
 */
open class BaseRequest() {

    open fun toMap(): HashMap<String, String> {
        val fields = javaClass.declaredFields
        val map = hashMapOf<String, String>()
        fields.forEachIndexed { index, field ->
            addMap(field, map)
        }
        return map
    }

    open fun addMap(it: Field, map: HashMap<String, String>): Boolean {
        val param = it.getAnnotation(Param::class.java)
        if (param != null) {
            if (!param.isAdd) {
                return false
            }
            val name = if (param.value == "") it.name else param.value
            it.isAccessible = true
            val v = it.get(this)
            val value = if (v is MutableLiveData<*>) {
                (v.value ?: "").toString()
            } else (it.get(this) ?: "").toString()
            if (param.noAdd.contains(value)) {
                return false
            }
            map[name] = value
            return true
        }

        var name = it.getAnnotation(SerializedName::class.java)?.value ?: ""
        name = if (name == "") it.name else name
        it.isAccessible = true
        val get = it.get(this)
        val value = if (get is MutableLiveData<*>) {
            (get.value ?: "").toString()
        } else {
            (get ?: "").toString()
        }
        map[name] = value
        return true
    }

    open fun toAnyMap(): HashMap<String, Any?>{
        val fields = javaClass.declaredFields
        val map = hashMapOf<String, Any?>()
        fields.forEachIndexed { index, field ->
            addAnyMap(field, map)
        }
        return map
    }


    open fun addAnyMap(it: Field, map: HashMap<String, Any?>): Boolean {
        val param = it.getAnnotation(Param::class.java)
        if (param != null) {
            if (!param.isAdd) {
                return false
            }
            val name = if (param.value == "") it.name else param.value
            it.isAccessible = true
            val get = it.get(this)
            val value = if (get is MutableLiveData<*>) get.value else it.get(this)

            if (param.noAdd.contains(value.toString())) {
                return false
            }
            map[name] = value
            return true
        }

        var name = it.getAnnotation(SerializedName::class.java)?.value ?: ""
        name = if (name == "") it.name else name
        it.isAccessible = true
        val get = it.get(this)
        val value = if (get is MutableLiveData<*>) {
            (get.value ?: "").toString()
        } else {
            (get ?: "").toString()
        }
        map[name] = value
        return true
    }

    /**
     * 抛出Param定义的可用字段是否为空（desc为描述文本）
     * @return [String?]
     */
    fun throwNullByDesc(vararg exceptName: String): String? {
        val fields = javaClass.declaredFields
        fields.forEach {
            it.isAccessible = true

            if (exceptName.contains(it.name)) {
                return null
            }

            val value = it.get(this)?.toString() ?: ""
            val param = it.getAnnotation(Param::class.java)
            if (param != null && param.isAdd && param.desc != "") {
                if (param.noAdd.contains(value)) {
                    return param.desc
                }
            }
        }
        return null
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
    )
    annotation class Param(
        val value: String = "",
        val noAdd: Array<String> = [],
        val isAdd: Boolean = true,
        val desc: String = "",
    )
}