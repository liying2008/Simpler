package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
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
import cc.duduhuo.simpler.adapter.AtFriendsAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.net.HttpListener;
import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.util.UserUtil;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class AtFriendActivity extends BaseActivity {
    private static final String TAG = "AtFriendActivity";
    private static final String URL = "https://m.weibo.cn/attention/getAttentionList";
    /** Intent传值的键（AT的用户昵称） */
    public static final String INTENT_AT = "at";
    /** 微博API模式 */
    private static final int TYPE_ORI = 0x0000;
    /** 搜索模式 */
    private static final int TYPE_SEARCH = 0x0001;
    private int mType = TYPE_ORI;
    @BindView(R.id.etSearch)
    EditText mEtSearch;
    @BindView(R.id.tvClearText)
    TextView mTvClearText;
    @BindView(R.id.rvFriends)
    RecyclerView mRvFriends;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;

    private AtFriendsAdapter mAdapter;
    /** 用户Id */
    private long mUid;
    /** 当前查询结果游标 */
    private int mCursor = 0;
    /** 第一次获取结果的游标 */
    private int mFirstCursor = 0;
    /** 查询结果当前页码 */
    private int mCurPage = 1;
    /** 当前搜索关键词 */
    private String mKeyword;
    /** 微博API获取到的用户列表 */
    private List<User> mFriends = new ArrayList<>();

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // 不支持下拉刷新
            mSwipeRefresh.setRefreshing(false);
        }
    };
    private DSwipeRefresh.OnLoadingListener loadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mType == TYPE_ORI) {
                if (mCursor != 0) {
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    queryFriends(mUid, mCursor, false);
                }
            } else {
                mCurPage++;
                if (mCurPage <= mMaxPage) {
                    searchUser(mKeyword, mCurPage, false);
                }
            }
        }
    };
    private FriendshipsAPI mFAPI;
    /** 查询结果的最大页码 */
    private int mMaxPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_friend);
        ButterKnife.bind(this);

        mUid = Long.parseLong(BaseConfig.sUid);
        mAdapter = new AtFriendsAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvFriends, mAdapter);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mSwipeRefresh.setOnLoadingListener(loadingListener);
        queryFriends(mUid, 0, true);  // 查询粉丝信息
        mSwipeRefresh.setRefreshing(true);
        // 搜索框文本内容变化监听器
        mEtSearch.addTextChangedListener(new MyTextWatcher());
        mAdapter.setOnAtListener(new AtFriendsAdapter.OnAtListener() {
            @Override
            public void onAt(String screenName) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_AT, screenName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AtFriendActivity.class);
        return intent;
    }
    /**
     * 查询关注信息
     *
     * @param uid     用户id
     * @param cursor  游标
     * @param refresh 是否刷新列表
     */
    private void queryFriends(long uid, int cursor, final boolean refresh) {
        if (mFAPI == null) {
            mFAPI = new FriendshipsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mFAPI.friends(uid, 50, cursor, true, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                List<User> friends = new ArrayList<>(50);
                if (!TextUtils.isEmpty(s)) {
//                    Log.d(TAG, s);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(s);
                        JSONArray array = jsonObject.getJSONArray("users");
                        int length = array.length();
//                        Log.d(TAG, "数量：" + length);
                        if (length > 0) {
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                User user = User.parse(obj);
                                friends.add(user);
                            }
                            // 下一页的游标
                            mCursor = jsonObject.getInt("next_cursor");
//                            Log.e(TAG, mCursor + "");
                            if (mCursor == 0) {
                                // 表示没有更多数据了
                                mAdapter.setFooterInfo(getString(R.string.no_more_data));
                            } else {
                                mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                            }
                        }
                        if (refresh) {
                            mFriends.clear();
                            mFriends.addAll(friends);
                            mFirstCursor = mCursor;
                            mAdapter.setData(friends);
                        } else {
                            mAdapter.addData(friends);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppToast.showToast(R.string.resolve_result_failure);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取粉丝信息失败。");
            }
        });
    }

    /**
     * 搜索用户
     */
    @OnClick(R.id.tvSearch)
    void search() {
        String keyword = mEtSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            AppToast.showToast(R.string.keyword_is_empty);
            return;
        }
        if (TextUtils.isEmpty(BaseConfig.sAccount.cookie)) {
            // 获取Cookie
            startActivity(WebWBActivity.newIntent(this, true));
            AppToast.showToast(R.string.get_web_wb_cookie);
        } else {
            // 搜索用户
            mKeyword = keyword;
            mCurPage = 1;
            searchUser(keyword, 1, true);
        }
    }

    private void searchUser(String keyword, final int page, final boolean refresh) {
        String url = URL + "?keyword=" + keyword + "&format=cards&page=" + page;

        HttpGetTask httpTask = new HttpGetTask(true, new HttpListener() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int ok = jsonObject.getInt("ok");
                    if (ok == 1) {
                        // 有结果
                        mType = TYPE_SEARCH;
                        List<User> result;
                        // 返回结果的最大页数
                        mMaxPage = jsonObject.optInt("maxPage");
                        if (page == mMaxPage) {
                            // 表示没有更多数据了
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        }
                        result = UserUtil.parseH5User(jsonObject);
                        if (refresh) {
                            mAdapter.setData(result);
                        } else {
                            mAdapter.addData(result);
                        }
                    } else {
                        // 无结果
                        String userInfo = jsonObject.optString("userInfo");
                        if ("null".equals(userInfo)) {
                            // 有可能是Cookie过期了，更新Cookie
                            startActivity(WebWBActivity.newIntent(AtFriendActivity.this, true));
                            AppToast.showToast(R.string.update_web_wb_cookie);
                        } else {
                            AppToast.showToast(R.string.search_no_result);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppToast.showToast(R.string.resolve_result_failure);
                }

            }

            @Override
            public void onFailure() {
                AppToast.showToast(R.string.net_failure);
            }
        });
        httpTask.execute(url, BaseConfig.sAccount.cookie);
        registerAsyncTask(AtFriendActivity.class, httpTask);
    }

    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if ("".equals(s.toString())) {
                if (mType == TYPE_SEARCH) {
                    // 加载微博API获取的用户
                    mAdapter.setData(mFriends);
                    mType = TYPE_ORI;
                    mCursor = mFirstCursor;
                    if (mCursor == 0) {
                        mAdapter.setFooterInfo(getString(R.string.no_more_data));
                    } else {
                        mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                    }
                }
                // 隐藏搜索框中的清除按钮
                mTvClearText.setVisibility(View.GONE);
            } else {
                // 显示搜索框中的清除按钮
                mTvClearText.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 清除搜索框中的文本
     */
    @OnClick(R.id.tvClearText)
    void clearText() {
        mEtSearch.setText("");
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(AtFriendActivity.class);
        super.onDestroy();
    }
}
