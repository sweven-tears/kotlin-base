package pers.sweven.common.utils.date

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by Sweven on 2024/10/24--16:05.
 * Email: sweventears@163.com
 */
@Deprecated(
    message = "请使用 [DayTime] 类",
    replaceWith = ReplaceWith("DayTime","pers.sweven.common.utils.date")
)
data class DayInfo(
    var year: Int,
    var month: Int,
    var day: Int,
    var hour: Int = 0,
    var minute: Int = 0,
    var second: Int = 0,
) : Serializable {

    fun plusYear(year: Int): DayInfo {
        val calendar = toCalendar()
        calendar.add(Calendar.YEAR, year)
        return from(calendar)
    }

    /**
     * 加月
     * @param [month] 月
     * @return [DayInfo]
     */
    fun plusMonth(month: Int): DayInfo {
        val calendar = toCalendar()
        calendar.add(Calendar.MONTH, month)
        return from(calendar)
    }

    /**
     * 加日
     * @param [day] 日
     * @return [DayInfo]
     */
    fun plusDay(day: Int): DayInfo {
        val calendar = toCalendar()
        calendar.add(Calendar.DAY_OF_MONTH, day)
        return from(calendar)
    }

    /**
     * 加秒
     * @param [second] 秒
     * @return [DayInfo]
     */
    fun plusSecond(second: Int): DayInfo {
        val calendar = toCalendar()
        calendar.add(Calendar.SECOND, second)
        return from(calendar)
    }

    /**
     * 减秒
     * @param [day] 日
     * @return [Long] 秒
     */
    fun minusSecond(day: DayInfo): Long {
        val l = toCalendar().timeInMillis - day.toCalendar().timeInMillis
        return l / 1000
    }


    fun getWeek(): String {
        when (getWeekInt()) {
            1 -> return "周日"
            2 -> return "周一"
            3 -> return "周二"
            4 -> return "周三"
            5 -> return "周四"
            6 -> return "周五"
            7 -> return "周六"
        }
        return ""
    }

    fun getWeekInt(): Int {
        return toCalendar().get(Calendar.DAY_OF_WEEK)
    }

    fun toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, second)
        return calendar
    }

    val timeInMillis: Long
        get() = toCalendar().timeInMillis

    /**
     * 格式化日期时间
     * @param [pattern] 模式 yyyy-MM-dd E(周) HH:mm:ss
     * @return [String] 当日期时间无效时返回空字符串
     */
    fun format(pattern: String): String {
        return format(pattern, "")
    }

    /**
     * 格式化日期时间
     * @param [pattern] 模式 yyyy-MM-dd E(周) HH:mm:ss
     * @param [invalid] 无效时文本
     * @return [String] 当日期时间无效时返回空字符串
     */
    fun format(pattern: String, invalid: String): String {
        if (isValid) {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(toCalendar().time)
        }
        return invalid
    }

    fun isToday(): Boolean = today() == this

    fun isItThisYear(): Boolean = today().year == this.year

    val isValid: Boolean
        get() = year in 1970..2500 && month in 1..12 && day in 1..getDaysOfMonth(year, month) &&
                hour in 0..23 && minute in 0..59 && second in 0..59

    fun update(
        year: Int = this.year,
        month: Int = this.month,
        day: Int = this.day,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
    ): DayInfo {
        this.year = if (year < 1970) 1970 else if (year > 2100) 2100 else year
        this.month = if (month < 1) 1 else if (month > 12) 12 else month

        val daysOfMonth = getDaysOfMonth(year, month)
        this.day = if (day < 1) 1 else if (day > daysOfMonth) daysOfMonth else day

        this.hour = if (hour < 0) 0 else if (hour > 23) 23 else hour
        this.minute = if (minute < 0) 0 else if (minute > 59) 59 else minute
        this.second = if (second < 0) 0 else if (second > 59) 59 else second
        return this
    }

    private fun getDaysOfMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> throw kotlin.IllegalArgumentException("Invalid month: $month")
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    }

    /**
     * 快速获取
     * @param [index] 第n项  [year,month,day,hour,minute,second]
     * @return [Int]
     */
    operator fun get(index: Int): Int {
        return when (index) {
            0 -> year
            1 -> month - 1
            2 -> day
            3 -> hour
            4 -> minute
            5 -> second
            else -> 0
        }
    }

    /**
     * 快速设置
     * @param [index] 第n项 [year,month,day,hour,minute,second]
     * @param [value] 值
     */
    operator fun set(index: Int, value: Int) {
        when (index) {
            0 -> year = value
            1 -> month = value
            2 -> day = value
            3 -> hour = value
            4 -> minute = value
            5 -> second = value
        }
    }

    /**
     * 仅判断是否同一天，不看具体时分秒
     * @param [other] 其他
     * @return [Boolean]
     */
    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        val (year1, month1, day1) = other as DayInfo
        return year == year1 && month == month1 && day == day1
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + day
        result = 31 * result + hour
        result = 31 * result + minute
        result = 31 * result + second
        return result
    }

    fun before(dayInfo: DayInfo): Boolean {
        return toCalendar().before(dayInfo.toCalendar())
    }

    fun after(dayInfo: DayInfo): Boolean {
        return toCalendar().after(dayInfo.toCalendar())
    }

    /**
     * 只比较年月日，时分秒不比较
     * @param [dayInfo] 当天信息
     * @return [Int]
     */
    operator fun compareTo(dayInfo: DayInfo): Int {
        var index = year - dayInfo.year
        if (index != 0) return index
        index = month - dayInfo.month
        if (index != 0) return index
        return day - dayInfo.day
    }

    /**
     * 是同一天
     * @param [other] 其他
     * @return [Boolean]
     */
    fun isSameDay(other: Any?): Boolean {
        if (other == null || other !is DayInfo) return false
        return year == other.year && month == other.month && day == other.day
    }

    companion object {
        @Deprecated(
            message = "请使用 [DayTime] 类",
            replaceWith = ReplaceWith("DayTime.today()","pers.sweven.common.utils.date")
        )
        @JvmStatic
        fun today(): DayInfo {
            val calendar = Calendar.getInstance()
            return from(calendar)
        }

        @Deprecated(
            message = "请使用 [DayTime] 类",
            replaceWith = ReplaceWith("DayTime.fromCalendar(calendar)","pers.sweven.common.utils.date")
        )
        @JvmStatic
        fun from(calendar: Calendar): DayInfo {
            return DayInfo(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            )
        }

        @Deprecated(
            message = "请使用 [DayTime] 类",
            replaceWith = ReplaceWith("DayTime.fromMillis(timeInMillis)","pers.sweven.common.utils.date")
        )
        @JvmStatic
        fun from(timeInMillis: Long): DayInfo {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis
            return from(calendar)
        }

        @Deprecated(
            message = "请使用 [DayTime] 类",
            replaceWith = ReplaceWith("DayTime.from(str, format, def)","pers.sweven.common.utils.date")
        )
        @JvmStatic
        fun from(str: String, format: String, def: DayInfo = fromNull()): DayInfo {
            if (str.isEmpty()) {
                return def
            }
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            return try {
                val date = sdf.parse(str) ?: return def
                val calendar = Calendar.getInstance()
                calendar.time = date
                from(calendar)
            } catch (e: Exception) {
                def
            }
        }

        @Deprecated(
            message = "请使用 [DayTime] 类",
            replaceWith = ReplaceWith("DayTime.fromNull()","pers.sweven.common.utils.date")
        )
        @JvmStatic
        fun fromNull(): DayInfo {
            return DayInfo(0, 0, 0)
        }

        fun fromTimeInSecond(timeInSecond: Long): DayInfo {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInSecond * 1000
            return from(calendar)
        }
    }
}