package cc.duduhuo.simpler.task;

import android.os.AsyncTask;

import java.io.File;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.LoadUserCacheListener;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/22 13:59
 * 版本：1.0
 * 描述：加载用户信息缓存
 * 备注：
 * =======================================================
 */
public class LoadUserCacheTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * 缓存的用户信息字符串
     */
    private String mUserStr = null;
    private LoadUserCacheListener mListener;

    public LoadUserCacheTask(LoadUserCacheListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BaseConfig.sSDCardExist) {
            // 当前用户微博的缓存文件名
            String cacheFile = Constants.Dir.CACHE_DIR + File.separator + BaseConfig.sUid + "_1";
            if (FileUtils.isFileExist(cacheFile)) {
                mUserStr = FileUtils.readFileContent(cacheFile);
                if (mUserStr != null) {
                    return true;
                } else {
                    // 文件为空
                    return false;
                }
            } else {
                // 缓存文件不存在
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
                mListener.userCacheLoaded(mUserStr);
            }
        } else {
            if (mListener != null) {
                mListener.noUserCache();
            }
        }
    }
}
