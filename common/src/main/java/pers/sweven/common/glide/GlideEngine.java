package pers.sweven.common.glide;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.concurrent.ExecutionException;

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


    //------------------------------------PictureSelector的加载器

    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public void loadImage(Context context, ImageView imageView, String url, int maxWidth, int maxHeight) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .override(maxWidth, maxHeight)
                .into(imageView);
    }

    public void loadAlbumCover(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .override(180, 180)
                .sizeMultiplier(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(8))
                .into(imageView);
    }

    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        Glide.with(context)
                .load(url)
                .override(200, 200)
                .centerCrop()
                .into(imageView);
    }

    public void pauseRequests(Context context) {
        Glide.with(context).pauseRequests();
    }

    public void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }


    //---------------------------------- easy photos --------------------------------
    public void loadPhoto(Context context, Uri uri, ImageView imageView) {
        GlideUtils.with(context)
                .load(uri)
                .into(imageView);
    }

    public void loadGifAsBitmap(Context context, Uri gifUri, ImageView imageView) {
        GlideUtils.with(context)
                .asGif()
                .load(gifUri)
                .into(imageView);
    }

    public void loadGif(Context context, Uri gifUri, ImageView imageView) {
        GlideUtils.with(context).asGif().load(gifUri).into(imageView);
    }

    public Bitmap getCacheBitmap(Context context, Uri uri, int width, int height) throws ExecutionException, InterruptedException {
        return GlideUtils.with(context).asBitmap().load(uri).submit(width, height).get();
    }
}