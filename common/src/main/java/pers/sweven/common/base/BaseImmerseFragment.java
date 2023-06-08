package pers.sweven.common.base;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.components.SimpleImmersionOwner;
import com.gyf.immersionbar.components.SimpleImmersionProxy;
import com.trello.rxlifecycle2.components.support.RxFragment;

public class BaseImmerseFragment extends RxFragment implements SimpleImmersionOwner {
    /**
     * 将代理类通用行为抽出来
     */
    private final SimpleImmersionProxy mSimpleImmersionProxy = new SimpleImmersionProxy(this);

    @Override
    public void initImmersionBar() {

    }

    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSimpleImmersionProxy.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mSimpleImmersionProxy.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mSimpleImmersionProxy.onHiddenChanged(hidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSimpleImmersionProxy.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSimpleImmersionProxy.onDestroy();
    }
}