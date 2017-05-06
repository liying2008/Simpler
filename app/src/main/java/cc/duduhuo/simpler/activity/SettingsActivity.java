package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.OptionsAdapter;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Browser;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.dialog.OptionsDialog;
import cc.duduhuo.simpler.bean.Option;
import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.net.HttpListener;
import cc.duduhuo.simpler.util.AccountUtil;
import cc.duduhuo.simpler.util.CacheUtils;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.NetWorkUtils;
import cc.duduhuo.simpler.util.OptionUtil;
import cc.duduhuo.simpler.util.SettingsUtil;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.tvBrowser)
    TextView mTvBrowser;
    @BindView(R.id.tvVersion)
    TextView mTvVersion;
    @BindView(R.id.tvCacheSize)
    TextView mTvCacheSize;
    /** 选项对话框 */
    private OptionsDialog dialog;
    private String mCachePath;
    /** 下载APK的任务 */
    private BaseDownloadTask mDownloadTask;
    /** 下载任务的ID */
    private int mDownloadId;

    /** 统计图片缓存大小的任务 */
    private AsyncTask<Void, Void, String> mStatisticsTask = new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... params) {
            long size = CacheUtils.getFolderSize(new File(mCachePath));
            return CacheUtils.getFormatSize(size);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mTvCacheSize.setText(s);
        }
    };
    /** 清理图片缓存的任务 */
    private AsyncTask<Void, Void, Boolean> mCleanTask = new AsyncTask<Void, Void, Boolean>() {

        @Override
        protected Boolean doInBackground(Void... params) {
            return CacheUtils.deleteFolderFile(mCachePath, false);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                AppToast.showToast(R.string.clear_done);
                mTvCacheSize.setText("0.0B");
            } else {
                AppToast.showToast(R.string.clear_error);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        dialog = new OptionsDialog(this);
        init();
    }

    private void init() {
        // 当前版本名称
        mTvVersion.setText(getString(R.string.version_name_prefix, App.getInstance().getVersionName()));
        if (BaseSettings.sSettings.browser == Browser.BUILT_IN) {
            mTvBrowser.setText("内置浏览器");
        } else if (BaseSettings.sSettings.browser == Browser.SYSTEM) {
            mTvBrowser.setText("系统浏览器");
        }
        // 获取图片缓存大小
        if (BaseConfig.sSDCardExist) {
            mCachePath = getExternalCacheDir() + File.separator + Constants.Dir.IMAGE_CACHE_DIR;
        } else {
            mCachePath = getCacheDir() + File.separator + Constants.Dir.IMAGE_CACHE_DIR;
        }
        // 统计图片缓存大小
        countCacheSize();
    }

    /**
     * 统计缓存大小
     */
    private void countCacheSize() {
        mStatisticsTask.execute();
        registerAsyncTask(SettingsActivity.class, mStatisticsTask);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    /**
     * 打开网页版微博
     */
    @OnClick(R.id.rlWebWB)
    void openWebWB() {
        startActivity(WebWBActivity.newIntent(this, false));
    }

    @OnClick(R.id.rlSettingDisplay)
    void setDisplay() {
        startActivity(DisplaySettingsActivity.newIntent(this));
    }

    @OnClick(R.id.rlSettingBrowser)
    void setBrowser() {
        final List<Option> browserOptions = OptionUtil.getBrowserOptions();
        dialog.setTitle("选择默认网页浏览器");
        dialog.setOptions(browserOptions);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvBrowser.setText(browserOptions.get(position).name);
                SettingsUtil.updateBrowser((int) browserOptions.get(position).value);
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rlDelCookie)
    void delCookie() {
        if (TextUtils.isEmpty(BaseConfig.sAccount.cookie)) {
            AppToast.showToast(R.string.no_cookie);
            return;
        }
        final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null);
        TextView tvTitle = ButterKnife.findById(view, R.id.tvTitle);
        TextView tvMsg = ButterKnife.findById(view, R.id.tvMsg);
        TextView tv1 = ButterKnife.findById(view, R.id.tv1);
        TextView tv2 = ButterKnife.findById(view, R.id.tv2);
        tvTitle.setText("清除Cookie");
        tvMsg.setGravity(Gravity.LEFT);
        tvMsg.setText("清除Cookie后，应用将无法显示微博中的图片链接中的图片，无法加载热门微博和话题。（清除Cookie后，可以通过应用中的相关引导再次获取Cookie。）\n\n确定要清除吗？");
        tv1.setText("清除");
        tv2.setText("不清除");
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AccountUtil.updateCookie("");
                AppToast.showToast(R.string.cookie_clear);
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @OnClick(R.id.rlSettingNotification)
    void setNotification() {
        startActivity(NotificationSettingsActivity.newIntent(this));
    }

    @OnClick(R.id.rlSettingFeedback)
    void setFeedback() {
        String text = getString(R.string.feedback_start, getString(R.string.app_name),
                App.getInstance().getVersionName(), android.os.Build.VERSION.RELEASE);
        String hint = "请在此填写您的反馈意见";
        startActivity(WBPostActivity.newIntent(this, "意见反馈", text, hint));
    }

    @OnClick(R.id.rlSettingUpdate)
    void setUpdate() {
        if (!NetWorkUtils.isConnectedByState(this)) {
            AppToast.showToast(R.string.network_unavailable);
            return;
        }
        AppToast.showToast(R.string.checking_update);
        HttpGetTask task = new HttpGetTask(false, new HttpListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int versionCode = obj.optInt("version_code", 0);
                    if (versionCode > App.getInstance().getVersionCode()) {
                        // 有新版本
                        String versionName = obj.optString("version_name");
                        String size = obj.optString("size");
                        String updateDate = obj.optString("update_date");
                        final String downloadUrl = obj.optString("download_url");
                        String log = obj.optString("log");

                        final AlertDialog dialog = App.getAlertDialogBuilder(SettingsActivity.this).create();
                        View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_update, null);
                        TextView tvVersionName = ButterKnife.findById(view, R.id.tvVersionName);
                        TextView tvSize = ButterKnife.findById(view, R.id.tvSize);
                        TextView tvUpdateDate = ButterKnife.findById(view, R.id.tvUpdateDate);
                        TextView tvUpdateLog = ButterKnife.findById(view, R.id.tvUpdateLog);

                        tvVersionName.setText(versionName);
                        tvSize.setText(size);
                        tvUpdateDate.setText(updateDate);
                        tvUpdateLog.setText(log);

                        TextView tvCancel = ButterKnife.findById(view, R.id.tvCancel);
                        TextView tvUpdate = ButterKnife.findById(view, R.id.tvUpdate);
                        dialog.setView(view);
                        dialog.show();

                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tvUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                // 下载新版本
                                showDownloadDialog(downloadUrl);
                            }
                        });
                    } else {
                        // 没有新版本
                        AppToast.showToast(R.string.no_new_version);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppToast.showToast(R.string.resolve_result_failure);
                }
            }

            @Override
            public void onFailure() {
                AppToast.showToast(R.string.check_update_failure);
            }
        });
        task.execute(Constants.UPDATE_PATH);
    }

    /**
     * 显示下载进度Dialog
     */
    private void showDownloadDialog(final String downloadUrl) {
        final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_downloading, null);
        ProgressBar pb = ButterKnife.findById(view, R.id.pb);
        TextView tvProgress = ButterKnife.findById(view, R.id.tvProgress);
        TextView tvBackground = ButterKnife.findById(view, R.id.tvBackground);
        final TextView tvPause = ButterKnife.findById(view, R.id.tvPause);
        dialog.setView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String path = Constants.Dir.WORK_DIR + File.separator + CommonUtils.getFileNameFromUrl(downloadUrl);
        final WeakReference<TextView> wrProgress = new WeakReference<>(tvProgress);
        final WeakReference<ProgressBar> wrPb = new WeakReference<>(pb);
        final WeakReference<AlertDialog> wrDialog = new WeakReference<>(dialog);
        mDownloadTask = createAPKDownloadTask(downloadUrl, path, wrProgress.get(), wrPb.get(), wrDialog.get());
        mDownloadId = mDownloadTask.start();

        tvBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mDownloadTask.getStatus() == FileDownloadStatus.paused) {
                    mDownloadTask = createAPKDownloadTask(downloadUrl, path, wrProgress.get(),
                            wrPb.get(), wrDialog.get());
                    mDownloadId = mDownloadTask.start();
                }
            }
        });
        tvPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadTask.getStatus() == FileDownloadStatus.progress) {
                    FileDownloader.getImpl().pause(mDownloadId);
                    tvPause.setText("继续");
                } else if (mDownloadTask.getStatus() == FileDownloadStatus.paused) {
                    mDownloadTask = createAPKDownloadTask(downloadUrl, path, wrProgress.get(),
                            wrPb.get(), wrDialog.get());
                    mDownloadId = mDownloadTask.start();
                    tvPause.setText("暂停");
                }
            }
        });
    }

    @OnClick(R.id.rlCleanCache)
    void cleanCache() {
        final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null);
        TextView tvTitle = ButterKnife.findById(view, R.id.tvTitle);
        TextView tvMsg = ButterKnife.findById(view, R.id.tvMsg);
        TextView tv1 = ButterKnife.findById(view, R.id.tv1);
        TextView tv2 = ButterKnife.findById(view, R.id.tv2);
        tvTitle.setText("清除图片缓存");
        tvMsg.setGravity(Gravity.LEFT);
        tvMsg.setText("确定删除所有图片缓存吗？（清理缓存不会删除已经保存的图片）");
        tv1.setText("清除");
        tv2.setText("不清除");
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppToast.showToast(R.string.clearing_image_cache);
                clearImageCache();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 清理图片缓存
     */
    private void clearImageCache() {
        if (mStatisticsTask != null && mStatisticsTask.getStatus() != AsyncTask.Status.FINISHED) {
            mStatisticsTask.cancel(true);
        }
        mCleanTask.execute();
        registerAsyncTask(SettingsActivity.class, mCleanTask);
    }

    @OnClick(R.id.rlSettingAbout)
    void setAbout() {
        startActivity(AboutActivity.newIntent(this));
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(SettingsActivity.class);
        super.onDestroy();
        dialog = null;
    }
}
