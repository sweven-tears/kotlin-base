package pers.sweven.common.repository.exception;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.IOException;
import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import retrofit2.HttpException;

/**
 * 建议自定义，不建议直接使用
 * author sweven
 * 2021/8/16 09:16
 */
@Deprecated
public class ApiException extends IOException {
    private int code;
    private ExceptionEntity entity;
    private String tag;
    private Throwable e;

    public ApiException(String tag, int code, ExceptionEntity entity) {
        super(entity.getMessage());
        this.code = code;
        this.tag = tag;
        this.entity = entity;
    }

    public ApiException(int code, String message, Throwable e) {
        super(message);
        this.e = e;
        this.code = code;
        entity = new ExceptionEntity();
        entity.setMessage(message);
    }

    public static ApiException handleException(Throwable e, String tag) {
        ApiException exception = handleException(e);
        exception.tag = tag;
        return exception;
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof ApiException) {
            ex = (ApiException) e;
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(httpException.code(), httpException.getMessage(), e);
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接超时，请检查您的网络状态，稍后重试！", e);
        } else if (e instanceof ConnectException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接异常，请检查您的网络状态，稍后重试！", e);
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接超时，请检查您的网络状态，稍后重试！", e);
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(ERROR.TIMEOUT_ERROR, "网络连接异常，请检查您的网络状态，稍后重试！", e);
        } else if (e instanceof NullPointerException) {
            ex = new ApiException(ERROR.NULL_POINTER_EXCEPTION, "空指针异常", e);
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(ERROR.SSL_ERROR, "证书验证失败", e);
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(ERROR.CAST_ERROR, "类型转换错误", e);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new ApiException(ERROR.PARSE_ERROR, "解析错误", e);
        } else if (e instanceof IllegalStateException) {
            ex = new ApiException(ERROR.ILLEGAL_STATE_ERROR, e.getMessage(), e);
        } else {
            ex = new ApiException(ERROR.UNKNOWN, e.getMessage(), e);
        }
        return ex;
    }

    public boolean isRoute(String value) {
        return tag.contains(value);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ExceptionEntity getEntity() {
        return entity;
    }

    public void setEntity(ExceptionEntity entity) {
        this.entity = entity;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        public static final int LOG_OUT = 225;
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1001;
        /**
         * 空指针错误
         */
        public static final int NULL_POINTER_EXCEPTION = 1002;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1003;

        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = 1004;

        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1005;

        /**
         * 非法数据异常
         */
        public static final int ILLEGAL_STATE_ERROR = 1006;

    }
}
