package pers.sweven.common.repository.entity.request

import androidx.lifecycle.MutableLiveData
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 定义一个开放类 BaseBody，用于处理对象字段的转换和空值检查
 *
 * graph TD;
 *
 *
 * classDef startend fill:#F5EBFF,stroke:#BE8FED,stroke-width:2px;
 *
 *
 * classDef process fill:#E5F6FF,stroke:#73A6FF,stroke-width:2px;
 *
 *
 * classDef decision fill:#FFF6CC,stroke:#FFBC52,stroke-width:2px;
 *
 * A([开始]):::startend --> B{调用方法}:::decision;
 *
 *
 * B -->|toBody| C(调用 processAllFields):::process;
 *
 *
 * B -->|checkNullByDesc| C;
 *
 *
 * C --> D{遍历类及其父类字段}:::decision;
 *
 *
 * D -->|有字段| E(调用 processField):::process;
 *
 *
 * E --> F{获取 Param 注解}:::decision;
 *
 *
 * F -->|存在且 isAdd=true| G(解析键 key):::process;
 *
 *
 * F -->|不存在或 isAdd=false| D;
 *
 *
 * G --> H(获取字段值并解包):::process;
 *
 *
 * H --> I{值是否为 BaseBody 类型}:::decision;
 *
 *
 * I -->|是| J(调用值的 toBody 方法):::process;
 *
 *
 * J --> K{嵌套 Map 类型是否匹配 T}:::decision;
 *
 *
 * K -->|是| L(添加到 map):::process;
 *
 *
 * K -->|否| M(输出错误信息):::process;
 *
 *
 * I -->|否| N{是否满足添加值条件}:::decision;
 *
 *
 * N -->|是| O(调用 valueHandler 处理值):::process;
 *
 *
 * N -->|否| D;
 *
 *
 * O --> P{处理结果非空}:::decision;
 *
 *
 * P -->|是| L;
 *
 *
 * P -->|否| D;
 *
 *
 * L --> D;
 *
 *
 * D -->|无字段| Q(返回 map):::process;
 *
 *
 * Q --> R([结束]):::startend;
 *
 *
 * @constructor Create empty Base body
 */
open class BaseBody {

    // 将对象的所有字段转换为一个包含任意类型值的 HashMap
    fun toBody(): HashMap<String, Any> {
        // 调用 processAllFields 方法，并传入一个 lambda 表达式，直接返回字段的值
        return processAllFields { _, _, any -> any }
    }

    fun toStringBody(): HashMap<String, String> {
        return processAllFields { _, _, any -> any?.toString() ?: "" }
    }

    /**
     * 对对象的字段进行增强版的空值检查，考虑了 Param 注解中的条件
     * @param [exceptName] 是一个可变参数，用于指定需要排除检查的字段名
     * @return [HashMap<String, String>]
     */
    fun checkNullByDesc(vararg exceptName: String): HashMap<String, String> {
        return processAllFields { field, _, value ->
            // 获取字段上的 Param 注解
            val param = field.getAnnotation(Param::class.java)
            // 如果注解为空、字段不应该被添加或者值不满足添加条件，则返回 null
            if (param == null || !shouldAddField(param) || !shouldAddValue(param, value)) {
                return@processAllFields null
            }
            // 检查注解中的描述是否不为空，字段名不在排除列表中，且值在 noAdd 列表中
            param.takeIf {
                it.desc.isNotEmpty() &&
                        !exceptName.contains(field.name) &&
                        it.noAdd.contains(value?.toString())
            }?.desc
        }
    }

    /**
     * 私有内联泛型方法，用于处理对象及其父类的所有字段
     *
     *
     * reified T 表示该泛型类型在运行时是可具体化的
     * @param [valueHandler] 是一个函数，用于处理每个字段的值
     * @return [HashMap<String, T>]
     */
    private inline fun <reified T> processAllFields(crossinline valueHandler: (Field, String, Any?) -> T?): HashMap<String, T> {
        // 创建一个空的 HashMap 用于存储处理后的键值对
        val map = hashMapOf<String, T>()
        // 获取当前对象的类
        var clazz: Class<*>? = this::class.java

        // 遍历当前类及其所有父类
        while (clazz != null) {
            // 遍历当前类的所有声明字段
            clazz.declaredFields.forEach { field ->
                // 对字段进行同步处理，确保线程安全
                synchronized(field) {
                    // 调用 processField 方法处理单个字段
                    processField(field, map, valueHandler)
                }
            }
            // 获取父类，继续遍历
            clazz = clazz.superclass
        }
        // 返回处理后的 HashMap
        return map
    }

