package pers.sweven.common.repository.entity.request

import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName
import pers.sweven.common.repository.entity.request.BaseRequest.Param
import java.lang.reflect.Field
import kotlin.reflect.KClass


/**
 * 基本请求参数构建
 *
 * [Param]=别名+不加入参数特例+是否加入参数
 *
 * Created by Sweven on 2023/12/05--14:38.
 * Email: sweventears@163.com
 */
open class BaseRequest {

    /**
     * 收集字符串类型参数映射
     */
    open fun toMap(): HashMap<String, String> {
        return toMap(false)
    }

    /**
     * 收集字符串类型参数映射（可选包含父类参数）
     * @param superClazz 是否包含父类的参数字段
     */
    open fun toMap(superClazz: Boolean): HashMap<String, String> {
        return collectMaps(superClazz) {
            it?.toString() ?: ""
        }
    }

    /**
     * 收集任意类型参数映射（可选包含父类参数）
     * @param superClazz 是否包含父类的参数字段
     */
    @JvmOverloads
    open fun toAnyMap(superClazz: Boolean = false): HashMap<String, Any> {
        return collectMaps(superClazz) {
            it ?: ""
        }
    }

    /**
     * 核心收集方法（使用泛型统一处理逻辑）
     * @param superClazz 是否包含父类参数
     * @param valueConverter 值转换器函数（用于处理不同类型的数据转换）
     * @return 收集完成的参数映射表
     */
    private inline fun <T> collectMaps(
        superClazz: Boolean,
        valueConverter: (Any?) -> T?
    ): HashMap<String, T> {
        // 创建结果容器
        val map = hashMapOf<String, T>()
        // 获取需要处理的类层次结构（当前类或包括父类）
        val classes = if (superClazz) generateSuperClasses() else listOf(javaClass)

        // 遍历所有类收集字段
        classes.forEach { clazz ->
            clazz.declaredFields.forEach { field ->
                addToMap(field, map, valueConverter)
            }
        }
        return map
    }

    /**
     * 核心字段处理方法（统一处理映射收集）
     * @param field 待处理字段
     * @param map 收集到的参数映射表
     * @param valueConverter 值转换器函数（用于处理不同类型的数据转换）
     */
    private inline fun <T> addToMap(
        field: Field,
        map: MutableMap<String, T>,
        valueConverter: (Any?) -> T?
    ) {
        // 获取字段注解
        val annotation = field.sortAnnotation()
        // 拦截过滤规则检查
        if (intercept(annotation)) return

        // 获取字段值（包含特殊类型处理）
        val value = getValue(field, annotation)
        // 拦截值过滤规则检查
        if (intercept(annotation, value)) return

        // 值转换并添加到映射表
        valueConverter(value)?.let { convertedValue ->
            map[getFieldName(field)] = convertedValue
        }
    }

    /**
     * 注解过滤规则检查
     * @param annotation 字段注解
     * @param value 字段值（可选）
     * @return 是否符合过滤条件（true为过滤掉此字段）
     */
    protected open fun intercept(annotation: Annotation?, value: Any? = null): Boolean {
        // 强制转换为Param注解类型
        val param = annotation as? Param
        // 检查是否标记不添加
        if (value == null && param != null && !param.isAdd) return true
        // 检查值是否在排除列表中
        if (value != null && param != null && param.noAdd.contains(value.toString())) return true

        return false
    }

