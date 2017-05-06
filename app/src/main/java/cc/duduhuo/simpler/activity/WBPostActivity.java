package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.imageselector.ImageLoader;
import cc.duduhuo.imageselector.ImageSelectActivity;
import cc.duduhuo.imageselector.ImageSelectConfig;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.PostPhotosAdapter;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.Draft;
import cc.duduhuo.simpler.bean.MenuItem;
import cc.duduhuo.simpler.bean.Photo;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.db.DraftServices;
import cc.duduhuo.simpler.fragment.GroupSelectFragment;
import cc.duduhuo.simpler.listener.GroupChangeListener;
import cc.duduhuo.simpler.listener.PostPicClickListener;
import cc.duduhuo.simpler.task.PostStatusTask;
import cc.duduhuo.simpler.util.BDLocationUtil;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.InputMethodUtils;
import cc.duduhuo.weibores.entities.Emoticon;
import cc.duduhuo.weibores.fragment.EmoticonPanelFragment;
import cc.duduhuo.weibores.utils.EmoticonUtils;

public class WBPostActivity extends BaseActivity implements EmoticonPanelFragment.OnEmoticonClickListener {
    private static final String TAG = "WBPostActivity";
    private static final String INTENT_TITLE = "title";
    private static final String INTENT_TEXT = "text";
    private static final String INTENT_HINT = "hint";
    private static final String INTENT_DRAFT = "draft";
    public static final String INTENT_REFRESH = "refresh";
    private static StringBuilder sContentBuilder;
    /** 微博字数（中文字符个数） */
    private static int sLength = 0;
    /** 可见分组 */
    private String mGroupId = "";
    /** 请求码 */
    private static final int REQUEST_ALBUM_CODE = 0;  // 从相册选择
    private static final int REQUEST_CAMERA_CODE = 1;// 拍照
    private static final int REQUEST_AT_CODE = 2;   // AT
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.etContent)
    EditText mEtContent;
    @BindView(R.id.tvLocation)
    TextView mTvLocation;
    @BindView(R.id.tvCount)
    TextView mTvCount;
    @BindView(R.id.tvVisible)
    TextView mTvVisible;
    @BindView(R.id.rvPhotos)
    RecyclerView mRvPhotos;
    @BindView(R.id.ivEmoticon)
    ImageView mIvEmoticon;
    @BindView(R.id.flEmoticon)
    FrameLayout mFlEmoticon;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private PostPhotosAdapter mPhotoAdapter;
    /** 上传的图片列表 */
    private List<Photo> mPhotoList = new ArrayList<>();
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    private PostPicClickListener postPicClickListener = new PostPicClickListener() {
        @Override
        public void onAdd() {
            openImageSelect();  // 打开图片选择器
        }

        @Override
        public void onDelete(int position) {
            mPhotoList.remove(position);
            mPhotoAdapter.deleteData(position);
        }
    };

    private ImageSelectConfig mImageSelectConfig;
    /** 拍照的照片名字 */
    private String capturePhotoName;
    /** 微博表情面板Fragment */
    private EmoticonPanelFragment mEmoticonFragment;
    /** 纬度 */
    private double mLatitude;
    /** 经度 */
    private double mLongitude;
    /** 是否上传定位（默认不上传） */
    private boolean mIsEnableGeo = false;
    /** 是否已经获取过位置信息 */
    private boolean mIsLocation = false;
    private static final int HANDLE_ADDRESS_OK = 0x0000;
    private static final int HANDLE_SERVER_ERROR = 0x0001;
    private static final int HANDLE_NETWORK_EXCEPTION = 0x0002;
    private static final int HANDLE_CRITERIA_EXCEPTION = 0x0003;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ADDRESS_OK:
                    if (!TextUtils.isEmpty(mAddrStr)) {
                        // 地址信息不空
                        mTvLocation.setVisibility(View.VISIBLE);
                        mTvLocation.setText(mAddrStr);
                    }
                    AppToast.showToast(R.string.location_ok);
                    mIsLocation = true;
                    // 停止定位。只获取一次结果
                    mLocationClient.stop();
                    break;
                case HANDLE_SERVER_ERROR:
                    AppToast.showToast(R.string.location_type_server_error);
                    break;
                case HANDLE_NETWORK_EXCEPTION:
                    AppToast.showToast(R.string.location_type_network_exception);
                    break;
                case HANDLE_CRITERIA_EXCEPTION:
                    AppToast.showToast(R.string.location_type_criteria_exception);
                    break;
                default:
                    break;
            }
            return true;
        }
    });
    private String mAddrStr = null;
    private int mMenuId = MenuItem.MENU_ID_ALL_VISIBLE;
    private DraftServices mDraftServices;
    private Draft mOldDraft = null;
    /** 分组名称 */
    private String mGroupName = "所有人可见";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_post);
        ButterKnife.bind(this);

        if ("".equals(BaseConfig.sUid)) {
            // 没有授权用户
            AppToast.showToast("请先登录");
            App.getInstance().finishAllActivities();
            startActivity(WBLoginActivity.newIntent(this));
        }

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.setLocOption(BDLocationUtil.getLocationClientOption());

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvPhotos.setLayoutManager(linearLayoutManager);
        mPhotoAdapter = new PostPhotosAdapter(this, postPicClickListener);
        mRvPhotos.setAdapter(mPhotoAdapter);

        sContentBuilder = new StringBuilder();
        mEtContent.requestFocus();
        // 添加文本变化监听器
        mEtContent.addTextChangedListener(new StatusTextWatcher());

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            mTvTitle.setText("分享给好友");
            if ("text/plain".equals(type)) {
                // 处理发来的文字
                String string = intent.getStringExtra(Intent.EXTRA_TEXT);
                mEtContent.setText(string);
                mEtContent.setSelection(mEtContent.length());
            } else if (type.startsWith("image/")) {
                // 处理发送来的图片
                handleSendImage(intent);
            } else if (type.startsWith("*/*")) {
                // 处理发送来的未知数据
                handleSendUnknown(intent);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                // 处理发送来的多张图片
                handleSendMultipleImages(intent);
            } else if (type.startsWith("*/*")) {
                // 处理发送来的多个未知数据
                handleSendMultipleUnknown(intent);
            }
        } else {
            String title = intent.getStringExtra(INTENT_TITLE);
            mOldDraft = intent.getParcelableExtra(INTENT_DRAFT);
            if (mOldDraft != null) {
                init();     // 初始化发博器
            }
            if (!TextUtils.isEmpty(title)) {
                String text = intent.getStringExtra(INTENT_TEXT);
                String hint = intent.getStringExtra(INTENT_HINT);
                mTvTitle.setText(title);
                mEtContent.setHint(hint);
                mEtContent.setText(text);
                mEtContent.setSelection(mEtContent.length());
            }
        }
        // 打开输入法软键盘
        InputMethodUtils.openSoftKeyboard(this, mEtContent);
    }

    private void handleSendMultipleUnknown(Intent intent) {
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        mEtContent.setText("分享照片");
        mEtContent.setSelection(mEtContent.length());
        if (uris != null && !uris.isEmpty()) {
            int size = uris.size();
            if (size > 9) {
                size = 9;
            }
            Uri uri;
            for (int i = 0; i < size; i++) {
                uri = uris.get(i);
                String path = uri.getPath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                    mPhotoList.add(new Photo(path));
                }
            }
            mPhotoAdapter.setData(mPhotoList);
        }
    }

    private void handleSendUnknown(Intent intent) {
        Uri data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (data != null) {
            String path = data.getPath();
            if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                mEtContent.setText("分享照片");
                mEtContent.setSelection(mEtContent.length());
                mPhotoList.add(new Photo(path));
                mPhotoAdapter.setData(mPhotoList);
            }
        }
    }

    private void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        mEtContent.setText("分享照片");
        mEtContent.setSelection(mEtContent.length());
        if (imageUris != null && !imageUris.isEmpty()) {
            int size = imageUris.size();
            if (size > 9) {
                size = 9;
            }
            Uri uri;
            for (int i = 0; i < size; i++) {
                uri = imageUris.get(i);
                mPhotoList.add(new Photo(uri.getPath()));
            }
            mPhotoAdapter.setData(mPhotoList);
        }
    }

    private void handleSendImage(Intent intent) {
        Uri data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        mEtContent.setText("分享照片");
        mEtContent.setSelection(mEtContent.length());
        mPhotoList.add(new Photo(data.getPath()));
        mPhotoAdapter.setData(mPhotoList);
    }

    /**
     * 初始化发博器
     */
    private void init() {
        EmoticonUtils.handleEmoticonText(mEtContent, mOldDraft.content, this);
        mEtContent.setSelection(mEtContent.length());
        mIsEnableGeo = mOldDraft.isEnableGeo;
        mIsLocation = mOldDraft.isLocation;
        if (mIsLocation) {
            mAddrStr = mOldDraft.addrStr;
            mTvLocation.setText(mOldDraft.addrStr);
            if (mIsEnableGeo) {
                mTvLocation.setVisibility(View.VISIBLE);
            } else {
                mTvLocation.setVisibility(View.GONE);
            }
            mLatitude = mOldDraft.latitude;
            mLongitude = mOldDraft.longitude;
        }
        mMenuId = mOldDraft.menuId;
        mGroupId = mOldDraft.groupId;
        mGroupName = mOldDraft.groupName;
        mTvVisible.setText(mGroupName);

        int size = mOldDraft.photoList.size();
        if (size > 0) {
            for (int i = 0; i < mOldDraft.photoList.size(); i++) {
                String path = mOldDraft.photoList.get(i).path;
                if ((new File(path)).exists()) {
                    mPhotoList.add(mOldDraft.photoList.get(i));
                }
            }
            if (!mPhotoList.isEmpty()) {
                mPhotoAdapter.setData(mPhotoList);
            }
        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, WBPostActivity.class);
        return intent;
    }

    public static Intent newIntent(Context context, String title, String text, String hint) {
        Intent intent = new Intent(context, WBPostActivity.class);
        intent.putExtra(INTENT_TITLE, title);
        intent.putExtra(INTENT_TEXT, text);
        intent.putExtra(INTENT_HINT, hint);
        return intent;
    }

    public static Intent newIntent(Context context, Draft draft) {
        Intent intent = new Intent(context, WBPostActivity.class);
        intent.putExtra(INTENT_DRAFT, draft);
        return intent;
    }

    @OnClick(R.id.tvPost)
    void post() {
        if ("".equals(sContentBuilder.toString().trim())) {
            AppToast.showToast(R.string.post_content_empty);
            return;
        }
        int visible = 0;
        if (mMenuId == MenuItem.MENU_ID_ALL_VISIBLE) {
            visible = 0;
        } else if (mMenuId == MenuItem.MENU_ID_GROUP) {
            visible = 3;
        }

        AppToast.showToast(R.string.posting_status);
        PostStatusTask task = new PostStatusTask(this, sContentBuilder.toString(), mPhotoList,
                String.valueOf(mLatitude), String.valueOf(mLongitude), visible, mGroupId);
        task.execute();
        task.setOnPostStatusListener(new PostStatusTask.OnPostStatusListener() {
            @Override
            public void onPostSuccess() {
                AppToast.showToast(R.string.post_status_success);
                finish();   // 关闭
            }

            @Override
            public void onPostFailure() {
                AppToast.showToast(R.string.post_status_failure);
            }

            @Override
            public void onPicUploadFail() {
                AppToast.showToast(R.string.post_pics_failure);
            }
        });
        registerAsyncTask(WBPostActivity.class, task);
    }

    /**
     * 定位和取消定位
     */
    @OnClick(R.id.ivLocation)
    void location() {
        mIsEnableGeo = !mIsEnableGeo;
        if (mIsEnableGeo) {
            if (mIsLocation) {
                mTvLocation.setVisibility(View.VISIBLE);
            } else {
                AppToast.showToast(R.string.location_running);
                mLocationClient.start();
            }
        } else {
            mTvLocation.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.ivPhoto)
    void photo() {
        final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
        View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_post_photos_ways, null);
        TextView tvCapture = (TextView) rootView.findViewById(R.id.tvCapture);
        TextView tvAlbum = (TextView) rootView.findViewById(R.id.tvAlbum);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
        dialog.show();
        DialogUtil.setBottom(dialog, rootView);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                capture();
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openImageSelect();
            }
        });
    }

    /**
     * 拍照
     */
    private void capture() {
        capturePhotoName = CommonUtils.capture(this, REQUEST_CAMERA_CODE);
    }

    /**
     * 打开图片选择器
     */
    private void openImageSelect() {
        // 图片选择器配置
        if (mImageSelectConfig == null) {
            mImageSelectConfig = new ImageSelectConfig.Builder(loader)
                    .multiSelect(true)      // 多选模式
                    .rememberSelected(false) // 不记住上次选中记录
                    .needCamera(false)      // 不显示拍照
                    .build();
        }
        // 打开图片选择器
        ImageSelectActivity.startActivity(WBPostActivity.this, mImageSelectConfig, REQUEST_ALBUM_CODE);
    }

    /**
     * 切换表情/输入法
     */
    @OnClick(R.id.ivEmoticon)
    void emoticon() {
        if (mEmoticonFragment == null) {
            mEmoticonFragment = EmoticonPanelFragment.Instance();
            // 隐藏软键盘
            InputMethodUtils.closeSoftKeyboard(this);
            getSupportFragmentManager().beginTransaction().add(R.id.flEmoticon, mEmoticonFragment).commit();
            mIvEmoticon.setBackgroundResource(R.drawable.ic_post_keyboard_dark);
        } else {
            if (mEmoticonFragment.isHidden()) {
                // 隐藏软键盘
                InputMethodUtils.closeSoftKeyboard(this);
                getSupportFragmentManager().beginTransaction().show(mEmoticonFragment).commit();
                mIvEmoticon.setBackgroundResource(R.drawable.ic_post_keyboard_dark);
            } else {
                getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
                // 开启软键盘
                InputMethodUtils.openSoftKeyboard(this, mEtContent);
                mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
            }
        }
    }

    @OnClick(R.id.ivAt)
    void at() {
        startActivityForResult(AtFriendActivity.newIntent(this), REQUEST_AT_CODE);
        if (mEmoticonFragment != null && !mEmoticonFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
            mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
        }
    }

    @OnClick(R.id.ivTopic)
    void topic() {
        mEtContent.setText(sContentBuilder.toString() + "##");
        EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
        mEtContent.setSelection(mEtContent.length() - 1);
    }

    @OnClick(R.id.tvBack)
    void back() {
        // 保存草稿
        saveDraft();
    }

    @OnClick(R.id.tvVisible)
    void visible() {
        GroupSelectFragment groupFragment = new GroupSelectFragment();
        groupFragment.setGroupChangeListener(new GroupChangeListener() {
            @Override
            public void onGroupChange(int menuId, String groupId, String groupName) {
                mGroupName = groupName;
                mTvVisible.setText(groupName);
                mMenuId = menuId;
                mGroupId = groupId;
            }
        });
        groupFragment.show(getSupportFragmentManager(), "group_select_fragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM_CODE && resultCode == RESULT_OK && data != null) {
            // 得到所选图片的绝对路径
            List<String> pathList = data.getStringArrayListExtra(ImageSelectActivity.INTENT_RESULT);
            if (mPhotoList.size() + pathList.size() > 9) {
                AppToast.showToast(R.string.pictures_no_more_than_9);
                return;
            }
            for (int i = 0; i < pathList.size(); i++) {
                mPhotoList.add(new Photo(pathList.get(i)));
            }
            mPhotoAdapter.setData(mPhotoList);
        } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
            if (capturePhotoName != null) {
                Photo photo = new Photo(capturePhotoName);
                if (mPhotoList.size() <= 8) {
                    mPhotoList.add(photo);
                    mPhotoAdapter.addData(photo);
                } else {
                    AppToast.showToast(R.string.pictures_no_more_than_9);
                }
            }
        } else if (requestCode == REQUEST_AT_CODE && resultCode == RESULT_OK) {
            String at = data.getStringExtra(AtFriendActivity.INTENT_AT);
            mEtContent.setText(sContentBuilder.toString() + "@" + at + " ");
            EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
            mEtContent.setSelection(mEtContent.length());
        }
    }

    /**
     * 获取点击事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideEmoticon(view, ev)) {
                if (mEmoticonFragment != null) {
                    getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
                    mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏表情面板 <br />
     * 如果点击的区域是EditText，则会唤起输入法面板，此时需要隐藏表情面板 <br />
     *
     * @param v
     * @param ev
     * @return
     */
    private boolean isHideEmoticon(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mEmoticonFragment != null && !mEmoticonFragment.isHidden()) {
            // 隐藏表情面板
            getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
            mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
        } else {
            saveDraft();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_top_in, R.anim.push_bottom_out);
    }

    /**
     * 保存草稿
     */
    private void saveDraft() {
        if (mEtContent.length() == 0 && mAddrStr == null && mPhotoList.size() == 0
                && mMenuId == MenuItem.MENU_ID_ALL_VISIBLE) {
            // 没做任何更改
            finish();
        } else {
            // 提示保存草稿
            final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null);
            TextView tvTitle = ButterKnife.findById(view, R.id.tvTitle);
            TextView tvMsg = ButterKnife.findById(view, R.id.tvMsg);
            TextView tv1 = ButterKnife.findById(view, R.id.tv1);
            TextView tv2 = ButterKnife.findById(view, R.id.tv2);
            tvTitle.setText("微博未发布");
            tvMsg.setText("要保存到草稿箱中吗？");
            tv1.setText("不保存");
            tv2.setText("保存");
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 保存到草稿箱
                    if (mDraftServices == null) {
                        mDraftServices = new DraftServices(WBPostActivity.this);
                    }
                    Draft draft;
                    if (mOldDraft == null) {
                        draft = new Draft(BaseConfig.sUid, sContentBuilder.toString(), mPhotoList,
                                mLatitude, mLongitude, mIsEnableGeo, mIsLocation, mAddrStr,
                                mMenuId, mGroupId, mGroupName);
                        mDraftServices.insertDraft(draft);
                    } else {
                        draft = new Draft(mOldDraft.id, mOldDraft.uid, sContentBuilder.toString(),
                                mPhotoList, mLatitude, mLongitude, mIsEnableGeo, mIsLocation,
                                mAddrStr, mMenuId, mGroupId, mGroupName);
                        mDraftServices.updateDraft(draft);
                        Intent data = new Intent();
                        data.putExtra(INTENT_REFRESH, draft);
                        setResult(RESULT_OK, data);
                    }
                    dialog.dismiss();
                    AppToast.showToast(R.string.draft_saved);
                    finish();
                }
            });
            dialog.setView(view);
            dialog.show();
        }
    }

    @Override
    public void onEmoticonDelete() {
        String text = mEtContent.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                // 模拟按键
                mEtContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
                return;
            }
            mEtContent.getText().delete(index, text.length());
            EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        mEtContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
    }

    @Override
    public void onEmoticonClick(Emoticon emoticon) {
        String s = sContentBuilder.toString();
        String selection = s.substring(0, mEtContent.getSelectionEnd()) + emoticon.getContent();
        mEtContent.setText(selection + s.substring(mEtContent.getSelectionEnd()));
        EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
        mEtContent.setSelection(selection.length());
    }

    /**
     * 微博文本变化监听器
     */
    private class StatusTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            sContentBuilder.delete(0, sContentBuilder.length());
            sContentBuilder.append(s);
