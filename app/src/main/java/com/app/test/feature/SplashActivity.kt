package com.app.test.feature

import com.app.test.R
import com.app.test.databinding.ActivitySplashBinding
import com.app.test.data.event.Event
import pers.sweven.common.base.BaseActivity
import pers.sweven.common.base.BaseViewModel
import pers.sweven.common.rx.RxBus
import pers.sweven.common.rx.RxBusObserver
import pers.sweven.common.rx.RxUtil
import pers.sweven.common.utils.ToastUtils
import pers.sweven.common.widget.SItemDecoration

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

        SItemDecoration(20)
    }

    override fun doBusiness() {
        RxBus.getDefault().post(Event.SplashClose("page"))
    }
}