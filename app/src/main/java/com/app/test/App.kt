package com.app.test

import android.app.Application
import android.util.Log
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig
import com.cretin.www.cretinautoupdatelibrary.model.UpdateConfig
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.app.test.manager.PageManager.Companion.initActivities
import pers.sweven.common.app.BaseApplication
import pers.sweven.common.app.PageInit
import pers.sweven.common.glide.GlideUtils
import pers.sweven.common.utils.SharedPreferencesUtil

/**
 * Created by Sweven on 2024/11/7--11:13.
 * Email: sweventears@163.com
 */
class App : BaseApplication() {
    companion object {
        lateinit var application: Application
    }

    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.color_999999, R.color.black) //全局设置主题颜色
            ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context).setDrawableSize(20f)
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        // 设置app-Shared文件名称
        SharedPreferencesUtil.initDefault(Constant.SHARED_FILE_NAME)
        // 页面监听初始化
        PageInit.init(this)

        // 页面路由初始化
        initActivities(this)

        // 更新插件初始化配置

        // 更新插件初始化配置
        val updateConfig = UpdateConfig()
            .setDebug(Constant.DEBUG)
            .setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_JSON)
            .setShowNotification(true)
            .setNotificationIconRes(R.mipmap.ic_launcher)
            .setUiThemeType(TypeConfig.UI_THEME_A)
            .setAutoDownloadBackground(false)
            .setNeedFileMD5Check(false)
        AppUpdateUtils.init(this, updateConfig)

        GlideUtils.init(this, Log.ERROR)
    }
}