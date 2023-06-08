package pers.sweven.common.rx;

import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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
        } else if (provider instanceof RxDialogFragment){
          return provider.bindUntilEvent(FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("lifecycle bind error");
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
                .doOnSubscribe(disposable -> viewModel.getShowLoading().setValue(true))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> viewModel.getShowLoading().setValue(false))
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
