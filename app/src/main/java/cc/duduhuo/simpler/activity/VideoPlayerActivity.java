package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.CommonUtils;

public class VideoPlayerActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "Video";
    /** Intent传值的键（视频播放页面地址） */
    private static final String INTENT_MIAO_PAI_URL = "miao_pai_url";
    /** Intent传值的键（视频播放地址） */
    private static final String INTENT_VIDEO_URL = "video_url";

    @BindView(R.id.video)
    VideoView mVideoView;
    @BindView(R.id.video_loading)
    LinearLayout mLoadingView;
    @BindView(R.id.llScreenLock)
    LinearLayout mLlScreenLock;
    @BindView(R.id.flFunc)
    FrameLayout mFlFunc;
    @BindView(R.id.ivLock)
    ImageView mIvLock;
    @BindView(R.id.ivOp)
    ImageView mIvOp;
    @BindView(R.id.tvDownload)
    TextView mTvDownload;

    private GestureDetector mGestureDetector;
    /** 上次点击Back键的时间 */
    private long mLastBackKeyTime;
    /** 是否锁定屏幕 */
    private boolean mScreenLocked = false;
    /** 秒拍页面地址 */
    private String mMiaoPaiUrl;
    private String mVideoUrl;
    /** 视频下载任务 */
    private BaseDownloadTask mDownloadTask;
    /** 下载任务Id */
    private int mDownloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏虚拟按键
        WindowManager.LayoutParams params = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.setAttributes(params);
        // 强行开启屏幕旋转效果
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mMiaoPaiUrl = intent.getStringExtra(INTENT_MIAO_PAI_URL);
        mVideoUrl = intent.getStringExtra(INTENT_VIDEO_URL);

        setListener();  // 设置监听器
        init();         // 初始化VideoView
    }

    public static Intent newIntent(Context context, String miaoPaiUrl, String videoUrl) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(INTENT_MIAO_PAI_URL, miaoPaiUrl);
        intent.putExtra(INTENT_VIDEO_URL, videoUrl);
        return intent;
    }

    private void setListener() {
        mLlScreenLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 锁定/解锁屏幕
                if (mScreenLocked) {
                    lockScreen(false);
                    mIvLock.setBackgroundResource(R.drawable.unlock_screen);
                    mScreenLocked = false;
                } else {
                    lockScreen(true);
                    mIvLock.setBackgroundResource(R.drawable.lock_screen);
                    mScreenLocked = true;
                }
            }
        });
        mTvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadTask == null) {
                    WeakReference<TextView> textView = new WeakReference<>(mTvDownload);
                    String path = Constants.Dir.PIC_DIR + File.separator + CommonUtils.getFileNameFromUrl(mVideoUrl);
                    mDownloadTask = createDownloadTask(mVideoUrl, path, textView.get(), false);
                    mDownloadId = mDownloadTask.start();
                } else {
                    if (mDownloadTask.getStatus() == FileDownloadStatus.progress) {
                        FileDownloader.getImpl().pause(mDownloadId);
                    } else if (mDownloadTask.getStatus() == FileDownloadStatus.paused) {
                        WeakReference<TextView> textView = new WeakReference<>(mTvDownload);
                        String path = Constants.Dir.PIC_DIR + File.separator + CommonUtils.getFileNameFromUrl(mVideoUrl);
                        mDownloadTask = createDownloadTask(mVideoUrl, path, textView.get(), false);
                        mDownloadId = mDownloadTask.start();
                    }
                }
            }
        });
        mIvOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = App.getAlertDialogBuilder(VideoPlayerActivity.this);
                View rootView = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.dialog_video_op, null);
                TextView tvCopyVideoUrl = (TextView) rootView.findViewById(R.id.tvCopyVideoUrl);
                TextView tvCopyWebUrl = (TextView) rootView.findViewById(R.id.tvCopyWebUrl);
                TextView tvOpen = (TextView) rootView.findViewById(R.id.tvOpen);
                builder.setView(rootView);
                final AlertDialog dialog = builder.create();
                tvCopyVideoUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.copyText(VideoPlayerActivity.this, mVideoUrl);
                        dialog.dismiss();
                    }
                });
                tvCopyWebUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.copyText(VideoPlayerActivity.this, mMiaoPaiUrl);
                        dialog.dismiss();
                    }
                });
                tvOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.openBrowser(VideoPlayerActivity.this, mMiaoPaiUrl);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void init() {
        BaseConfig.sPositionWhenPaused = -1;
        // Create media controller
        MediaController mediaController = new MediaController(this);
        // 设置MediaController
        mVideoView.setMediaController(mediaController);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mVideoView.setOnInfoListener(this);
        } else {
            mLoadingView.setVisibility(View.GONE);
        }
        mVideoView.setOnPreparedListener(this);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        Uri uri = Uri.parse(mVideoUrl);
        mVideoView.setVideoURI(uri);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, mLlScreenLock.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE");
            mFlFunc.setVisibility(mLlScreenLock.getVisibility());
            if (mLlScreenLock.getVisibility() == View.VISIBLE) {
                mLlScreenLock.setVisibility(View.GONE);
                mFlFunc.setVisibility(View.GONE);
            } else {
                mLlScreenLock.setVisibility(View.VISIBLE);
                mFlFunc.setVisibility(View.VISIBLE);
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        SystemClock.sleep(3000);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mLlScreenLock.setVisibility(View.GONE);
                        mFlFunc.setVisibility(View.GONE);
                    }
                }.execute();
            }
            return super.onSingleTapConfirmed(e);
        }

    }

    /**
     * 锁定/解锁屏幕方向
     *
     * @param lockScreen
     */
    public void lockScreen(boolean lockScreen) {
        if (lockScreen) {
            Configuration config = getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                AppToast.showToast("横屏已锁定");
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                AppToast.showToast("竖屏已锁定");
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            AppToast.showToast("屏幕锁定已解除");
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "播放完成");
        this.finish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        AppToast.showToast("播放失败");
        Log.d(TAG, "播放失败, " + what + ", " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.d(TAG, "MEDIA_ERROR_IO");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop video when the activity is pause.
        BaseConfig.sPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.pause();
        Log.d(TAG, "OnStop: sPositionWhenPaused = " + BaseConfig.sPositionWhenPaused);
        Log.d(TAG, "OnStop: getDuration  = " + mVideoView.getDuration());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume video player
        if (BaseConfig.sPositionWhenPaused >= 0) {
            if (mVideoView.canSeekForward()) {
                mVideoView.seekTo(BaseConfig.sPositionWhenPaused);
                mVideoView.start();
            }
            BaseConfig.sPositionWhenPaused = -1;
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // 当一些特定信息出现或者警告时触发
        Log.d(TAG, "INFO = " + what + "/" + extra);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START");
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mLoadingView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                mVideoView.start();
                mLoadingView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                mLoadingView.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 按下返回键
            long delay = Math.abs(System.currentTimeMillis() - mLastBackKeyTime);
            if (delay > 3000) {
                AppToast.showToast("再按一次，退出播放");
                mLastBackKeyTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        super.onDestroy();
    }
}
