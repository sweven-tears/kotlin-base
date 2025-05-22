package pers.sweven.common.repository.entity.request

/**
 * Created by Sweven on 2025/5/21--13:25.
 * Email: sweventears@163.com
 */
/**
 * 值转换器接口
 */
interface ValueConverter {
    fun convert(value: Any?): Any?
}

/**
 * 默认转换器（直接返回原值）
 */
class DefaultConverter : ValueConverter {
    override fun convert(value: Any?) = value
}

inline fun valueConverter(crossinline block: (Any?) -> Any?): ValueConverter {
    return object : ValueConverter {
        override fun convert(value: Any?): Any? {
            return block(value)
        }
    }
}