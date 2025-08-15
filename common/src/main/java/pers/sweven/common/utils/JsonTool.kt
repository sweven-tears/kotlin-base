package pers.sweven.common.utils

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Created by Sweven on 2024/12/16--17:02.
 * Email: sweventears@163.com
 */
abstract class JsonTool {
    open val gson = Gson()

    inline fun <reified T> fromJson(json: String?): T? {
        return gson.fromJson(json, T::class.java)
    }

    open fun <T> fromJson(json: String?, clazz: Class<T>): T? {
        return gson.fromJson(json, clazz)
    }

    open fun <T> fromJson(json: String?, type: Type): T? {
        return gson.fromJson(json, type)
    }

    open fun toJson(obj: Any?): String {
        return gson.toJson(obj) ?: ""
    }
}