package pers.sweven.common.glide;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * 结合 ImageEngine
 * Glide4.x的加载图片引擎实现,单例模式
 * Glide4.x的缓存机制更加智能，已经达到无需配置的境界。如果使用Glide3.x，需要考虑缓存机制。
 * Created by huan on 2018/1/15.
 */
public class GlideEngine {
    public void loadPhoto(Context context, String photoPath, ImageView imageView) {
        Glide.with(context).load(photoPath).transition(withCrossFade()).into(imageView);
    }

    public void loadGifAsBitmap(Context context, String gifPath, ImageView imageView) {
        Glide.with(context).asBitmap().load(gifPath).into(imageView);
    }

    public void loadGif(Context context, String gifPath, ImageView imageView) {
        Glide.with(context).asGif().load(gifPath).transition(withCrossFade()).into(imageView);
    }

    public Bitmap getCacheBitmap(Context context, String path, int width, int height) throws Exception {
        return Glide.with(context).asBitmap().load(path).submit(width, height).get();
    }
}