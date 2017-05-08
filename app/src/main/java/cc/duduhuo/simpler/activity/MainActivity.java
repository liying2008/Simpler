package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.GroupList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.StatusListAdapter;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.MenuItem;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.fragment.MainMenuFragment;
import cc.duduhuo.simpler.listener.GroupChangeListener;
import cc.duduhuo.simpler.listener.LoadStatusCacheListener;
import cc.duduhuo.simpler.listener.LoadUserCacheListener;
import cc.duduhuo.simpler.service.UnreadService;
import cc.duduhuo.simpler.task.LoadAvatarTask;
import cc.duduhuo.simpler.task.LoadStatusCacheTask;
import cc.duduhuo.simpler.task.LoadUserCacheTask;
import cc.duduhuo.simpler.task.SaveStatusCacheTask;
import cc.duduhuo.simpler.task.SaveUserCacheTask;
import cc.duduhuo.simpler.util.AccountUtil;
import cc.duduhuo.simpler.util.NetWorkUtils;
import cc.duduhuo.simpler.util.PrefsUtils;
import cc.duduhuo.simpler.util.SettingsUtil;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MainActivity";
    /** 授权用户头像 */
    @BindView(R.id.civHeadIcon)
    ImageView mCivHeadIcon;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;

    /** 微博列表 */
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    /** 当前用户是否已验证 */
    private boolean mOK = false;
    private StatusListAdapter mAdapter;
    private static long mMaxId = 0;

    private StatusesAPI mStatusesAPI;
    /** 加载微博类型 */
    private static int sType = MenuItem.MENU_ID_ALL_STATUS;
    private static long sGroupId;
    private GroupAPI mGroupAPI;
    private int mRefreshCount;
    /** 上次点击Back键的时间 */
    private long mLastBackKeyTime;
    private UsersAPI mUsersAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mUsersAPI = new UsersAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);

        // 每次刷新微博数
        if (BaseSettings.sSettings == null) {
            BaseSettings.sSettings = SettingsUtil.readSettings(BaseConfig.sUid, false);
        }
        mRefreshCount = BaseSettings.sSettings.refreshCount;

        if (NetWorkUtils.isConnectedByState(this)) {
            // 有网络，从网络获取用户信息
            checkUserId(mUsersAPI); // 检查并加载授权用户信息
        } else {
            // 无网络，从缓存获取用户信息
            loadUserCache();
        }

        mSwipeRefresh.setOnRefreshListener(this);

        // 创建适配器
        mAdapter = new StatusListAdapter(MainActivity.this, null);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                // 加载更早微博
                if (mMaxId != 0) {
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    if (sType == MenuItem.MENU_ID_ALL_STATUS) {
                        // 所有微博
                        loadStatuses(mMaxId, false);
                    } else if (sType == MenuItem.MENU_ID_GROUP) {
                        // 分组微博
                        loadGroupStatuses(sGroupId, mMaxId, false);
                    } else if (sType == MenuItem.MENU_ID_FRIEND_STATUS) {
                        // 朋友圈微博
                        loadBilateralStatuses(mMaxId, false);
                    }
                }
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    /**
     * 加载用户信息缓存
     */
    private void loadUserCache() {
        mSwipeRefresh.setRefreshing(true);
        LoadUserCacheTask task = new LoadUserCacheTask(new LoadUserCacheListener() {
            @Override
            public void userCacheLoaded(String cacheStr) {
                formatUserStr(cacheStr);
            }

            @Override
            public void noUserCache() {
                // 无缓存
                AppToast.showToast(R.string.unable_to_load_user_info);
            }
        });
        task.execute();
        registerAsyncTask(MainActivity.class, task);
    }

    /**
     * 加载授权用户信息
     *
     * @param usersAPI
     */
    private void checkUserId(UsersAPI usersAPI) {
        if ("".equals(BaseConfig.sUid)) {
            BaseConfig.sUid = PrefsUtils.getString(Constants.PREFS_CUR_UID, "");
            if ("".equals(BaseConfig.sUid)) {
                App.getInstance().finishAllActivities();
                startActivity(WBLoginActivity.newIntent(this));
            } else {
                loadUserInfo(usersAPI);
            }
        } else {
            loadUserInfo(usersAPI);
        }
    }

    private void loadUserInfo(UsersAPI usersAPI) {
        usersAPI.show(Long.parseLong(BaseConfig.sUid), new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    formatUserStr(s);      // 格式化用户信息字符串
                    saveUserCache(s);    // 缓存当前用户信息
                    UnreadService.startService();   // 开启未读服务
                } else {
                    // 获取用户信息失败
                    mOK = false;
                    AppToast.showToast(R.string.failed_to_get_user_info);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    int errorCode = obj.optInt("error_code");
                    if (errorCode == 10006 || errorCode == 21332) {
                        // 授权过期
                        AppToast.showToast("应用授权过期，请重新授权");
                        if (!BaseConfig.sTokenExpired) {
                            BaseConfig.sTokenExpired = true;
                            App.getInstance().finishAllActivities();
                            startActivity(WBLoginActivity.newIntent(MainActivity.this));
                            return;
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                mOK = false;
                AppToast.showToast(e.getMessage());
            }
        });
    }

    private void formatUserStr(String s) {
        mOK = true;
        // 设置当前用户
        BaseConfig.sUser = User.parse(s);
        // 更新帐户信息
        AccountUtil.updateScreenName(BaseConfig.sUser.screen_name);
        AccountUtil.updateName(BaseConfig.sUser.name);
        loadAvatar();   // 加载头像
        loadGroups();   // 加载分组信息
        if (BaseSettings.sSettings.autoRefresh) {
            // 刷新微博
            loadStatuses(0, true);
            mSwipeRefresh.setRefreshing(true);
        } else {
            loadStatusesCache();    // 加载微博缓存
        }
    }

    /**
     * 缓存当前用户信息
     *
     * @param s
     */
    private void saveUserCache(String s) {
        SaveUserCacheTask task = new SaveUserCacheTask();
        task.execute(s);
        registerAsyncTask(MainActivity.class, task);
    }

    /**
     * 加载微博分组
     */
    private void loadGroups() {
        // 获取用户信息接口
        if (mGroupAPI == null) {
            mGroupAPI = new GroupAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        RequestListener listener = new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    GroupList groupList = GroupList.parse(response);
                    if (groupList != null && groupList.groupList != null) {
                        BaseConfig.sGroups = groupList.groupList;
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                AppToast.showToast(R.string.loading_groups_failed);
            }
        };
        mGroupAPI.groups(listener);
    }

    /**
     * 加载微博缓存
     */
    private void loadStatusesCache() {
        mSwipeRefresh.setRefreshing(true);
        LoadStatusCacheTask task = new LoadStatusCacheTask(new LoadStatusCacheListener() {
            @Override
            public void cacheLoaded(String cacheStr) {
                formatStatusStr(cacheStr, true);
            }

            @Override
            public void noCache() {
                loadStatuses(0, true); // 加载微博
            }
        });
        task.execute();
        registerAsyncTask(MainActivity.class, task);
    }

    /**
     * 加载头像
     */
    private void loadAvatar() {
        LoadAvatarTask task = new LoadAvatarTask(this, mCivHeadIcon);
        task.execute();
        registerAsyncTask(MainActivity.class, task);
    }

    /**
     * 加载微博
     */
    private void loadStatuses(long maxId, final boolean refresh) {
        if (mStatusesAPI == null) {
            mStatusesAPI = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mStatusesAPI.friendsTimeline(0, maxId, mRefreshCount, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    if (refresh) {
                        // 缓存最新微博
                        SaveStatusCacheTask task = new SaveStatusCacheTask();
                        task.execute(s);
                        registerAsyncTask(MainActivity.class, task);
                    }
                    formatStatusStr(s, refresh);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    int errorCode = obj.optInt("error_code");
                    if (errorCode == 10006 || errorCode == 21332) {
                        // 授权过期
                        AppToast.showToast("应用授权过期，请重新授权");
                        if (!BaseConfig.sTokenExpired) {
                            BaseConfig.sTokenExpired = true;
                            App.getInstance().finishAllActivities();
                            startActivity(WBLoginActivity.newIntent(MainActivity.this));
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * 跳转到授权用户主页
     */
    @OnClick(R.id.civHeadIcon)
    void viewUserHomepage() {
        startActivity(UserHomepageActivity.newIntent(this));
    }

    /**
     * 跳转到设置界面
     */
    @OnClick(R.id.tvSettings)
    void viewSettings() {
        if (mOK) {
            startActivity(SettingsActivity.newIntent(this));
        } else {
            AppToast.showToast(R.string.wb_loading_info);
        }
    }

    /**
     * 打开菜单
     */
    @OnClick(R.id.toolbar_menu_ib)
    void openMenu() {
        if (mOK) {
            MainMenuFragment menuFragment = new MainMenuFragment();
            menuFragment.setGroupChangeListener(new GroupChangeListener() {
                @Override
                public void onGroupChange(int menuId, String groupId, String groupName) {
                    if (menuId == MenuItem.MENU_ID_ALL_STATUS) {
                        mSwipeRefresh.setRefreshing(true);
                        mTvTitle.setText(groupName);
                        loadStatuses(0, true);
                    } else if (menuId == MenuItem.MENU_ID_GROUP) {
                        mSwipeRefresh.setRefreshing(true);
                        mTvTitle.setText(groupName);
                        long groupIdL = Long.parseLong(groupId);
                        loadGroupStatuses(groupIdL, 0, true);
                        sGroupId = groupIdL;
                    } else if (menuId == MenuItem.MENU_ID_FRIEND_STATUS) {
                        mSwipeRefresh.setRefreshing(true);
                        mTvTitle.setText(groupName);
                        loadBilateralStatuses(0, true);
                    } else if (menuId == MenuItem.MENU_ID_FAVORITE) {
                        // 我的收藏
                        Intent intent = WBFavoritesActivity.newIntent(MainActivity.this,
                                Long.parseLong(BaseConfig.sUid), BaseConfig.sUser.name);
                        startActivity(intent);
                    } else if (menuId == MenuItem.MENU_ID_SEARCH) {
                        startActivity(SearchActivity.newIntent(MainActivity.this));
                    } else if (menuId == MenuItem.MENU_ID_HOT) {
                        startActivity(HotStatusesActivity.newIntent(MainActivity.this));
                    } else if (menuId == MenuItem.MENU_ID_DRAFT) {
                        startActivity(DraftsActivity.newIntent(MainActivity.this));
                    } else if (menuId == MenuItem.MENU_ID_MESSAGE) {
                        startActivity(WBMessageActivity.newIntent(MainActivity.this));
                    }
                    sType = menuId;
                }
            });
            menuFragment.show(getSupportFragmentManager(), "main_menu_fragment");
        } else {
            AppToast.showToast(R.string.wb_loading_info);
        }
    }

    /**
     * 加载分组微博
     *
     * @param groupId
     * @param maxId
     * @param refresh
     */
    public void loadGroupStatuses(long groupId, long maxId, final boolean refresh) {
        if (mGroupAPI == null) {
            mGroupAPI = new GroupAPI(MainActivity.this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGroupAPI.timeline(groupId, 0, maxId, mRefreshCount, 1, false, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    formatStatusStr(s, refresh);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                AppToast.showToast(R.string.loading_statuses_failed);
                e.printStackTrace();
            }
        });
    }

    /**
     * 加载朋友圈（双向关注的用户）微博
     */
    public void loadBilateralStatuses(long maxId, final boolean refresh) {
        if (mStatusesAPI == null) {
            mStatusesAPI = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mStatusesAPI.bilateralTimeline(0, maxId, mRefreshCount, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    formatStatusStr(s, refresh);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                AppToast.showToast(R.string.loading_statuses_failed);
                e.printStackTrace();
            }
        });
    }

    /**
     * 跳转到发布微博界面
     */
    @OnClick(R.id.toolbar_post_ib)
    void postStatus() {
        if (mOK) {
            startActivity(WBPostActivity.newIntent(this));
            overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_top_out);
        } else {
            AppToast.showToast(R.string.wb_loading_info);
        }
    }

    /**
     * 刷新微博
     */
    @OnClick(R.id.toolbar_refresh_ib)
    void refreshStatus() {
        if (NetWorkUtils.isConnectedByState(this)) {
            if (mOK) {
                loadStatuses(0, true);
                mSwipeRefresh.setRefreshing(true);
            } else {
                AppToast.showToast(R.string.wb_loading_info);
            }
        } else {
            AppToast.showToast(R.string.network_unavailable);
        }
    }

    @Override
    public void onRefresh() {
        // 刷新微博，加载最新微博
        if (mOK) {
            if (NetWorkUtils.isConnectedByState(this)) {
                loadStatuses(0, true);
            } else {
                mSwipeRefresh.setRefreshing(false);
                AppToast.showToast(R.string.network_unavailable);
            }
        } else {
            checkUserId(mUsersAPI);
        }
    }

    /**
     * 格式化微博JSON字符串
     *
     * @param s       json字符串
     * @param refresh 是否刷新微博
     */
    private void formatStatusStr(String s, boolean refresh) {
        mSwipeRefresh.setRefreshing(false);
        JSONObject statuses = null;
        List<Status> statusList = new ArrayList<>(mRefreshCount);
        try {
            statuses = new JSONObject(s);
            JSONArray jsonArray = statuses.getJSONArray("statuses");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject o = (JSONObject) jsonArray.get(i);
                Status status = Status.parse(o);
                statusList.add(status);
            }
            // 更新mMaxId
            mMaxId = statuses.getLong("next_cursor");
            if (mMaxId == 0) {
                // 表示没有更多数据了
                mAdapter.setFooterInfo(getString(R.string.no_more_data));
            } else {
                mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
            }
            if (refresh) {
                mAdapter.setStatuses(statusList);
                mSwipeRefresh.getLayoutManager().scrollToPosition(0);
            } else {
                mAdapter.addStatuses(statusList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // 重新加载微博
            loadStatuses(mMaxId, true);
        }
    }

    @Override
    public void onBackPressed() {
        // 按下返回键
        long delay = Math.abs(System.currentTimeMillis() - mLastBackKeyTime);
        if (delay > 2000) {
            AppToast.showToast("再按一次，退出应用");
            mLastBackKeyTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(MainActivity.class);
        super.onDestroy();
        UnreadService.stopService();   // 关闭未读服务
    }

}
