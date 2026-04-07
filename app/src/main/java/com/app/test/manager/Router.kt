package com.app.test.manager


/**
 * Created by Sweven on 2023/6/9.
 * Email:sweventears@Foxmail.com
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Router(val value: String,val login: Boolean = false)