    /**
     * 私有内联泛型方法，用于处理单个字段
     * @param [field] 田
     * @param [map] 地图
     * @param [handler] 处理器
     * @return [Boolean]
     */
    private inline fun <reified T> processField(
        field: Field,
        map: MutableMap<String, T>,
        crossinline handler: (Field, String, Any?) -> T?,
    ): Boolean {
        try {
            // 设置字段的可访问性，以便可以获取私有字段的值
            field.isAccessible = true
            // 获取字段上的 Param 注解
            val param = field.getAnnotation(Param::class.java)
            // 如果字段不应该被添加，则返回 false
            if (!shouldAddField(param)) return false

            // 解析字段的键，如果注解中有指定值则使用，否则使用字段名
            val key = resolveKey(field, param)
            // 获取字段的原始值
            val rawValue = field.get(this)
            // 对原始值进行解包处理
            val value = unwrapValue(rawValue)

            // 处理反射异常，如果发生异常则返回 false
            if (handleReflectionException(key, value)) return false

            // 如果值是 BaseBody 类型
            if (value is BaseBody) {
                // 调用该对象的 toBody 方法，将其转换为 HashMap
                val nestedMap = value.toBody()
                // 检查嵌套的 HashMap 是否是泛型类型 T 的实例
                if (nestedMap is T) {
                    // 如果是，则将其添加到结果 map 中
                    map[key] = nestedMap
                    return true
                }
            }

            // 如果值不满足添加条件，则返回 false
            if (!shouldAddValue(param, value)) return false

            // 调用 handler 函数处理字段的值，如果结果不为空，则添加到结果 map 中
            handler(field, key, value)?.let {
                map[key] = it
                return true
            }
        } catch (e: Exception) {
            // 处理异常，如果发生异常则返回 false
            if (handleReflectionException(e)) return false
        }
        // 如果以上条件都不满足，则返回 false
        return false
    }

    /**
     * 判断字段是否应该被添加到结果中
     * @param [param] 参数
     * @return [Boolean]
     */
    private fun shouldAddField(param: Param?): Boolean {
        // 如果 Param 注解不为空且 isAdd 属性为 true，则返回 true
        return param?.isAdd == true
    }

    /**
     * 判断字段的值是否应该被添加到结果中
     * @param [param] 参数
     * @param [value] 价值
     * @return [Boolean]
     */
    private fun shouldAddValue(param: Param?, value: Any?): Boolean {
        // 如果注解中有指定条件类
        param?.condition?.let { conditionClass ->
            // 创建条件类的实例
            val condition = conditionClass.java.newInstance()
            // 如果条件类的 shouldAdd 方法返回 false，则返回 false
            if (!condition.shouldAdd(value)) return false
        }
        // 如果值不在注解的 noAdd 列表中，则返回 true
        return param?.noAdd?.contains(value?.toString() ?: "") != true
    }

    /**
     * 解析字段的键，如果注解中有指定值则使用，否则使用字段名
     * @param [field] 田
     * @param [param] 参数
     * @return [String]
     */
    private fun resolveKey(field: Field, param: Param?): String {
        return param?.value?.takeIf { it.isNotEmpty() }
            ?: field.name
    }

    /**
     * 对值进行解包处理，处理 MutableLiveData 和 Collection 类型的值
     * @param [value] 价值
     * @return [Any?]
     */
    private fun unwrapValue(value: Any?): Any? {
        return when (value) {
            is MutableLiveData<*> -> value.value
            is Collection<*> -> value.map { unwrapValue(it) }
            else -> value
        }
    }

    /**
     * 处理反射异常，默认返回 false
     * @param [key] 钥匙
     * @param [value] 价值
     * @return [Boolean]
     */
    protected open fun handleReflectionException(key: String, value: Any?): Boolean {
        return false
    }

    /**
     * 处理异常，打印异常堆栈信息，默认返回 false
     * @param [e] e
     * @return [Boolean]
     */
    protected open fun handleReflectionException(e: Exception): Boolean {
        e.printStackTrace()
        return false
    }

    /**
     * 定义 Param 注解，用于标记字段的相关信息
     * @param [value] 字段的序列化名称
     * @param [noAdd] 不添加到结果中的值列表
     * @param [isAdd] 是否包含该字段
     * @param [desc] 字段的描述信息
     * @param [condition] 字段值的添加条件类
     */
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FIELD)
    annotation class Param(
        @get:JvmName("paramValue") val value: String = "",
        @get:JvmName("noAddValues") val noAdd: Array<String> = [],
        @get:JvmName("isIncluded") val isAdd: Boolean = true,
        @get:JvmName("description") val desc: String = "",
        val condition: KClass<out Condition> = DefaultCondition::class
    ) {
        companion object {
            // 序列化名称的常量
            const val SERIALIZED_NAME = "value"
        }
    }

}