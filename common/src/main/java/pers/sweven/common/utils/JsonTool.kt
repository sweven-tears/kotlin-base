package pers.sweven.common.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by Sweven on 2024/12/16--17:02.
 * Email: sweventears@163.com
 */
abstract class JsonTool {
    open val gson = Gson()

    open fun <T> fromJson(json: String?, clazz: Class<T>): T? {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(json, type)
    }

    open fun <T> fromJsonList(json: String?, clazz: Class<T>): List<T>? {
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type)
    }

    open fun <T> fromJsonType(json: String?, type: Type): T? {
        return gson.fromJson(json, type)
    }

    open fun toJson(obj: Any): String {
        return gson.toJson(obj)?:""
    }
}