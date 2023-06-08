package pers.sweven.common.glide;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.CheckResult;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.net.URL;

import pers.sweven.common.glide.transform.BlurTransformation;
import pers.sweven.common.glide.transform.CircleTransformation;
import pers.sweven.common.glide.transform.RoundTransformation;

/**
 * @author xuhao
 * @date 2018/6/11 17:36
 * @desc 图片加载工具类
 */
public class GlideUtils {
    private final Context context;
    private ImageView iv;
    private Object loadModel;
    private int placeholder;
    private boolean circle;
    private int roundedCorner;
    private int crossFade;
    private OnGlideListener onGlideListener;
    private boolean isGif;
    private int blur;

    private GlideUtils(Context context, ImageView iv) {
        this.context = context;
        this.iv = iv;
    }

    public static GlideUtils with(ImageView iv) {
        return new GlideUtils(iv.getContext(), iv);
    }

    public static GlideUtils with(Activity activity) {
        return new GlideUtils(activity, null);
    }

    public static GlideUtils with(Fragment fragment) {
        return new GlideUtils(fragment.getContext(), null);
    }

    public static GlideUtils with(FragmentActivity activity) {
        return new GlideUtils(activity, null);
    }

    public static void loadImage(ImageView iv, String url) {
        loadImage(iv, url, 0);
    }

    public static void loadImage(ImageView iv, String url, int emptyImg) {
        GlideUtils.with(iv)
                .load(url)
                .setPlaceholder(emptyImg)
                .into();
    }

    /**
     * 加载圆形图片
     */
    public static void loadCircleImage(ImageView iv, String url, int emptyImg) {
        GlideUtils.with(iv)
                .load(url)
                .setPlaceholder(emptyImg)
                .asCircle()
                .into();
    }

    /**
     * 加载圆角图片
     */
    public static void loadRoundImage(ImageView iv, String url, int emptyImg) {
        GlideUtils.with(iv)
                .load(url)
                .setRoundedCorner(20)
                .setPlaceholder(emptyImg)
                .into();
    }

    public void into(ImageView iv) {
        this.iv = iv;
        into();
    }

    @SuppressLint("CheckResult")
    public void into() {
        if (iv == null) {
            Log.e("GlideUtils", "imageView can't is null");
            return;
        }
        RequestManager with = Glide.with(context);
        if (isGif) {
            // 解决加载gif出现黑边的异常
            RequestOptions options = new RequestOptions().set(GifOptions.DECODE_FORMAT, PREFER_ARGB_8888);
            with.setDefaultRequestOptions(options);
        }
        RequestBuilder<Drawable> load = with.load(loadModel);

        if (placeholder > 0) {
            load.error(placeholder).placeholder(placeholder);
        }

        if (crossFade > 0) {
            load.transition(new DrawableTransitionOptions().crossFade(crossFade));
        }

        if (circle) {
            load.transform(new CircleTransformation());
        } else if (roundedCorner > 0) {
            load.transform(new RoundTransformation(roundedCorner));
        }
        if (blur > 0) {
            load.transform(new BlurTransformation(blur));
        }

        if (onGlideListener != null) {
            load.addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return onGlideListener.onLoadFailed(e, model, isFirstResource);
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return onGlideListener.onResourceReady(resource, model, isFirstResource);
                }
            });
        }

        load.into(iv);
    }

    private GlideUtils loadModel(Object model) {
        this.loadModel = model;
        return this;
    }

    public GlideUtils load(@Nullable Bitmap bitmap) {
        return loadModel(bitmap);
    }

    @NonNull
    @CheckResult
    public GlideUtils load(@Nullable Drawable drawable) {
        return loadModel(drawable);
    }

    @NonNull
    @CheckResult
    public GlideUtils load(@Nullable String string) {
        return loadModel(string);
    }

    @NonNull
    @CheckResult
    public GlideUtils load(@Nullable Uri uri) {
        return loadModel(uri);
    }

    @NonNull
    @CheckResult
    public GlideUtils load(@Nullable File file) {
        return loadModel(file);
    }

    @NonNull
    @CheckResult
    public GlideUtils load(@RawRes @DrawableRes @Nullable Integer resourceId) {
        return loadModel(resourceId);
    }

    @CheckResult
    @Deprecated
    public GlideUtils load(@Nullable URL url) {
        return loadModel(url);
    }

    @NonNull
    @CheckResult
    public GlideUtils load(@Nullable byte[] model) {
        return loadModel(model);
    }

    /**
     * @param placeholder 占位符
     * @return 设置占位符
     */
    public GlideUtils setPlaceholder(int placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * @return 设置圆形图片
     */
    public GlideUtils asCircle() {
        this.circle = true;
        return this;
    }

    /**
     * @param roundedCorner 圆角幅度
     * @return 设置加载圆角图片
     */
    public GlideUtils setRoundedCorner(int roundedCorner) {
        this.roundedCorner = roundedCorner;
        return this;
    }

    /**
     * @param duration 加载时长间隔
     * @return 有占位符的情况下，进行交叉褪色变换
     */
    public GlideUtils crossFade(int duration) {
        this.crossFade = duration;
        return this;
    }

    /**
     * @return 设置当前为gif图，并处理黑边异常
     */
    public GlideUtils asGif() {
        this.isGif = true;
        return this;
    }

    public GlideUtils toBlur(int radius) {
        this.blur = radius;
        return this;
    }

    /**
     * @param onGlideListener 加载监听
     * @return 设置加载图片失败的监听
     */
    public GlideUtils setOnGlideListener(OnGlideListener onGlideListener) {
        this.onGlideListener = onGlideListener;
        return this;
    }
}
