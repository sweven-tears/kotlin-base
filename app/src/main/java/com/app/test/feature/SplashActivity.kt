package com.app.test.feature

import com.app.test.R
import com.app.test.base.BaseActivity
import com.app.test.base.BaseViewModel
import com.app.test.data.event.Event
import com.app.test.databinding.ActivitySplashBinding
import pers.sweven.common.rx.RxBus
import pers.sweven.common.rx.RxBusObserver
import pers.sweven.common.rx.RxUtil
import pers.sweven.common.utils.ToastUtils

class SplashActivity :
    BaseActivity<ActivitySplashBinding, BaseViewModel>(R.layout.activity_splash) {
    override fun initView() {
        RxBus.getDefault()
            .toObservable(Event.SplashClose::class.java)
            .compose(RxUtil.applySchedulers())
            .subscribe(object : RxBusObserver<Event.SplashClose>() {
                override fun onSuccess(data: Event.SplashClose) {
                    ToastUtils.showShort(data.page)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                }
            })
    }

    override fun doBusiness() {
        RxBus.getDefault().post(Event.SplashClose("page"))
    }
}