    /**
     * 获取类继承层次结构
     * @return 当前类及其所有父类的列表
     */
    private fun generateSuperClasses(): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()
        var current: Class<*>? = javaClass
        while (current != null) {
            classes.add(current)
            current = current.superclass
        }
        return classes
    }

    /**
     * 排序字段注解（优先返回Param注解）
     * @return 匹配到的第一个注解（Param优先，其次SerializedName）
     */
    protected fun Field.sortAnnotation(): Annotation? {
        return annotations.firstOrNull { it is Param }
            ?: annotations.firstOrNull { it is SerializedName }
    }

    /**
     * 获取字段映射名称
     * @param field 目标字段
     * @return 优先使用注解中的别名，否则使用原字段名
     */
    protected fun getFieldName(field: Field): String {
        return field.getAnnotation(Param::class.java)?.value
            ?: field.getAnnotation(SerializedName::class.java)?.value
            ?: field.name
    }

    /**
     * 获取字段值（包含LiveData特殊处理）
     * @param field 目标字段
     * @param annotation 字段相关注解
     * @return 转换后的字段值
     */
    protected fun getValue(field: Field, annotation: Annotation?): Any? {
        field.isAccessible = true

        // 处理LiveData特殊取值逻辑
        val rawValue = field.get(this).let {
            if (it is MutableLiveData<*>) it.value else it
        }

        // 查找并应用值转换器
        return findConverter(annotation as? Param)?.convert(rawValue) ?: rawValue
    }

    /**
     * 查找值转换器
     * @param param 字段的Param注解
     * @return 实例化的值转换器或null
     */
    private fun findConverter(param: Param?): ValueConverter? {
        return when {
            param == null -> null
            param.converter.objectInstance != null -> param.converter.objectInstance!!
            else -> param.converter.java.newInstance()
        }
    }

    /**
     * 检查必填参数缺失（不包含父类字段）
     * @param exceptName 需要排除检查的字段名
     * @return 缺失字段的描述信息，全部存在返回null
     */
    fun throwNullByDesc(vararg exceptName: String): String? {
        val fields = javaClass.declaredFields
        fields.forEach {
            it.isAccessible = true

            if (exceptName.contains(it.name)) {
                return null
            }
            val annotation = it.sortAnnotation()

            val value = getValue(it, annotation)
            if (annotation is Param && annotation.isAdd) {
                if (annotation.desc != "") {
                    if (annotation.noAdd.contains(value.toString())) {
                        return annotation.desc
                    }
                }
                for (rule in annotation.rules) {
                    if (!value.toString().matches(Regex(rule.regular))) {
                        return rule.error
                    }
                }
            }
        }
        return null
    }

    /**
     * 检查必填参数缺失（包含父类字段）
     * @param exceptName 需要排除检查的字段名
     * @return 缺失字段的描述信息，全部存在返回null
     */
    fun throwSuperNullByDesc(vararg exceptName: String): String? {
        var clazz: Class<*>? = javaClass
        while (clazz != null) {
            val fields = clazz.declaredFields
            fields.forEach {
                it.isAccessible = true

                if (exceptName.contains(it.name)) {
                    return null
                }

                val annotation = it.sortAnnotation()
                val value = getValue(it, annotation)
                if (annotation is Param && annotation.isAdd) {
                    if (annotation.desc != "") {
                        if (annotation.noAdd.contains(value)) {
                            return annotation.desc
                        }
                    }
                    for (rule in annotation.rules) {
                        if (!value.toString().matches(Regex(rule.regular))) {
                            return rule.error
                        }
                    }
                }
            }
            clazz = clazz.superclass
        }
        return null
    }

    /**
     * 参数
     * @param [value] 字段别名
     * @param [noAdd] 排除值列表
     * @param [isAdd] 是否加入最终参数
     * @param [desc] 参数描述（用于必填校验）
     * @param [converter] 值转换器（实现ValueConverter接口）
     * Created by Sweven on 2024/12/20--10:37.
     * Email: sweventears@163.com
     */
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
        val converter: KClass<out ValueConverter> = DefaultConverter::class,
        val rules: Array<MatchRule> = []
    )

    /**
     * 匹配规则注解
     * @param [regular] 正则表达规则
     * @param [error] 匹配失败错误提示
     * Created by Sweven on 2025/07/23--14:46.
     * Email: sweventears@163.com
     */
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MatchRule(
        val regular: String,
        val error: String
    )
}