package com.app.test.data.network;

/**
 * Created by Sweven on 2023/8/29--14:20.
 * Email: sweventears@163.com
 */
public class HttpResponse<T> {
    private String message;
    private T data;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
