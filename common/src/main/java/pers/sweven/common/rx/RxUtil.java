package pers.sweven.common.rx;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pers.sweven.common.base.BaseViewModel;

/**
 * Created by Sweven on 2021/7/28--22:59.
 * Email: sweventears@163.com
 */
public class RxUtil {
    /**
     * RxJava 绑定生命周期
     *
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull LifecycleProvider provider) {
        if (provider instanceof RxAppCompatActivity) {
            return provider.bindUntilEvent(ActivityEvent.DESTROY);
        } else if (provider instanceof RxFragment) {
            return provider.bindUntilEvent(FragmentEvent.DESTROY);
        } else if (provider instanceof RxDialogFragment) {
            return provider.bindUntilEvent(FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("lifecycle bind error:" + provider);
        }
    }


    /**
     * RxJava处理线程调度，绑定生命周期
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(BaseViewModel viewModel) {
        return observable -> observable.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle(viewModel.getProvider()));
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(BaseViewModel viewModel, boolean showLoading) {
        return observable -> observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    if (showLoading) viewModel.getShowLoading().setValue(true);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (showLoading) viewModel.getShowLoading().setValue(false);
                })
                .compose(bindToLifecycle(viewModel.getProvider()));
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(BaseViewModel viewModel, boolean showLoading, boolean alwaysLoading, long timeoutMillis) {
        AtomicBoolean timeoutTriggered = new AtomicBoolean(false);
        Disposable[] timeoutDisposable = { null };

        return observable -> observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    if (showLoading) viewModel.getShowLoading().setValue(true);

                    // 设置超时加载
                    if (timeoutMillis > 0) {
                        timeoutDisposable[0] = Observable.timer(timeoutMillis, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(__ -> {
                                    timeoutTriggered.set(true);
                                    viewModel.getShowLoading().postValue(true);
                                },throwable -> {});
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    // 取消超时任务
                    if (timeoutDisposable[0] != null) {
                        timeoutDisposable[0].dispose();
                    }

                    if (!alwaysLoading) {
                        boolean needHide = showLoading || timeoutTriggered.get();
                        if (needHide) {
                            viewModel.getShowLoading().postValue(false);
                        }
                    }
                })
                .compose(bindToLifecycle(viewModel.getProvider()));
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(BaseViewModel viewModel, boolean showLoading, boolean alwaysLoading) {
        return observable -> observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    if (showLoading) viewModel.getShowLoading().setValue(true);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (showLoading && !alwaysLoading) viewModel.getShowLoading().setValue(false);
                })
                .compose(bindToLifecycle(viewModel.getProvider()));
    }

    /**
     * RxJava处理线程调度
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
