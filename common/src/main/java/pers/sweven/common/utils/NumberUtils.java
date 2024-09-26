package pers.sweven.common.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Created by Sweven on 2023/7/26--14:53.
 * Email: sweventears@163.com
 */
public class NumberUtils {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    /**
     * @param str 字符串
     * @return 是否为数值（不可带+号）
     */
    public static boolean isNumeric(String str) {
        return str != null && NUMBER_PATTERN.matcher(str).matches();
    }

    public static int parseInt(String str) {
        if (isNumeric(str)) {
            return (int) parseDouble(str);
        }
        return 0;
    }

    public static long parseLong(String str) {
        if (isNumeric(str)) {
            return (long) parseDouble(str);
        }
        return 0;
    }

    public static float parseFloat(String str) {
        if (isNumeric(str)) {
            return Float.parseFloat(str);
        }
        return 0;
    }

    public static double parseDouble(String str) {
        if (isNumeric(str)) {
            return Double.parseDouble(str);
        }
        return 0;
    }

    public static double parseDouble(String s, int newScale) {
        return parseDouble(s, newScale, BigDecimal.ROUND_HALF_UP);
    }


    public static double parseDouble(String s, int newScale, @RoundingMode int roundingMode) {
        if (!isNumeric(s)) {
            s = "0";
        }
        BigDecimal decimal = new BigDecimal(s);
        return decimal.setScale(newScale, roundingMode).doubleValue();
    }

    public static double parseDouble(double s, int newScale, @RoundingMode int roundingMode) {
        BigDecimal decimal = new BigDecimal(s);
        return decimal.setScale(newScale, roundingMode).doubleValue();
    }

    @IntDef({
            BigDecimal.ROUND_UP,
            BigDecimal.ROUND_DOWN,
            BigDecimal.ROUND_CEILING,
            BigDecimal.ROUND_FLOOR,
            BigDecimal.ROUND_HALF_UP,
            BigDecimal.ROUND_HALF_DOWN,
            BigDecimal.ROUND_HALF_EVEN,
            BigDecimal.ROUND_UNNECESSARY
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface RoundingMode {
    }
}
