package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.imageselector.ImageLoader;
import cc.duduhuo.imageselector.ImageSelectActivity;
import cc.duduhuo.imageselector.ImageSelectConfig;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.impl.AvatarOp;
import cc.duduhuo.simpler.task.LoadAvatarTask;
import cc.duduhuo.simpler.util.AccessTokenKeeper;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.FileUtils;
import cc.duduhuo.simpler.util.NetWorkUtils;
import cc.duduhuo.simpler.util.NumberFormatter;
import cc.duduhuo.simpler.util.UserVerify;

/**
 * 授权用户的个人中心
 */
public class UserHomepageActivity extends BaseActivity {
    /** 请求码：拍照 */
    private static final int REQUEST_CODE_CAPTURE = 0;
    /** 请求码：从相册选择 */
    private static final int REQUEST_CODE_ALBUM = 1;
    /** 请求码：裁剪拍到的图片 */
    private static final int REQUEST_CODE_CLIP = 2;
    /** 下拉刷新布局 */
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    /** 主内容区域 */
    @BindView(R.id.svContent)
    ScrollView svContent;
    /** 授权用户头像 */
    @BindView(R.id.civHeadIcon)
    ImageView mCivHeadIcon;
    /** 认证用户标识 */
    @BindView(R.id.ivAvatarVip)
    ImageView mIvAvatarVip;
    /** 上传头像按钮 */
    @BindView(R.id.tvUploadPic)
    TextView mTvUploadPic;
    /** 授权用户昵称 */
    @BindView(R.id.tvScreenName)
    TextView mTvScreenName;
    /** 用户性别标识 */
    @BindView(R.id.ivGender)
    ImageView mIvGender;
    /** 用户所在地区 */
    @BindView(R.id.tvLocation)
    TextView mTvLocation;
    /** 用户的个人描述 */
    @BindView(R.id.tvDescription)
    TextView mTvDescription;
    /** 用户粉丝数量 */
    @BindView(R.id.tvFollowersCount)
    TextView mTvFollowersCount;
    /** 用户关注的数量 */
    @BindView(R.id.tvFriendsCount)
    TextView mTvFriendsCount;
    /** 用户微博的数量 */
    @BindView(R.id.tvStatusesCount)
    TextView mTvStatusesCount;

    private User mCurUser;
    private UsersAPI mUsersAPI;
    /** 上传头像的图片路径 */
    private String mAvatarPath;
    private ImageSelectConfig mImageSelectConfig;
    private AvatarOp mAvatarOp;

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (NetWorkUtils.isConnectedByState(UserHomepageActivity.this)) {
                queryUserInfo(true);
                svContent.setVisibility(View.GONE);
                mSwipeRefresh.setRefreshing(true);
            } else {
                mSwipeRefresh.setRefreshing(false);
                AppToast.showToast(R.string.network_unavailable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);
        ButterKnife.bind(this);
        // 隐藏用户认证标识
        mIvAvatarVip.setVisibility(View.GONE);
        mSwipeRefresh.setColorSchemeResources(BaseConfig.sSwipeRefreshColor);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mUsersAPI = new UsersAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        mCurUser = BaseConfig.sUser;
        // 一般情况下mCurUse不为空，做此判断仅为应用的健壮性
        if (mCurUser != null) {
            initUserInfo(false); // 初始化授权用户信息
        } else {
            queryUserInfo(true);
            svContent.setVisibility(View.GONE);
            mSwipeRefresh.setRefreshing(true);
        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, UserHomepageActivity.class);
        return intent;
    }

