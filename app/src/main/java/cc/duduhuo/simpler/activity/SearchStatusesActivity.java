package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.MBlogListAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.weibo.Card;
import cc.duduhuo.simpler.bean.weibo.CardList;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.net.HttpListener;
import cc.duduhuo.simpler.view.DSwipeRefresh;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 11:00
 * 版本：1.0
 * 描述：搜索微博
 * 备注：
 * =======================================================
 */
public class SearchStatusesActivity extends BaseActivity {
    private static final String TAG = "SearchStatusesActivity";
    private static final String BASE_URL = "http://m.weibo.cn/api/container/getIndex";
    /** Intent传值的键（搜索关键字） */
    private static final String INTENT_KEYWORD = "keyword";
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private MBlogListAdapter mAdapter;
    private int mPage = 1;
    private String mKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_statuses);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mKeyword = intent.getStringExtra(INTENT_KEYWORD);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新微博，加载最新微博
                loadStatuses(1, true);
                mSwipeRefresh.setRefreshing(true);
            }
        });
        // 创建适配器
        mAdapter = new MBlogListAdapter(this, false);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                if (mPage > 0) {
                    // 加载更早微博
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    loadStatuses(mPage, false);
                }
            }
        });
        loadStatuses(1, true);  // 加载最新微博
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, String keyword) {
        Intent intent = new Intent(context, SearchStatusesActivity.class);
        intent.putExtra(INTENT_KEYWORD, keyword);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 加载用户微博
     */
    private void loadStatuses(int page, final boolean refresh) {
        String url = BASE_URL + "?type=wb&queryVal=" + mKeyword + "&containerid=100103type%3D2%26q%3D"
                + mKeyword + "&page=" + page;
        HttpGetTask task = new HttpGetTask(true, new HttpListener() {
            @Override
            public void onResponse(String response) {
                mSwipeRefresh.setRefreshing(false);
                CardList cardList = CardList.parse(response);
                if (cardList != null && cardList.cards != null) {
                    Card card = cardList.cards.get(0);
                    if (card.card_group != null) {
                        if (refresh) {
                            mAdapter.setMBlogs(card.card_group);
                        } else {
                            mAdapter.addMBlogs(card.card_group);
                        }
                    }
                    if (cardList.cardlistInfo != null) {
                        mPage = cardList.cardlistInfo.page;
                        if (mPage > 0) {
                            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                        } else {
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        }
                    } else {
                        mPage = 0;
                        mAdapter.setFooterInfo(getString(R.string.no_more_data));
                    }
                } else {
                    mPage = 0;
                    mAdapter.setFooterInfo(getString(R.string.no_more_data));
                }
            }

            @Override
            public void onFailure() {
                mSwipeRefresh.setRefreshing(false);
                AppToast.showToast("没有搜索结果");
            }
        });
        task.execute(url, BaseConfig.sAccount.cookie);
        registerAsyncTask(SearchStatusesActivity.class, task);
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(SearchStatusesActivity.class);
        super.onDestroy();
    }
}
