package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import cc.duduhuo.simpler.adapter.TopicsAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.Topic;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.net.HttpListener;
import cc.duduhuo.simpler.view.DSwipeRefresh;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/18 19:27
 * 版本：1.0
 * 描述：搜索界面
 * 备注：
 * =======================================================
 */
public class SearchActivity extends BaseActivity {
    private static final String TOPIC_URL = "http://m.weibo.cn/api/container/getIndex?containerid=100803";
    private static final int REQUEST_CODE_COOKIE = 1;
    @BindView(R.id.etSearch)
    EditText mEtSearch;
    @BindView(R.id.tvClearText)
    TextView mTvClearText;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvHotTopics)
    RecyclerView mRvHotTopics;
    private TopicsAdapter mAdapter;
    private String mNextPage;
    @BindView(R.id.rlNoCookie)
    RelativeLayout mRlNoCookie;
    @BindView(R.id.tvLogin)
    TextView mTvLogin;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            queryDailyHotTopic(null, true);
            mSwipeRefresh.setRefreshing(true);
        }
    };

    private DSwipeRefresh.OnLoadingListener mLoadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (!TextUtils.isEmpty(mNextPage)) {
                mAdapter.setFooterInfo(getString(R.string.data_loading));
                queryDailyHotTopic(mNextPage, false);
            }
        }
    };
    private String mCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mEtSearch.addTextChangedListener(new MyTextWatcher());
        mAdapter = new TopicsAdapter(SearchActivity.this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvHotTopics, mAdapter);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);
        mSwipeRefresh.setOnLoadingListener(mLoadingListener);
        mTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(WebWBActivity.newIntent(SearchActivity.this, true), REQUEST_CODE_COOKIE);
            }
        });

        mCookie = BaseConfig.sAccount.cookie;
        if (TextUtils.isEmpty(mCookie)) {
            mRlNoCookie.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.GONE);
        } else {
            mRlNoCookie.setVisibility(View.GONE);
            mSwipeRefresh.setVisibility(View.VISIBLE);
        }
        // 获取日热门话题
        queryDailyHotTopic(null, true);
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        return intent;
    }

    /**
     * 搜索某一话题关键字下的微博
     */
    @OnClick(R.id.tvSearchTopics)
    void searchTopics() {
        String keyword = mEtSearch.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            startActivity(SearchTopicsActivity.newIntent(this, keyword));
        } else {
            AppToast.showToast(R.string.keyword_is_empty);
        }
    }

    /**
     * 搜索相关用户
     */
    @OnClick(R.id.tvSearchUsers)
    void searchUsers() {
        String keyword = mEtSearch.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            startActivity(SearchUsersActivity.newIntent(this, keyword));
        } else {
            AppToast.showToast(R.string.keyword_is_empty);
        }
    }

    /**
     * 搜索相关微博
     */
    @OnClick(R.id.tvSearchStatuses)
    void searchStatuses() {
        String keyword = mEtSearch.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            startActivity(SearchStatusesActivity.newIntent(this, keyword));
        } else {
            AppToast.showToast(R.string.keyword_is_empty);
        }
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    public void queryDailyHotTopic(String nextPage, final boolean refresh) {
        String url;
        if (nextPage == null) {
            url = TOPIC_URL;
        } else {
            url = TOPIC_URL + "&since_id=" + nextPage;
        }

        if (!TextUtils.isEmpty(mCookie)) {
            HttpGetTask task = new HttpGetTask(true, new HttpListener() {
                @Override
                public void onResponse(String response) {
                    mSwipeRefresh.setRefreshing(false);
//                    Log.d("Topic", response);
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            List<Topic> topicList = new ArrayList<>();
                            JSONObject obj = new JSONObject(response);
                            JSONArray cards = obj.optJSONArray("cards");
                            JSONObject cardlistInfo = obj.optJSONObject("cardlistInfo");
                            if (cardlistInfo != null) {
                                mNextPage = cardlistInfo.optString("since_id", null);
                            } else {
                                mNextPage = null;
                            }
                            if (cards != null && cards.length() > 0) {
                                JSONArray array = cards.optJSONObject(0).optJSONArray("card_group");
                                if (array != null && array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject topic = array.optJSONObject(i);
                                        topicList.add(Topic.parse(topic));
                                    }
                                }
                            }
                            if (refresh) {
                                mAdapter.setTopics(topicList);
                            } else {
                                mAdapter.addTopics(topicList);
                            }
                            if (mNextPage != null) {
                                mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                            } else {
                                mAdapter.setFooterInfo(getString(R.string.no_more_data));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppToast.showToast(R.string.update_web_wb_cookie);
                            startActivity(WebWBActivity.newIntent(SearchActivity.this, true));
                        }
                    }
                }

                @Override
                public void onFailure() {
                    mSwipeRefresh.setRefreshing(false);
                    AppToast.showToast("获取热门话题失败");
                }
            });
            task.execute(url, mCookie);
            registerAsyncTask(SearchActivity.class, task);
        } else {
            // Cookie为空
            mSwipeRefresh.setRefreshing(false);
        }
    }

    @OnClick(R.id.tvClearText)
    void clearText() {
        mEtSearch.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COOKIE && resultCode == RESULT_OK) {
            // 得到了Cookie
            mCookie = BaseConfig.sAccount.cookie;
            mRlNoCookie.setVisibility(View.GONE);
            mSwipeRefresh.setVisibility(View.VISIBLE);
            queryDailyHotTopic(null, true);
        }
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
            if (TextUtils.isEmpty(s.toString())) {
                mTvClearText.setVisibility(View.GONE);
            } else {
                mTvClearText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(SearchActivity.class);
        super.onDestroy();
    }
}
