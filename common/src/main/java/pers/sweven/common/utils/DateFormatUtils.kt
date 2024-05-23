package pers.sweven.common.utils

import androidx.annotation.StringDef
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Created by Sweven on 2024/4/19--22:22.
 * Email: sweventears@163.com
 */
object DateFormatUtils {
    /********************************************LocalDateTime格式字符串函数************************************/

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    const val DATE_FORMAT_19 = "yyyy-MM-dd HH:mm:ss"

    /**
     * yyyy-MM-dd HH:mm
     */
    const val DATE_FORMAT_16 = "yyyy-MM-dd HH:mm"

    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    const val DATE_FORMAT_25 = "yyyy-MM-dd HH:ss.[SSS]"

    /**
     * yyyyMMdd HHmmss
     */
    const val DATE_FORMAT_15 = "yyyyMMdd HHmmss"

    /**
     * yyyyMMddHHmmsss
     */
    const val DATE_FORMAT_14 = "yyyyMMddHHmmss"

    /**
     * 日期格式 yyyyMMdd
     */
    const val DATE_FORMAT_8 = "yyyyMMdd"

    /** yyMMdd */
    const val DATE_FORMAT_6 = "yyMMdd"

    /** yyyy-MM-dd */
    const val DATE_FORMAT_10 = "yyyy-MM-dd"

    /********************************************Time格式字符串函数************************************/

    /**
     * HH:mm
     */
    const val TIME_FORMAT_5 = "HH:mm"

    /** HH:mm:ss */
    const val TIME_FORMAT_8 = "HH:mm:ss"

    @StringDef(value = [
        DATE_FORMAT_19,
        DATE_FORMAT_16,
        DATE_FORMAT_25,
        DATE_FORMAT_15,
        DATE_FORMAT_14,
        DATE_FORMAT_8,
        DATE_FORMAT_6,
        DATE_FORMAT_10], open = false)
    annotation class DateFormat

    @StringDef(value = [TIME_FORMAT_5, TIME_FORMAT_8], open = false)
    annotation class TimeFormat

    private fun dateFormat(pattern: String, time: LocalDateTime): String {
        val df = DateTimeFormatter.ofPattern(pattern)
        return df.format(time)
    }

    private fun dateFormat(pattern: String, date: LocalDate): String {
        val df = DateTimeFormatter.ofPattern(pattern)
        return df.format(date)
    }

    fun LocalDate.formatString(@DateFormat pattern: String): String = dateFormat(pattern, this)

    fun LocalDateTime.formatString(@TimeFormat pattern: String): String = dateFormat(pattern, this)

    fun String?.formatLocalDateTime(@TimeFormat pattern: String): LocalDateTime? {
        if (this.isNullOrBlank()) return null
        var date: LocalDateTime? = null
        if (pattern.length != this.length) {
            return null
        }
        try {
            date = LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * yyyy-MM-dd 转 LocalDate
     */
    fun String?.formatLocalDate(@DateFormat pattern: String): LocalDate? {
        if (this.isNullOrBlank()) return null
        var date: LocalDate? = null
        try {
            date = LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * HH:mm:ss 转 LocalTime
     */
    fun String?.formatLocalTime(@TimeFormat pattern: String): LocalTime? {
        if (this.isNullOrBlank()) return null
        var time: LocalTime? = null
        try {
            time = LocalTime.parse(this, DateTimeFormatter.ofPattern(pattern))
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
        }
        return time
    }


    fun String?.formatDate(@DateFormat pattern: String): Date? {
        if (this.isNullOrBlank()) return null
        var date: Date? = null
        if (pattern.length != this.length) {
            return null
        }
        try {
            val sdf = SimpleDateFormat(pattern)
            date = sdf.parse(this)
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun String?.formatString(@DateFormat pattern: String, @DateFormat patternTo: String): String? {
        return this.formatDate(pattern).formatString(patternTo)
    }


    /**
     * 转为一天中最开始的时刻 0:00
     */
    fun LocalDate?.toLocalDateTimeStartOfDay(): LocalDateTime? {
        if (this == null) return this
        return LocalDateTime.of(this, LocalTime.MIN)
    }

    /**
     * 转为一天中最晚的时刻 23:59:59
     */
    fun LocalDate?.toLocalDateTimeEndOfDay(): LocalDateTime? {
        if (this == null) return this
        return LocalDateTime.of(this, LocalTime.MAX)
    }

    /**
     * Date转LocalDateTime
     */
    fun Date?.toLocalDateTime(): LocalDateTime? {
        if (this == null) return this
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    /**
     * Date转LocalDateTime
     */
    fun Date?.formatString(pattern: String): String? {
        if (this == null) return this
        return this.toLocalDateTime()?.formatString(pattern)
    }


    /**
     * 获取对比的两个时间中大的时间
     */
    fun LocalDateTime.getMaxLocalDateTime(localDateTime: LocalDateTime?): LocalDateTime {
        localDateTime ?: return this
        return if (this.isBefore(localDateTime)) localDateTime else this

    }

    /**
     * 获取对比的两个时间中小的时间
     */
    fun LocalDateTime.getMiLocalDateTime(localDateTime: LocalDateTime?): LocalDateTime {
        localDateTime ?: return this
        return if (this.isBefore(localDateTime)) this else localDateTime

    }

    /**
     * 获取明天凌晨0:0:0与当前时间相差的秒数
     * 主要用于设置redis key只在当天有效 半夜
     */
    fun getDiffSecondMidnight(): Long =
        ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay())


    /**
     * 毫秒时间戳转LocalDateTime
     */
    fun Long.millsToLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())


}