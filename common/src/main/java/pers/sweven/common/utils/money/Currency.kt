package pers.sweven.common.utils.money

import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

// ====================== Currency 类 ======================
/**
 * 货币
 * @param [mainUnit] 主单位
 * @param [subUnit1] 子单元1
 * @param [subUnit2] 子单元2
 * @param [subUnit3] 子单元3
 * @param [isNegative] 为负数
 * @param [currencyCode] 货币代码
 * Created by Sweven on 2025/5/6--14:36.
 * Email: sweventears@163.com
 */
data class Currency(
    var mainUnit: Long,
    var subUnit1: Int = 0,
    var subUnit2: Int = 0,
    var subUnit3: Int = 0,
    var isNegative: Boolean = false,
    var currencyCode: String = "CNY"
) : Comparable<Currency> {
    // region 核心方法
    companion object {
        // 向外暴露
        fun String?.currencyMoney(currencyCode: String = "CNY") = from(this, currencyCode)

        fun Double?.currencyMoney(currencyCode: String = "CNY") = from(this, currencyCode)

        @JvmStatic
        fun from(value: Any?, currencyCode: String = "CNY"): Currency {
            return when (value) {
                is Currency -> value
                is Number -> fromNumber(value.toDouble(), currencyCode)
                is String -> fromString(value, currencyCode)
                else -> fromNumber(0.00, currencyCode)
            }
        }

        private fun fromNumber(number: Double, code: String): Currency {
            val meta = CurrencyRegistry.getMeta(code)
            return decompose(number, meta)
        }

        private fun fromString(str: String, defaultCode: String): Currency {
            val (symbolPart, numberPart) = parseSymbol(str)
            val code = symbolPart?.let {
                try {
                    CurrencyRegistry.getMetaBySymbol(it).code
                } catch (e: Exception) {
                    defaultCode
                }
            } ?: defaultCode

            val number = numberPart.toDoubleOrNull() ?: 0.0
            return decompose(number, CurrencyRegistry.getMeta(code))
        }

        private fun decompose(amount: Double, meta: CurrencyMeta): Currency {
            val isNegative = amount < 0
            val absAmount = abs(amount)
            val mainUnit = absAmount.toLong()
            val decimalPart = absAmount - mainUnit

            val totalSubunits = (decimalPart * 10.0.pow(meta.decimalPlaces)).roundToInt()
            return Currency(
                mainUnit = mainUnit,
                subUnit1 = getSubUnit(totalSubunits, meta.decimalPlaces, 1),
                subUnit2 = getSubUnit(totalSubunits, meta.decimalPlaces, 2),
                subUnit3 = getSubUnit(totalSubunits, meta.decimalPlaces, 3),
                isNegative = isNegative,
                currencyCode = meta.code
            )
        }

        private fun parseSymbol(str: String): Pair<String?, String> {
            val regex = Regex("""^(\p{Sc})\s*([\d.]+)$""")
            return regex.find(str.trim())?.destructured?.let {
                Pair(it.match.groupValues[1], it.match.groupValues[2])
            } ?: Pair(null, str)
        }

        private fun getSubUnit(total: Int, decimalPlaces: Int, level: Int): Int {
            val divide = 10.0.pow(decimalPlaces - level).toInt()
            return if (decimalPlaces >= level && divide != 0) {
                (total / divide) % 10
            } else {
                0
            }
        }
    }

    fun convertTo(targetCurrencyCode: String, baseToTargetRate: Double): Currency {
        require(baseToTargetRate > 0) { "汇率必须为正数" }
        val targetMeta = CurrencyRegistry.getMeta(targetCurrencyCode)

        // 转换为目标货币金额
        val sourceAmount = this.toDouble()
        val targetAmount = sourceAmount * baseToTargetRate // 基础货币→目标货币

        // 四舍五入到目标货币精度
        val roundedAmount = roundToDecimal(targetAmount, targetMeta.decimalPlaces)
        return from(roundedAmount, targetCurrencyCode)
    }

    private fun roundToDecimal(value: Double, decimalPlaces: Int): Double {
        val factor = 10.0.pow(decimalPlaces)
        return (value * factor).roundToInt() / factor
    }

    fun isEquivalentTo(other: Any?, exchangeRate: Double? = null): Boolean {
        return try {
            val otherCurrency = when (other) {
                is Currency -> other
                else -> from(other) // 假设默认货币与当前对象相同
            }
            if (currencyCode == otherCurrency.currencyCode) {
                this == otherCurrency
            } else {
                require(exchangeRate != null) { "跨货币比较需提供汇率" }
                val thisAmount = this.toDouble()
                val otherAmount = otherCurrency.toDouble() * exchangeRate
                thisAmount == otherAmount
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 提取小数部分为整数（不带小数点）
     * 示例：
     * - 5.60 CNY（3位小数）→ 600
     * - 5.60 USD（2位小数）→ 60
     * - 5.792 CNY → 792
     * - 5.69 USD → 69
     */
    fun decimalPart(): Int {
        val meta = CurrencyRegistry.getMeta(currencyCode)
        return when (meta.decimalPlaces) {
            1 -> subUnit1
            2 -> subUnit1 * 10 + subUnit2
            3 -> subUnit1 * 100 + subUnit2 * 10 + subUnit3
            else -> 0 // 0位小数（如JPY）或无定义
        }
    }

    /**
     * 获取完整小数部分字符串（保留前导零）
     * 示例：
     * - 5.06 CNY → "060"
     * - 5.02 USD → "02"
     */
    fun decimalString(): String {
        val meta = CurrencyRegistry.getMeta(currencyCode)
        return buildString {
            if (meta.decimalPlaces >= 1) append(subUnit1)
            if (meta.decimalPlaces >= 2) append(subUnit2)
            if (meta.decimalPlaces >= 3) append(subUnit3)
        }.padEnd(meta.decimalPlaces, '0')
    }

    /**
     * 判断两个 Currency 对象是否等价
     * 等价条件：货币代码相同，且各子单位及符号完全一致
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Currency) return false

        return currencyCode == other.currencyCode &&
                mainUnit == other.mainUnit &&
                subUnit1 == other.subUnit1 &&
                subUnit2 == other.subUnit2 &&
                subUnit3 == other.subUnit3 &&
                isNegative == other.isNegative
    }

    /**
     * 生成哈希码，确保等价对象哈希一致
     */
    override fun hashCode(): Int {
        return Objects.hash(
            currencyCode,
            mainUnit,
            subUnit1,
            subUnit2,
            subUnit3,
            isNegative
        )
    }

    // endregion

    // region 操作符重载
    /**
     * 加法：相同货币才能相加
     * @throws IllegalArgumentException 货币类型不一致时抛出
     */
    operator fun plus(other: Currency): Currency {
        require(currencyCode == other.currencyCode) { "货币类型不一致: $currencyCode vs ${other.currencyCode}" }
        return operate(this, other) { a, b -> a + b }
    }

    /**
     * 减法：相同货币才能相减
     * @throws IllegalArgumentException 货币类型不一致时抛出
     */
    operator fun minus(other: Currency): Currency {
        require(currencyCode == other.currencyCode) { "货币类型不一致: $currencyCode vs ${other.currencyCode}" }
        return operate(this, other) { a, b -> a - b }
    }

    /**
     * 乘法：货币乘以数值
     * @throws IllegalArgumentException 乘数非正数时抛出
     */
    operator fun times(number: Number): Currency {
        val multiplier = number.toDouble()
        require(multiplier >= 0) { "乘数不能为负数" }
        return scale(this) { it * multiplier }
    }

    /**
     * 除法：货币除以数值
     * @throws IllegalArgumentException 除数为零或负数时抛出
     */
    operator fun div(number: Number): Currency {
        val divisor = number.toDouble()
        require(divisor > 0) { "除数必须为正数" }
        return scale(this) { it / divisor }
    }
    // endregion

    // region compare

    /**
     * 比较两个同种货币对象的大小
     * @throws IllegalArgumentException 货币类型不一致时抛出
     * @return 正数表示当前对象更大，负数表示更小，0表示相等
     */
    override fun compareTo(other: Currency): Int {
        require(currencyCode == other.currencyCode) {
            "无法比较不同货币: $currencyCode vs ${other.currencyCode}"
        }
        return this.toSmallestSubunits().compareTo(other.toSmallestSubunits())
    }

    /**
     * 跨货币比较（需提供汇率）
     * @param exchangeRate 1单位目标货币 = [exchangeRate]当前货币
     */
    fun compareToWithExchange(other: Currency, exchangeRate: Double): Int {
        require(exchangeRate > 0) { "汇率必须为正数" }
        val thisAmount = this.toDouble()
        val otherAmountInThisCurrency = other.toDouble() * exchangeRate
        return thisAmount.compareTo(otherAmountInThisCurrency)
    }

    // endregion

    // region 核心计算逻辑
    private fun operate(a: Currency, b: Currency, operation: (Long, Long) -> Long): Currency {
        val meta = CurrencyRegistry.getMeta(a.currencyCode)
        val totalA = a.toSmallestSubunits()
        val totalB = b.toSmallestSubunits()
        val result = operation(totalA, totalB)
        return fromSmallestSubunits(abs(result), meta, result < 0)
    }

    private fun scale(base: Currency, operation: (Long) -> Double): Currency {
        val meta = CurrencyRegistry.getMeta(base.currencyCode)
        val total = base.toSmallestSubunits()
        val scaled = operation(total)
        val rounded = scaled.roundToLong()
        return fromSmallestSubunits(rounded, meta, scaled < 0)
    }

    private fun toSmallestSubunits(): Long {
        val meta = CurrencyRegistry.getMeta(currencyCode)
        val value = mainUnit * 10.0.pow(meta.decimalPlaces) +
                subUnit1 * 10.0.pow(meta.decimalPlaces - 1) +
                subUnit2 * 10.0.pow(meta.decimalPlaces - 2) +
                subUnit3 * 10.0.pow(meta.decimalPlaces - 3)
        val signedValue = if (isNegative) -value else value
        return signedValue.roundToLong()
    }

    private fun fromSmallestSubunits(
        total: Long,
        meta: CurrencyMeta,
        isNegative: Boolean
    ): Currency {
        val absTotal = abs(total)
        val mainUnit = absTotal / 10.0.pow(meta.decimalPlaces).toLong()
        val remainder = absTotal % 10.0.pow(meta.decimalPlaces).toInt()
        val decimalPlaces = meta.decimalPlaces

        fun unit(total: Long, places: Int, level: Int): Int {
            val divide = 10.0.pow(places - level).toInt()
            if (divide == 0) {
                return 0
            }
            return ((total / divide) % 10).toInt()
        }

        return Currency(
            mainUnit = mainUnit,
            subUnit1 = unit(remainder, decimalPlaces, 1),
            subUnit2 = unit(remainder, decimalPlaces, 2),
            subUnit3 = unit(remainder, decimalPlaces, 3),
            isNegative = isNegative,
            currencyCode = meta.code
        )
    }
    // endregion

    // region 显示优化

    fun toDouble(): Double {
        val meta = CurrencyRegistry.getMeta(currencyCode)
        val decimalValue = subUnit1 * 10.0.pow(meta.decimalPlaces - 1) +
                subUnit2 * 10.0.pow(meta.decimalPlaces - 2) +
                subUnit3 * 10.0.pow(meta.decimalPlaces - 3)
        val absolute = mainUnit + decimalValue / 10.0.pow(meta.decimalPlaces)
        return if (isNegative) -absolute else absolute
    }

    override fun toString(): String {
        val meta = CurrencyRegistry.getMeta(currencyCode)
        return buildString {
            if (isNegative) append("-")
            append(mainUnit)
            if (meta.decimalPlaces > 0) {
                append(".")
                append(subUnit1)
                if (meta.decimalPlaces >= 2) append(subUnit2)
                if (meta.decimalPlaces >= 3) append(subUnit3)
            }
        }
    }

    fun toUnitString(): String =
        CurrencyRegistry.getMeta(currencyCode).symbol + toString()

    // endregion
}

// ====================== 货币元数据类 ======================
data class CurrencyMeta(
    val code: String,      // 货币代码（ISO标准）
    val symbol: String,    // 货币符号
    val decimalPlaces: Int // 小数位数（如人民币3位，美元2位）
) {
    init {
        require(code.length == 3) { "货币代码必须为3位字母" }
        require(decimalPlaces in 0..3) { "小数位数需在0-3之间" }
    }
}

// ====================== 货币元数据管理器 ======================
object CurrencyRegistry {
    private val metaMap = mutableMapOf<String, CurrencyMeta>()
    private val symbolToCode = mutableMapOf<String, String>()

    init {
        // 注册默认货币（人民币、美元等）
        register(CurrencyMeta("CNY", "￥", 2))
        register(CurrencyMeta("USD", "$", 2))
    }

    // 在 CurrencyRegistry 的 init 块或应用启动时调用
    fun registerCommonCurrencies() {
        // 亚太地区
        register(CurrencyMeta("CNY", "￥", 3))    // 人民币
        register(CurrencyMeta("JPY", "¥", 0))    // 日元
        register(CurrencyMeta("KRW", "₩", 0))    // 韩元
        register(CurrencyMeta("INR", "₹", 2))    // 印度卢比
        register(CurrencyMeta("SGD", "S$", 2))   // 新加坡元
        register(CurrencyMeta("HKD", "HK$", 2))  // 港元
        register(CurrencyMeta("AUD", "A$", 2))   // 澳元
        register(CurrencyMeta("TWD", "NT$", 2))  // 新台币

        // 欧洲
        register(CurrencyMeta("EUR", "€", 2))     // 欧元
        register(CurrencyMeta("GBP", "£", 2))    // 英镑
        register(CurrencyMeta("CHF", "Fr", 2))   // 瑞士法郎
        register(CurrencyMeta("SEK", "SEK", 2))  // 瑞典克朗（符号冲突时用代码）
        register(CurrencyMeta("NOK", "NOK", 2))  // 挪威克朗
        register(CurrencyMeta("DKK", "DKK", 2))  // 丹麦克朗

        // 美洲
        register(CurrencyMeta("USD", "$", 2))    // 美元
        register(CurrencyMeta("CAD", "C$", 2))   // 加元
        register(CurrencyMeta("MXN", "Mex$", 2))  // 墨西哥比索
        register(CurrencyMeta("BRL", "R$", 2))   // 巴西雷亚尔

        // 中东及非洲
        register(CurrencyMeta("RUB", "₽", 2))     // 俄罗斯卢布
        register(CurrencyMeta("TRY", "₺", 2))    // 土耳其里拉
        register(CurrencyMeta("ZAR", "R", 2))    // 南非兰特
        register(CurrencyMeta("AED", "د.إ", 2))  // 阿联酋迪拉姆

        // 特殊场景（高精度货币）
        register(CurrencyMeta("KWD", "د.ك", 3))  // 科威特第纳尔（3位小数）
        register(CurrencyMeta("BHD", ".د.ب", 3)) // 巴林第纳尔
    }

    @Synchronized
    fun register(meta: CurrencyMeta) {
        require(!metaMap.containsKey(meta.code)) { "货币代码 ${meta.code} 已存在" }
        require(!symbolToCode.containsKey(meta.symbol)) { "货币符号 ${meta.symbol} 已存在" }

        metaMap[meta.code] = meta
        symbolToCode[meta.symbol] = meta.code
    }

    @Synchronized
    fun update(meta: CurrencyMeta) {
        require(metaMap.containsKey(meta.code)) { "货币代码 ${meta.code} 未注册" }
        metaMap[meta.code] = meta
    }

    fun getMeta(code: String): CurrencyMeta {
        return metaMap[code] ?: throw IllegalArgumentException("未注册的货币代码: $code")
    }

    fun getMetaBySymbol(symbol: String): CurrencyMeta {
        val code = symbolToCode[symbol] ?: throw IllegalArgumentException("未注册的货币符号: $symbol")
        return getMeta(code)
    }
}