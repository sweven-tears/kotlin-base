package com.app.test.manager;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Sweven on 2023/6/9.
 * Email:sweventears@Foxmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {
    String value();
}
