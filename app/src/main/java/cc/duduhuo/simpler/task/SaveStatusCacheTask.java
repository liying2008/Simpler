package cc.duduhuo.simpler.task;

import android.os.AsyncTask;

import java.io.File;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/22 14:18
 * 版本：1.0
 * 描述：将最新微博缓存到文件中
 * 备注：
 * =======================================================
 */
public class SaveStatusCacheTask extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        if (BaseConfig.sSDCardExist) {
            // 当前用户微博的缓存文件名
            String cacheFile = Constants.Dir.CACHE_DIR + File.separator + BaseConfig.sUid + "_0";
            try {
                FileUtils.writeFile(cacheFile, params[0]);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
    }
}
