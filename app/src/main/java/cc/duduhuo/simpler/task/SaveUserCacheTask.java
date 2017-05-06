package cc.duduhuo.simpler.task;

import android.os.AsyncTask;

import java.io.File;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/8 14:56
 * 版本：1.0
 * 描述：缓存用户信息任务类
 * 备注：
 * =======================================================
 */
public class SaveUserCacheTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        if (BaseConfig.sSDCardExist) {
            // 缓存当前用户信息的文件名
            String cacheFile = Constants.Dir.CACHE_DIR + File.separator + BaseConfig.sUid + "_1";
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
