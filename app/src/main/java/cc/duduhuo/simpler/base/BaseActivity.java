package cc.duduhuo.simpler.base;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.AccessTokenKeeper;
import cc.duduhuo.simpler.util.AccountUtil;
import cc.duduhuo.simpler.util.FileUtils;
import cc.duduhuo.simpler.util.PrefsUtils;
import cc.duduhuo.simpler.util.SettingsUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/2 23:17
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    /** 存储所有Activity中的异步任务，便于统一删除 */
    private Multimap<Class<? extends BaseActivity>, AsyncTask> mTasks = ArrayListMultimap.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化重要数据
        if ("".equals(BaseConfig.sUid)) {
            BaseConfig.sUid = PrefsUtils.getString(Constants.PREFS_CUR_UID, "");
        }
        BaseConfig.sSDCardExist = FileUtils.hasStorage();
        BaseConfig.sAccount = AccountUtil.readAccount(BaseConfig.sUid, false);
        BaseSettings.sSettings = SettingsUtil.readSettings(BaseConfig.sUid, false);
        if (BaseConfig.sAccessToken == null) {
            BaseConfig.sAccessToken = AccessTokenKeeper.readAccessToken(BaseConfig.sUid, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 创建一个下载任务
     *
     * @param url  下载地址
     * @param path 下载路径（含文件名）
     * @return
     */
    protected BaseDownloadTask createDownloadTask(String url, String path, final TextView textView, final boolean share) {
        return FileDownloader.getImpl().create(url)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                        if (textView != null) {
                            textView.setText("等待下载");
                        }
                        AppToast.showToast("等待下载");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        int rate = soFarBytes * 100 / totalBytes;
                        if (textView != null) {
                            textView.setText("已下载 " + rate + "%");
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        if (textView != null) {
                            textView.setText("下载失败");
                        }
                        AppToast.showToast("下载失败：" + e.getMessage());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        if (textView != null) {
                            textView.setText("已暂停");
                        }
                        AppToast.showToast("已暂停");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        if (textView != null) {
                            textView.setText("下载完成");
                        }
                        if (share) {
                            // 分享图片
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(task.getTargetFilePath())));
                            shareIntent.setType("image/*");
                            startActivity(Intent.createChooser(shareIntent, "分享图片到"));
                        } else {
                            AppToast.showToast(task.getFilename() + " 下载完成");
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        if (textView != null) {
                            textView.setText("任务重复");
                        }
                        AppToast.showToast("任务重复");
                    }

                    @Override
                    protected void started(BaseDownloadTask task) {
                        super.started(task);
                        if (textView != null) {
                            textView.setText("开始下载");
                        }
                        AppToast.showToast("开始下载");
                    }
                });
    }

    /**
     * 创建一个下载任务
     *
     * @param url  下载地址
     * @param path 下载路径（含文件名）
     * @return
     */
    protected BaseDownloadTask createAPKDownloadTask(String url, String path, final TextView tvProgress,
                                                     final ProgressBar pb, final AlertDialog dialog) {
        return FileDownloader.getImpl().create(url)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                        if (tvProgress != null) {
                            tvProgress.setText("等待");
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        int rate = soFarBytes * 100 / totalBytes;
                        if (tvProgress != null) {
                            tvProgress.setText(rate + "%");
                        }
                        if (pb != null) {
                            pb.setProgress(rate);
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        if (tvProgress != null) {
                            tvProgress.setText("失败");
                        }
                        AppToast.showToast("下载失败：" + e.getMessage());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        if (tvProgress != null) {
                            tvProgress.setText("已暂停");
                        }
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        // 下载完成，开始安装
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(new File(task.getTargetFilePath())),
                                "application/vnd.android.package-archive");
                        startActivity(intent);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        AppToast.showToast("任务重复");
                    }

                    @Override
                    protected void started(BaseDownloadTask task) {
                        super.started(task);
                        if (tvProgress != null) {
                            tvProgress.setText("开始");
                        }
                    }
                });
    }

    /**
     * 注册任务
     *
     * @param clazz Activity Class
     * @param task  AsyncTask对象
     */
    public void registerAsyncTask(Class<? extends BaseActivity> clazz, AsyncTask task) {
        boolean b = mTasks.put(clazz, task);
        if (!b) {
            Log.e(TAG, "任务注册异常");
        }
    }

    /**
     * 如果任务未执行完毕，则取消任务
     *
     * @param clazz Activity Class
     */
    public void unregisterAsyncTask(Class<? extends BaseActivity> clazz) {
        ArrayList<AsyncTask> tasks = new ArrayList<>(mTasks.removeAll(clazz));
        int size = tasks.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                AsyncTask task = tasks.get(i);
                //you may call the cancel() method but if it is not handled in doInBackground() method
                if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                    task.cancel(true);
                }
            }
        }
    }
}
