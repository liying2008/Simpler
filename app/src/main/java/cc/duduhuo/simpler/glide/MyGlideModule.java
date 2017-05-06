package cc.duduhuo.simpler.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.File;
import java.io.InputStream;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.Constants;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/19 19:34
 * 版本：1.0
 * 描述：Glide全局配置
 * 备注：
 * =======================================================
 */
public class MyGlideModule implements GlideModule {
    private int extDiskSize = 1024 * 1024 * 1024;
    private int intDiskSize = 1024 * 1024 * 512;
//    private int memorySize = (int) (Runtime.getRuntime().maxMemory()) / 8;  // 取1/8最大内存作为最大缓存

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glide_tag_id);
        // Apply options to the builder here.
        // 默认内存和图片池大小
//        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
//        int defaultMemoryCacheSize = calculator.getMemoryCacheSize(); // 默认内存大小
//        int defaultBitmapPoolSize = calculator.getBitmapPoolSize(); // 默认图片池大小
//        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize)); // 该两句无需设置，是默认的
//        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));
        //定义图片的本地磁盘缓存
//        File cacheDir = context.getExternalCacheDir();//指定的是数据的缓存地址
//        int diskCacheSize = 1024 * 1024 * 1024;//最多可以缓存多少字节的数据
        //设置磁盘缓存大小
//        builder.setDiskCache(new DiskLruCacheFactory(cacheDir.getPath(), "glide", diskCacheSize));
        // 定义缓存大小和位置
        if (BaseConfig.sSDCardExist) {
            builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, Constants.Dir.IMAGE_CACHE_DIR, extDiskSize)); //外部存储
        } else {
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, Constants.Dir.IMAGE_CACHE_DIR, intDiskSize));  //内部存储
        }
        // 自定义内存和图片池大小
//        builder.setMemoryCache(new LruResourceCache(memorySize));
//        builder.setBitmapPool(new LruBitmapPool(memorySize));
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
        // 加载进度
        glide.register(String.class, InputStream.class, new ProgressModelLoader.Factory());
    }
}
