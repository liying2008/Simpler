package cc.duduhuo.simpler.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.util.concurrent.ExecutionException;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.util.AccountUtil;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/6 11:08
 * 版本：1.0
 * 描述：加载用户头像任务类
 * 备注：
 * =======================================================
 */
public class LoadAvatarTask extends AsyncTask<Void, Void, File> {
    private Activity mActivity;
    private ImageView mIv;
    private String mAvatarLarge;

    public LoadAvatarTask(Activity activity, ImageView iv) {
        this.mActivity = activity;
        this.mIv = iv;
    }

    @Override
    protected File doInBackground(Void... voids) {
        // 用户头像地址（大图），180×180像素
        mAvatarLarge = BaseConfig.sUser.avatar_large;
        // 更新帐号信息
        AccountUtil.updateHeadUrl(mAvatarLarge);
        if (BaseConfig.sSDCardExist) {
            // 用户头像本地存储路径
            String localAvatarPath = Constants.Dir.AVATAR_DIR + File.separator + BaseConfig.sUid;

            RequestManager requestManager = Glide.with(mActivity);
            FutureTarget<File> target = requestManager.load(mAvatarLarge).downloadOnly(180, 180);
            try {
                File file = target.get();
                // 拷贝下载的头像文件到SD卡下的应用工作目录
                FileUtils.copyFile(file.getAbsolutePath(), localAvatarPath);
                // 更新数据库信息
                AccountUtil.updateHeadCachePath(localAvatarPath);
                return file;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (file != null) {
            // 从存储器加载用户头像
            Glide.with(mActivity).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIv);
        } else {
            // 从网络加载用户头像
            Glide.with(mActivity).load(mAvatarLarge).into(mIv);
        }
    }
}
