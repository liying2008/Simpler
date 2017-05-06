package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.fragment.GroupChangeFragment;
import cc.duduhuo.simpler.listener.impl.FriendshipOp;
import cc.duduhuo.simpler.util.NetWorkUtils;
import cc.duduhuo.simpler.util.NumberFormatter;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.util.UserVerify;

public class WBUserHomeActivity extends BaseActivity {
    private static final String TAG = "User";
    /** Intent传值的键（用户昵称） */
    private static final String INTENT_SCREEN_NAME = "screen_name";
    /** 请求码：修改备注 */
    private static final int REQUEST_REMARK = 0;

    /** 下拉刷新布局 */
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    /** 主内容区域 */
    @BindView(R.id.llContent)
    LinearLayout llContent;
    /** 根据用户的性别更改文本“他或她” */
    @BindView(R.id.tvFriendshipCreate)
    TextView mTvFriendshipCreate;
    /** Titlebar Title */
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
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
    /** 更改备注文本 */
    @BindView(R.id.tvUpdateRemark)
    TextView mTvUpdateRemark;
    /** 更改备注ITEM */
    @BindView(R.id.rlUpdateRemark)
    RelativeLayout mRlUpdateRemark;
    /** 更改分组文本 */
    @BindView(R.id.tvUpdateGroup)
    TextView mTvUpdateGroup;
    /** 更改分组ITEM */
    @BindView(R.id.rlUpdateGroup)
    RelativeLayout mRlUpdateGroup;

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (NetWorkUtils.isConnectedByState(WBUserHomeActivity.this)) {
                queryUserInfo();
                llContent.setVisibility(View.GONE);
                mSwipeRefresh.setRefreshing(true);
            } else {
                mSwipeRefresh.setRefreshing(false);
                AppToast.showToast(R.string.network_unavailable);
            }
        }
    };
    private User mUser;
    private UsersAPI mUsersAPI;
    private String mScreenName;
    private FriendshipsAPI mFApi;
    private FriendshipOp mFriendshipOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_user_home);
        ButterKnife.bind(this);

        // 隐藏上传头像按钮
        mTvUploadPic.setVisibility(View.GONE);
        // 得到传入的用户id
        Intent intent = getIntent();
        mScreenName = intent.getStringExtra(INTENT_SCREEN_NAME);
        mUsersAPI = new UsersAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);

        mSwipeRefresh.setColorSchemeResources(BaseConfig.sSwipeRefreshColor);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        disableUpdateRemarkAndGroup();
        if (NetWorkUtils.isConnectedByState(this)) {
            queryUserInfo();    // 查询用户信息
            mSwipeRefresh.setRefreshing(true);
        } else {
            AppToast.showToast(R.string.network_unavailable);
        }
        llContent.setVisibility(View.GONE);
    }

    public static Intent newIntent(Context context, String screenName) {
        Intent intent = new Intent(context, WBUserHomeActivity.class);
        intent.putExtra(INTENT_SCREEN_NAME, screenName);
        return intent;
    }

    /**
     * 查询用户信息
     */
    private void queryUserInfo() {
        // 根据用户昵称，获取用户信息
        mUsersAPI.show(mScreenName, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                if (!TextUtils.isEmpty(s)) {
//                    Log.d(TAG, s);
                    mUser = User.parse(s);
                    initUserInfo();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    if (obj.optInt("error_code") == 20003) {
                        AppToast.showToast(R.string.user_does_not_exists);
                        finish();
                        return;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                AppToast.showToast(R.string.failed_to_get_user_info);
            }
        });
    }

    /**
     * 加载用户信息
     */
    private void initUserInfo() {
        llContent.setVisibility(View.VISIBLE);
        // 加载用户头像
        Glide.with(this).load(mUser.avatar_large).into(mCivHeadIcon);
        // 用户昵称
        String name = mUser.screen_name;
        if (!TextUtils.isEmpty(mUser.remark)) {
            name += "(" + mUser.remark + ")";
        }
        mTvScreenName.setText(name);
        mTvTitle.setText(mUser.screen_name);
        // 用户性别 m：男、f：女、n：未知
        if ("m".equals(mUser.gender)) {
            mIvGender.setImageResource(R.drawable.gender_boy_imv);
            mTvFriendshipCreate.setText(R.string.pay_attention_to_him);
        } else if ("f".equals(mUser.gender)) {
            mIvGender.setImageResource(R.drawable.gender_girl_imv);
            mTvFriendshipCreate.setText(R.string.pay_attention_to_her);
        } else {
            mIvGender.setVisibility(View.GONE);
            mTvFriendshipCreate.setText(R.string.pay_attention_to_him_or_her);
        }

        // 授权用户是否关注了该用户
        if (mUser.following) {
            mTvFriendshipCreate.setText(R.string.not_pay_attention);
            enableUpdateRemarkAndGroup();
        }

        // 认证信息
        UserVerify.verify(mUser, mIvAvatarVip, mTvDescription);
        // 地理位置
        mTvLocation.setText(mUser.location);
        // 粉丝数、关注数、微博数
        mTvFollowersCount.setText(NumberFormatter.formatWBCount(mUser.followers_count, 60000));
        mTvFriendsCount.setText(NumberFormatter.formatWBCount(mUser.friends_count, 60000));
        mTvStatusesCount.setText(NumberFormatter.formatWBCount(mUser.statuses_count, 100000));
    }

    /**
     * 查看头像原图
     */
    @OnClick(R.id.civHeadIcon)
    void viewAvatarHD() {
        Intent intent = PhotoViewActivity.newIntent(this, mUser.avatar_hd);
        startActivity(intent);
    }

    /**
     * 查看粉丝
     */
    @OnClick(R.id.llFollowers)
    void viewFollowers() {
        Intent intent = WBFollowersActivity.newIntent(this, mUser.id, mUser.name);
        startActivity(intent);
    }

    /**
     * 查看关注
     */
    @OnClick(R.id.llFriends)
    void viewFriends() {
        Intent intent = WBFriendsActivity.newIntent(this, mUser.id, mUser.name);
        startActivity(intent);
    }

    /**
     * 查看微博
     */
    @OnClick(R.id.llStatuses)
    void viewStatuses() {
        Intent intent = WBStatusesActivity.newIntent(this, mUser.id, mUser.name);
        startActivity(intent);
    }

    /**
     * 关注或取消关注该用户
     */
    @OnClick(R.id.rlFriendshipCreate)
    void createFriendship() {
        if (mFApi == null) {
            mFApi = new FriendshipsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        if (mFriendshipOp == null) {
            mFriendshipOp = new FriendshipOp(this, mFApi);
            mFriendshipOp.setOnFriendshipOpResultListener(new MyFriendshipOpResultListener());
        }
        if (mUser.following) {
            // 取消关注
            mFriendshipOp.onDestroy(0, mUser.id, mUser.screen_name);
        } else {
            // 关注他/她
            mFriendshipOp.onCreate(0, mUser.id, mUser.screen_name);
        }
    }

    /**
     * 查看相册
     */
    @OnClick(R.id.rlPhotos)
    void viewAlbum() {
        startActivity(WBAlbumActivity.newIntent(this, mUser.id, mUser.name));
    }

    /**
     * 查看信息
     */
    @OnClick(R.id.rlInfo)
    void viewInfo() {
        Intent intent = WBUserInfoActivity.newIntent(this, mUser);
        startActivity(intent);
    }

    /**
     * 更新备注
     */
    @OnClick(R.id.rlUpdateRemark)
    void updateRemark() {
        Intent intent = UpdateRemarkActivity.newIntent(this, mUser.id, mUser.remark, mUser.name);
        startActivityForResult(intent, REQUEST_REMARK);
    }

    /**
     * 更新分组
     */
    @OnClick(R.id.rlUpdateGroup)
    void updateGroup() {
        GroupChangeFragment fragment = GroupChangeFragment.newInstance(mUser.id);
        fragment.show(getSupportFragmentManager(), "group_change_fragment");
    }

    /** 允许修改备注信息和分组 */
    private void enableUpdateRemarkAndGroup() {
        mRlUpdateRemark.setClickable(true);
        mTvUpdateRemark.setTextColor(0xff333333);
        mRlUpdateGroup.setClickable(true);
        mTvUpdateGroup.setTextColor(0xff333333);
    }

    /** 不允许修改备注信息和分组 */
    private void disableUpdateRemarkAndGroup() {
        mRlUpdateRemark.setClickable(false);
        mTvUpdateRemark.setTextColor(0xffAAAAAA);
        mRlUpdateGroup.setClickable(false);
        mTvUpdateGroup.setTextColor(0xffAAAAAA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_REMARK) {
                String remark = data.getStringExtra(UpdateRemarkActivity.INTENT_NEW_REMARK);
                mTvScreenName.setText(mUser.screen_name + "(" + remark + ")");
            }
        }
    }

    @OnClick(R.id.tvBack)
    void back() {
        finish();
    }

    private class MyFriendshipOpResultListener implements FriendshipOp.OnFriendshipOpResultListener {
        @Override
        public void onCreateSuccess(int position, String screenName) {
            AppToast.showToast(getString(R.string.pay_attention_prefix, screenName));
            mTvFriendshipCreate.setText(getString(R.string.not_pay_attention));
            mUser.following = true;
            enableUpdateRemarkAndGroup();
        }

        @Override
        public void onCreateFailure(int position, String screenName, String msg) {
            AppToast.showToast(getString(R.string.pay_attention_failure_prefix, msg));
        }

        @Override
        public void onDestroySuccess(int position, String screenName) {
            AppToast.showToast(getString(R.string.not_pay_attention_prefix, screenName));
            if ("m".equals(mUser.gender)) {
                mTvFriendshipCreate.setText(getString(R.string.pay_attention_to_him));
            } else if ("f".equals(mUser.gender)) {
                mTvFriendshipCreate.setText(getString(R.string.pay_attention_to_her));
            } else {
                mTvFriendshipCreate.setText(getString(R.string.pay_attention_to_him_or_her));
            }
            mUser.following = false;
            disableUpdateRemarkAndGroup();
        }

        @Override
        public void onDestroyFailure(int position, String screenName, String msg) {
            AppToast.showToast(getString(R.string.not_pay_attention_failure_prefix, msg));
        }

        @Override
        public void onAddToGroupFailure(String msg) {
            AppToast.showToast(getString(R.string.add_to_group_failure_prefix, msg));
        }
    }
}
