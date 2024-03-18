package pers.sweven.common.glide;

import android.graphics.drawable.Drawable;

/**
 * Created by Sweven on 2021/10/27.
 * Email:sweventears@Foxmail.com
 */
public interface OnGlideListener {
    boolean onLoadFailed(Exception e, Object model, boolean isFirstResource);

    boolean onResourceReady(Drawable resource, Object mode, boolean isFirstResource);
}