//            Log.d(TAG, "内容：" + sContentBuilder.toString());
            sLength = CommonUtils.calcLength(s.toString());
            mTvCount.setText(sLength + "/140");
            if (sLength > 140) {
                mTvCount.setTextColor(ContextCompat.getColor(WBPostActivity.this, R.color.wb_post_text_count_color_exceed));
            } else {
                mTvCount.setTextColor(ContextCompat.getColor(WBPostActivity.this, R.color.wb_post_text_count_color_normal));
            }
        }
    }

    /**
     * 百度地图定位结果回调接口
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // 在回调方法运行在remote进程
            // 获取定位类型
            int locType = location.getLocType();
            if (locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation
                    || locType == BDLocation.TypeOffLineLocation) {
                // 定位成功。GPS定位结果 || 网络定位结果 || 离线定位结果
                // 纬度
                mLatitude = location.getLatitude();
                // 经度
                mLongitude = location.getLongitude();
                // 获取地址信息
                mAddrStr = location.getAddrStr();
                mHandler.sendEmptyMessage(HANDLE_ADDRESS_OK);
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                mHandler.sendEmptyMessage(HANDLE_SERVER_ERROR);
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                mHandler.sendEmptyMessage(HANDLE_NETWORK_EXCEPTION);
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                mHandler.sendEmptyMessage(HANDLE_CRITERIA_EXCEPTION);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(WBPostActivity.class);
        super.onDestroy();
    }

}
