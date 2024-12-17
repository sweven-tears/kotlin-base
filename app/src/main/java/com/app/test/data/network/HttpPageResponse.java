package com.app.test.data.network;

import com.app.test.data.entity.Page;

/**
 * <br/>
 * Created by Sweven on 2023/9/26--13:14.
 * Email: sweventears@163.com
 */
public class HttpPageResponse<T> extends Page<T> {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
