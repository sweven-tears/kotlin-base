package pers.sweven.common.glide;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Sweven on 2021/10/27.
 * Email:sweventears@Foxmail.com
 */
public interface OnGlideListener {
    boolean onLoadFailed(Exception e, Object model, boolean isFirstResource);

    boolean onResourceReady(Drawable resource, Object mode, boolean isFirstResource);
}
