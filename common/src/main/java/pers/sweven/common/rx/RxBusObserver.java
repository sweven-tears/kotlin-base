package pers.sweven.common.rx;

import android.app.Activity;

import androidx.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Rxbus信息处理<p>
 * 默认处理完就关闭接收器,默认：{@link RxBusObserver#isDisposable} = true<p>
 * Created by Sweven on 2021/11/19.
 * Email:sweventears@Foxmail.com
 */
public abstract class RxBusObserver<T> implements Observer<T> {
    private Disposable disposable;
    private Activity activity;
    private boolean isDisposable = true;

    public RxBusObserver() {
    }

    public RxBusObserver(Activity activity) {
        this.activity = activity;
    }

    public RxBusObserver(Activity activity, boolean isDisposable) {
        this.activity = activity;
        this.isDisposable = isDisposable;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(@NonNull T data) {
        try {
            onSuccess(data);
        } catch (Exception e) {
            onError(e);
        } finally {
            onComplete();
        }
    }

    protected abstract void onSuccess(T data);

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (disposable != null && !disposable.isDisposed() && isDisposable) {
            disposable.dispose();
        }
    }
}
