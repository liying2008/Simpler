package cc.duduhuo.simpler.task;

import android.os.AsyncTask;

import java.io.File;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.LoadStatusCacheListener;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/22 13:59
 * 版本：1.0
 * 描述：加载微博缓存
 * 备注：
 * =======================================================
 */
public class LoadStatusCacheTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * 缓存的微博字符串
     */
    private String mStatusStr = null;
    private LoadStatusCacheListener mListener;

    public LoadStatusCacheTask(LoadStatusCacheListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BaseConfig.sSDCardExist) {
            // 当前用户微博的缓存文件名
            String cacheFile = Constants.Dir.CACHE_DIR + File.separator + BaseConfig.sUid + "_0";
            if (FileUtils.isFileExist(cacheFile)) {
                mStatusStr = FileUtils.readFileContent(cacheFile);
                if (mStatusStr != null) {
                    return true;
                } else {
                    // 文件为空
                    return false;
                }
            } else {
                // 缓存文件不存在，则从服务器加载新微博
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean cacheLoad) {
        super.onPostExecute(cacheLoad);
        if (cacheLoad) {
            if (mListener != null) {
                mListener.cacheLoaded(mStatusStr);
            }
        } else {
            if (mListener != null) {
                mListener.noCache();
            }
        }
    }
}
