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
 * 日期：2017/4/20 21:38
 * 版本：1.0
 * 描述：热门微博
 * 备注：
 * =======================================================
 */
public class HotStatusesActivity extends BaseActivity {
    private static final String TAG = "HotStatusesActivity";
    private static final String BASE_URL = "http://m.weibo.cn/api/container/getIndex?containerid=102803";
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private MBlogListAdapter mAdapter;
    private int mSinceId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_statuses);
        ButterKnife.bind(this);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新微博，加载最新微博
                loadStatuses(0, true);
                mSwipeRefresh.setRefreshing(true);
            }
        });
        // 创建适配器
        mAdapter = new MBlogListAdapter(this, true);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                if (mSinceId > 0) {
                    // 加载更早微博
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    loadStatuses(mSinceId, false);
                }
            }
        });
        loadStatuses(0, true);  // 加载最新微博
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HotStatusesActivity.class);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 加载用户微博
     */
    private void loadStatuses(int sinceId, final boolean refresh) {
        String url = BASE_URL + "&since_id=" + sinceId;
        HttpGetTask task = new HttpGetTask(true, new HttpListener() {
            @Override
            public void onResponse(String response) {
                mSwipeRefresh.setRefreshing(false);
                Log.d(TAG, response);
                CardList cardList = CardList.parse(response);
                if (cardList != null && cardList.cards != null) {
                    if (refresh) {
                        mAdapter.setMBlogs(cardList.cards, true);
                    } else {
                        mAdapter.addMBlogs(cardList.cards, true);
                    }
                    if (cardList.cardlistInfo != null) {
                        mSinceId = cardList.cardlistInfo.since_id;
                        if (mSinceId > 0) {
                            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                        } else {
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        }
                    } else {
                        mSinceId = 0;
                        mAdapter.setFooterInfo(getString(R.string.no_more_data));
                    }
                } else {
                    mSinceId = 0;
                    mAdapter.setFooterInfo(getString(R.string.no_more_data));
                }
            }

            @Override
            public void onFailure() {
                mSwipeRefresh.setRefreshing(false);
                AppToast.showToast("获取热门微博失败");
            }
        });
        task.execute(url, BaseConfig.sAccount.cookie);
    }
}
