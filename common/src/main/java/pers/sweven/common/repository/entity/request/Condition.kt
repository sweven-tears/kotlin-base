package pers.sweven.common.repository.entity.request

/**
 * 定义 Condition 接口，用于判断字段的值是否应该被添加
 */
interface Condition {
    fun shouldAdd(value: Any?): Boolean
}

/**
 * 默认的 Condition 实现，始终返回 true
 */
class DefaultCondition : Condition {
    override fun shouldAdd(value: Any?) = true
}

inline fun condition(crossinline block: (Any?) -> Boolean): Condition {
    return object : Condition {
        override fun shouldAdd(value: Any?): Boolean {
            return block(value)
        }
    }
}