package com.yqsh.wandin_weigher

import com.yqsh.wandin_weigher.DayTime.Companion.fromNull
import com.yqsh.wandin_weigher.DayTime.Companion.now
import java.io.Serializable
import java.util.Calendar
import java.util.Locale

/**
 * 日期时间信息类，封装年、月、日、时、分、秒、毫秒等字段。
 * 提供日期比较、格式化、闰年判断等常用操作，并可与 Calendar 和时间戳互转。
 *
 * Created by Sweven on 2026/6/5--15:40:55.
 * Email: sweventears@163.com
 */
class DayTime private constructor(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val millisecond: Int,
) {
    operator fun component1() = year
    operator fun component2() = month
    operator fun component3() = day
    operator fun component4() = hour
    operator fun component5() = minute
    operator fun component6() = second
    operator fun component7() = millisecond

    private var _calendar: Calendar? = null
    val calendar: Calendar
        get() = (_calendar ?: Calendar.getInstance().apply {
            set(year, month - 1, day, hour, minute, second)
            set(Calendar.MILLISECOND, millisecond)
        }.also { _calendar = it }).clone() as Calendar

    val timeInMillis: Long
        get() = calendar.timeInMillis

    val isLeapYear: Boolean
        get() = isLeapYear(year)

    /** 当前日期是星期几（1=周日, 2=周一, ..., 7=周六，遵循 Calendar 常量） */
    val week: Int get() = calendar.get(Calendar.DAY_OF_WEEK)

    /**
     * 获取当前日期是星期几的中文表示（如：周日、周一）。
     */
    val weekCn: String
        get() = when (week) {
            1 -> "周日"
            2 -> "周一"
            3 -> "周二"
            4 -> "周三"
            5 -> "周四"
            6 -> "周五"
            7 -> "周六"
            else -> ""
        }

    val isToday: Boolean get() = this == now()

    /**
     * 通过索引获取对应字段的值。
     * 索引映射：0=year, 1=month, 2=day, 3=hour, 4=minute, 5=second, 6=millisecond
     *
     * @param index 字段索引，范围 0-6
     * @return 对应字段的值
     * @throws IndexOutOfBoundsException 索引超出范围时抛出
     */
    operator fun get(index: Int) = when (index) {
        0 -> year
        1 -> month
        2 -> day
        3 -> hour
        4 -> minute
        5 -> second
        6 -> millisecond
        else -> throw IndexOutOfBoundsException("Invalid index: $index")
    }

    /**
     * 将当前日期时间与毫秒时间戳进行比较。
     *
     * @param millis 毫秒时间戳
     * @return 负数表示当前时间更早，0表示相同，正数表示当前时间更晚
     */
    fun compareToTimestamp(millis: Long): Int = timeInMillis.compareTo(millis)

    /**
     * 与另一个 DayInfo 进行比较，按年→月→日→时→分→秒→毫秒逐级比较。
     *
     * @param other 另一个 DayInfo
     * @return 负数表示当前时间更早，0表示相等，正数表示当前时间更晚
     */
    operator fun compareTo(other: DayTime): Int {
        if (this === other) return 0
        return when {
            year != other.year -> year - other.year
            month != other.month -> month - other.month
            day != other.day -> day - other.day
            hour != other.hour -> hour - other.hour
            minute != other.minute -> minute - other.minute
            second != other.second -> second - other.second
            millisecond != other.millisecond -> millisecond - other.millisecond
            else -> 0
        }
    }

    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DayTime) return false
        return year == other.year &&
                month == other.month &&
                day == other.day &&
                hour == other.hour &&
                minute == other.minute &&
                second == other.second &&
                millisecond == other.millisecond
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + day
        result = 31 * result + hour
        result = 31 * result + minute
        result = 31 * result + second
        result = 31 * result + millisecond
        return result
    }

    override fun toString(): String {
        return "DayTime(year=$year, month=$month, day=$day, hour=$hour, minute=$minute, second=$second, millisecond=$millisecond)"
    }

    /**
     * 判断是否与另一个 DayInfo 为同一天（年、月、日相同）。
     *
     * @param other 另一个 DayInfo
     * @return 如果年份、月份和日期都相同则返回 true，否则返回 false
     */
    fun isSameDay(other: DayTime): Boolean {
        return year == other.year && month == other.month && day == other.day
    }

    /**
     * 判断是否与另一个 DayInfo 为同一年（年相同）。
     *
     * @param other 另一个 DayInfo
     * @return 如果年份相同则返回 true，否则返回 false
     */
    fun isSameYear(other: DayTime): Boolean {
        return year == other.year
    }

    /**
     * 判断是否与另一个 DayInfo 为同一月（年、月相同）。
     *
     * @param other 另一个 DayInfo
     * @return 如果年份和月份都相同则返回 true，否则返回 false
     */
    fun isSameMonth(other: DayTime): Boolean {
        return year == other.year && month == other.month
    }

    /**
     * 判断当前日期时间是否在另一个 DayInfo 之后。
     *
     * @param other 另一个 DayInfo
     * @return 如果当前时间在 other 之后则返回 true，否则返回 false
     */
    fun after(other: DayTime): Boolean {
        return compareTo(other) > 0
    }

    /**
     * 判断当前日期时间是否在另一个 DayInfo 之前
     *
     * @param other 另一个 DayInfo
     * @return 如果当前时间在 other 之前则返回 true，否则返回 false
     */
    fun before(other: DayTime): Boolean {
        return compareTo(other) < 0
    }

    fun plusYear(years: Int): DayTime {
        calendar.add(Calendar.YEAR, years)
        return fromCalendar(calendar)
    }

    fun plusMonth(months: Int): DayTime {
        calendar.add(Calendar.MONTH, months)
        return fromCalendar(calendar)
    }

    fun plusDay(days: Int): DayTime {
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return fromCalendar(calendar)
    }

    fun plusHour(hours: Int): DayTime {
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return fromCalendar(calendar)
    }

    fun plusMinute(minutes: Int): DayTime {
        calendar.add(Calendar.MINUTE, minutes)
        return fromCalendar(calendar)
    }

    fun plusSecond(seconds: Int): DayTime {
        calendar.add(Calendar.SECOND, seconds)
        return fromCalendar(calendar)
    }

    /**
     * 将当前日期时间加上指定的毫秒数，并返回新的 DayTime。
     */
    operator fun plus(millis: Int): DayTime {
        calendar.add(Calendar.MILLISECOND, millis)
        return fromCalendar(calendar)
    }

    fun minusYear(years: Int) = plusYear(-years)
    fun minusMonth(months: Int) = plusMonth(-months)
    fun minusDay(days: Int) = plusDay(-days)
    fun minusHour(hours: Int) = plusHour(-hours)
    fun minusMinute(minutes: Int) = plusMinute(-minutes)
    fun minusSecond(seconds: Int) = plusSecond(-seconds)
    operator fun minus(millis: Int) = plus(-millis)

    // 当天的 00:00:00.000
    fun startOfDay(): DayTime = copy(hour = 0, minute = 0, second = 0, millisecond = 0)

    // 当天的 23:59:59.999
    fun endOfDay(): DayTime = copy(hour = 23, minute = 59, second = 59, millisecond = 999)

    // 当月第一天 00:00:00.000
    fun startOfMonth(): DayTime = copy(day = 1).startOfDay()

    // 当月最后一天
    fun endOfMonth(): DayTime = copy(day = daysOfMonth(year, month)).endOfDay()

    // 当年第一天
    fun startOfYear(): DayTime = copy(month = 1, day = 1).startOfDay()

    // 当年最后一天
    fun endOfYear(): DayTime = copy(month = 12, day = 31).endOfDay()

    @JvmOverloads
    fun copy(
        year: Int = this.year,
        month: Int = this.month,
        day: Int = this.day,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
        millisecond: Int = this.millisecond,
    ): DayTime = create(year, month, day, hour, minute, second, millisecond)

    /**
     * 计算当前日期时间与另一个 DayTime 之间的毫秒差。
     *
     * @param other 另一个 DayTime
     * @return 当前日期时间与 other 之间的毫秒差
     */
    fun diffMillis(other: DayTime): Long {
        return calendar.timeInMillis - other.calendar.timeInMillis
    }

    fun isBetween(start: DayTime, end: DayTime, inclusiveStart: Boolean = true, inclusiveEnd: Boolean = true): Boolean {
        val afterStart = if (inclusiveStart) this >= start else this > start
        val beforeEnd = if (inclusiveEnd) this <= end else this < end
        return afterStart && beforeEnd
    }

    /**
     * 将日期时间格式化为字符串。
     *
     * @param pattern     格式模板，如 "yyyy-MM-dd HH:mm:ss"
     * @param invalidText 日期不合法时返回的替代文本，默认为空字符串
     * @param locale      地区设置，默认为系统当前地区
     * @return 格式化后的字符串；日期不合法时返回 invalidText
     */
    @JvmOverloads
    fun format(
        pattern: String,
        invalidText: String = "",
        locale: Locale = Locale.getDefault(),
    ): String {
        return java.text.SimpleDateFormat(pattern, locale).format(calendar.time)
    }

    fun toDateString(): String = format("yyyy-MM-dd")
    fun toTimeString(): String = format("HH:mm:ss")
    fun toDateTimeString(): String = format("yyyy-MM-dd HH:mm:ss")
    fun toChineseDateString(): String = format("yyyy年MM月dd日")

    companion object {
        /** 获取当前时间的 DayInfo */
        @JvmStatic
        fun now(): DayTime {
            val calendar = Calendar.getInstance()
            return DayTime(
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH),
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                second = calendar.get(Calendar.SECOND),
                millisecond = calendar.get(Calendar.MILLISECOND),
            )
        }

        /** 获取今天日期的 DayInfo（同 [now]） */
        @JvmStatic
        fun today() = now()

        /**
         * 获取一个默认的空值 DayInfo，表示 1970-01-01 08:00:00.000。
         * 常用作解析失败时的默认返回值。
         */
        @JvmStatic
        fun fromNull(): DayTime {
            return DayTime(
                year = 1970,
                month = 1,
                day = 1,
                hour = 8,
                minute = 0,
                second = 0,
                millisecond = 0,
            )
        }

        /**
         * 从字符串解析为 DayInfo。
         *
         * @param text     待解析的日期时间字符串
         * @param pattern  格式模板，如 "yyyy-MM-dd"
         * @param locale   地区设置，默认为系统当前地区
         * @param default  解析失败时返回的默认值，默认为 [fromNull]
         * @return 解析成功的 DayInfo；字符串为空或解析失败时返回 default
         */
        @JvmOverloads
        @JvmStatic
        fun from(
            text: String,
            pattern: String,
            locale: Locale = Locale.getDefault(),
            default: DayTime = fromNull(),
        ): DayTime {
            if (text.isEmpty()) return default
            return java.text.SimpleDateFormat(pattern, locale).parse(text)?.let {
                val calendar = Calendar.getInstance().apply { time = it }
                DayTime(
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH) + 1,
                    day = calendar.get(Calendar.DAY_OF_MONTH),
                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                    minute = calendar.get(Calendar.MINUTE),
                    second = calendar.get(Calendar.SECOND),
                    millisecond = calendar.get(Calendar.MILLISECOND),
                )
            } ?: default
        }

        @JvmStatic
        @JvmOverloads
        fun from(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            millisecond: Int = 0,
        ): DayTime? {
            if (year !in 1970..9999) return null
            if (month !in 1..12) return null
            if (day !in 1..daysOfMonth(year, month)) return null
            if (year == 1970 && month == 1 && day == 1) {
                if (hour in 8..23) return null
            } else {
                if (hour !in 0..23) return null
            }
            if (minute !in 0..59) return null
            if (second !in 0..59) return null
            if (millisecond !in 0..999) return null
            return DayTime(
                year = year,
                month = month,
                day = day,
                hour = hour,
                minute = minute,
                second = second,
                millisecond = millisecond,
            )
        }


        /**
         * 从毫秒时间戳创建 DayInfo。
         *
         * @param millis 毫秒时间戳
         * @return 对应的 DayInfo
         */
        @JvmStatic
        fun fromMillis(millis: Long): DayTime {
            val calendar = Calendar.getInstance().apply { timeInMillis = millis }
            return fromCalendar(calendar)
        }

        /**
         * 从 Calendar 实例创建 DayInfo。
         *
         * @param calendar Calendar 实例
         * @return 对应的 DayInfo
         */
        @JvmStatic
        fun fromCalendar(calendar: Calendar): DayTime {
            return DayTime(
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH),
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                second = calendar.get(Calendar.SECOND),
                millisecond = calendar.get(Calendar.MILLISECOND),
            )
        }

        private fun create(
            year: Int, month: Int, day: Int,
            hour: Int, minute: Int, second: Int, millisecond: Int
        ): DayTime {
            require(year in 1970..9999) { "Year out of range" }
            require(month in 1..12) { "Month out of range" }
            require(day in 1..daysOfMonth(year, month)) { "Day out of range" }
            val hourValid = if (year == 1970 && month == 1 && day == 1) hour in 8..23 else hour in 0..23
            require(hourValid) { "Hour out of range for given date" }
            require(minute in 0..59) { "Minute out of range" }
            require(second in 0..59) { "Second out of range" }
            require(millisecond in 0..999) { "Millisecond out of range" }
            return DayTime(year, month, day, hour, minute, second, millisecond)
        }

        /**
         * 判断指定年份是否为闰年（能被4整除且不能被100整除，或能被400整除）。
         *
         * @param year 年份
         * @return 如果是闰年则返回 true，否则返回 false
         */
        @JvmStatic
        fun isLeapYear(year: Int): Boolean {
            return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
        }

        /**
         * 获取指定年份和月份的天数，2月根据闰年返回28或29。
         *
         * @param year 年份
         * @param month 月份
         * @return 指定年份和月份的天数
         * @throws IllegalArgumentException 如果月份无效（小于1或大于12）则抛出
         */
        @JvmStatic
        fun daysOfMonth(year: Int, month: Int): Int {
            return when (month) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> if (isLeapYear(year)) 29 else 28
                else -> throw kotlin.IllegalArgumentException("Invalid month: $month")
            }
        }
    }
}