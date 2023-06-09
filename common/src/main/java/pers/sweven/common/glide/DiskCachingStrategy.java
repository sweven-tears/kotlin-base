package pers.sweven.common.glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Sweven on 2023/6/9.
 * Email:sweventears@Foxmail.com
 */
public class DiskCachingStrategy {
    public static final DiskCacheStrategy ALL = DiskCacheStrategy.ALL;

    /**
     * Saves no data to cache.
     */
    public static final DiskCacheStrategy NONE = DiskCacheStrategy.NONE;

    /**
     * Writes retrieved data directly to the disk cache before it's decoded.
     */
    public static final DiskCacheStrategy DATA = DiskCacheStrategy.DATA;

    /**
     * Writes resources to disk after they've been decoded.
     */
    public static final DiskCacheStrategy RESOURCE = DiskCacheStrategy.RESOURCE;

    /**
     * Tries to intelligently choose a strategy based on the data source of the {@link
     * com.bumptech.glide.load.data.DataFetcher} and the {@link
     * com.bumptech.glide.load.EncodeStrategy} of the {@link com.bumptech.glide.load.ResourceEncoder}
     * (if an {@link com.bumptech.glide.load.ResourceEncoder} is available).
     */
    public static final DiskCacheStrategy AUTOMATIC = DiskCacheStrategy.AUTOMATIC;

}