    /**
     * 初始化授权用户信息
     *
     * @param refreshAvatar 是否更新头像缓存
     */
    private void initUserInfo(boolean refreshAvatar) {
        svContent.setVisibility(View.VISIBLE);
        // 用户头像
        if (refreshAvatar) {
            // 更新头像缓存
            LoadAvatarTask task = new LoadAvatarTask(UserHomepageActivity.this, mCivHeadIcon);
            task.execute();
            registerAsyncTask(UserHomepageActivity.class, task);
        } else {
            String avatarFile = BaseConfig.sAccount.headCachePath;
            if (FileUtils.isFileExist(avatarFile)) {
                Glide.with(this).load(avatarFile).diskCacheStrategy(DiskCacheStrategy.NONE).into(mCivHeadIcon);
            } else {
                // 无用户头像的缓存，则从网络上加载，并缓存到本地
                LoadAvatarTask task = new LoadAvatarTask(this, mCivHeadIcon);
                task.execute();
                registerAsyncTask(UserHomepageActivity.class, task);
            }
        }
        // 用户昵称
        mTvScreenName.setText(mCurUser.screen_name);
        // 用户性别 m：男、f：女、n：未知
        if ("m".equals(mCurUser.gender)) {
            mIvGender.setImageResource(R.drawable.gender_boy_imv);
        } else if ("f".equals(mCurUser.gender)) {
            mIvGender.setImageResource(R.drawable.gender_girl_imv);
        } else {
            mIvGender.setVisibility(View.GONE);
        }
        // 认证信息
        UserVerify.verify(mCurUser, null, mTvDescription);
        // 地理位置
        mTvLocation.setText(mCurUser.location);
        // 粉丝数、关注数、微博数
        mTvFollowersCount.setText(NumberFormatter.formatWBCount(mCurUser.followers_count, 60000));
        mTvFriendsCount.setText(NumberFormatter.formatWBCount(mCurUser.friends_count, 60000));
        mTvStatusesCount.setText(NumberFormatter.formatWBCount(mCurUser.statuses_count, 100000));
    }

    /**
     * 查询用户信息
     *
     * @param refreshAvatar 是否更新头像缓存
     */
    private void queryUserInfo(final boolean refreshAvatar) {
        // 根据用户Id，获取用户信息
        mUsersAPI.show(Long.parseLong(BaseConfig.sUid), new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                if (!TextUtils.isEmpty(s)) {
//                    Log.d(TAG, s);
                    mCurUser = User.parse(s);
                    // 更新用户信息
                    BaseConfig.sUser = mCurUser;
                    initUserInfo(refreshAvatar);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取用户信息失败");
            }
        });
    }

    /** 查看头像原图 */
    @OnClick(R.id.civHeadIcon)
    void viewAvatarHD() {
        Intent intent = PhotoViewActivity.newIntent(this, mCurUser.avatar_hd);
        startActivity(intent);
    }

    /** 上传头像 */
    @OnClick(R.id.tvUploadPic)
    void uploadPic() {
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
                mAvatarPath = CommonUtils.capture(UserHomepageActivity.this, REQUEST_CODE_CAPTURE);
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 图片选择器配置
                if (mImageSelectConfig == null) {
                    mImageSelectConfig = new ImageSelectConfig.Builder(loader)
                            .multiSelect(false)      // 单选模式
                            .rememberSelected(false) // 不记住上次选中记录
                            .needCamera(false)      // 不显示拍照
                            .needCrop(true)
                            .build();
                }
                // 打开图片选择器
                ImageSelectActivity.startActivity(UserHomepageActivity.this, mImageSelectConfig, REQUEST_CODE_ALBUM);
            }
        });
    }

    /** 查看粉丝 */
    @OnClick(R.id.llFollowers)
    void viewFollowers() {
        Intent intent = WBFollowersActivity.newIntent(this, mCurUser.id, mCurUser.name);
        startActivity(intent);
    }

    /** 查看关注 */
    @OnClick(R.id.llFriends)
    void viewFriends() {
        Intent intent = WBFriendsActivity.newIntent(this, mCurUser.id, mCurUser.name);
        startActivity(intent);
    }

    /** 查看微博 */
    @OnClick(R.id.llStatuses)
    void viewStatuses() {
        Intent intent = WBStatusesActivity.newIntent(this, mCurUser.id, mCurUser.name);
        startActivity(intent);
    }

    /** 查看我的相册 */
    @OnClick(R.id.rlMyPhotos)
    void viewAlbum() {
        startActivity(WBAlbumActivity.newIntent(this, mCurUser.id, mCurUser.name));
    }

    /** 查看我的信息 */
    @OnClick(R.id.rlMyInfo)
    void viewMyInfo() {
        Intent intent = WBUserInfoActivity.newIntent(this, mCurUser);
        startActivity(intent);
    }

    /** 分组管理 */
    @OnClick(R.id.rlGroupMgr)
    void groupMgr() {
        startActivity(GroupManagerActivity.newIntent(this));
    }

    /** 查看周边动态 */
    @OnClick(R.id.rlNearby)
    void viewNearby() {
        startActivity(WBNearbyActivity.newIntent(this));
    }

    /** 返回 */
    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /** 切换账号 */
    @OnClick(R.id.rlSwitchAccount)
    void toSwitchAccount() {
        startActivity(SwitchAccountActivity.newIntent(this));
    }

    /** 退出当前帐号 */
    @OnClick(R.id.tvLogout)
    void toLogout() {
        final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
        View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_warning, null);
        ((TextView) rootView.findViewById(R.id.tvTitle)).setText("确认注销登录？");
        TextView tvConfirm = (TextView) rootView.findViewById(R.id.tvConfirm);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
        tvConfirm.setText("退出");
        dialog.show();
        DialogUtil.setBottom(dialog, rootView);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /** 注销当前帐号 */
    private void logout() {
        LogoutAPI logoutAPI = new LogoutAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        logoutAPI.logout(new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.isNull("error")) {
                            String value = obj.getString("result");

                            // 注销成功
                            if ("true".equalsIgnoreCase(value)) {
                                AccessTokenKeeper.delToken(BaseConfig.sUid);
                                // 需要finish之前所有的Activity并打开WBLoginActivity
                                App.getInstance().finishAllActivities();
                                startActivity(WBLoginActivity.newIntent(UserHomepageActivity.this));
//                                setText(R.string.com_sina_weibo_sdk_login_with_weibo_account);
                            }
                        } else {
                            String error_code = obj.getString("error_code");
                            if (error_code.equals("21317")) {
//                                sAccessToken = null;
//                                setText(R.string.com_sina_weibo_sdk_login_with_weibo_account);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                LogUtil.e("LOGOUT", "WeiboException： " + e.getMessage());
                // 注销失败
//                setText(R.string.com_sina_weibo_sdk_logout);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAvatarOp == null) {
            mAvatarOp = new AvatarOp(this);
            mAvatarOp.setOnAvatarUploadOpListener(new AvatarOp.OnAvatarUploadOpListener() {
                @Override
                public void onSuccess() {
                    AppToast.showToast(R.string.avatar_upload_success);
                    queryUserInfo(true);    // 重新获取用户信息
                }

                @Override
                public void onFailure(String msg) {
                    AppToast.showToast(msg);
                }
            });
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                if (mAvatarPath != null) {
                    CommonUtils.clipPhoto(this, Uri.fromFile(new File(mAvatarPath)), REQUEST_CODE_CLIP);
                } else {
                    AppToast.showToast("拍照出现异常");
                }
            } else if (requestCode == REQUEST_CODE_ALBUM) {
                if (data != null) {
                    List<String> pathList = data.getStringArrayListExtra(ImageSelectActivity.INTENT_RESULT);
                    if (pathList != null && !pathList.isEmpty()) {
                        AppToast.showToast(R.string.avatar_uploading);
                        mAvatarOp.upload(pathList.get(0));
                    } else {
                        AppToast.showToast("选择图片失败");
                    }
                }
            } else if (requestCode == REQUEST_CODE_CLIP) {
                if (data != null) {
                    AppToast.showToast(R.string.avatar_uploading);
                    Bitmap bitmap = data.getParcelableExtra("data");
                    if (bitmap != null) {
                        mAvatarOp.upload(bitmap);
                    } else {
                        AppToast.showToast("图片没有找到");
                    }
                } else {
                    AppToast.showToast("裁剪图片失败");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(UserHomepageActivity.class);
        super.onDestroy();
    }
}
