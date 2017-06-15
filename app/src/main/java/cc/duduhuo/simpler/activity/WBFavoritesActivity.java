package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.models.Favorite;

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
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class WBFavoritesActivity extends BaseActivity {
    private static final String TAG = "WBStatusesActivity";
    /** Intent传值的键（用户id） */
    private static final String INTENT_UID = "uid";
    /** Intent传值的键（用户友好名称） */
    private static final String INTENT_NAME = "name";

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private StatusListAdapter mAdapter;

    private int mRefreshCount;
    private FavoritesAPI mFApi;
    private int mPage = 1;      // 当前页数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_favorites);
        ButterKnife.bind(this);

        // 每次刷新微博数
        mRefreshCount = BaseSettings.sSettings.refreshCount;
        Intent intent = getIntent();
        String name = intent.getStringExtra(INTENT_NAME);
        mTvTitle.setText(getString(R.string.title_favorites, name));
        // 创建适配器
        mAdapter = new StatusListAdapter(WBFavoritesActivity.this, null, true);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新
                mPage = 1;
                loadFavorites(1, true);
                mSwipeRefresh.setRefreshing(true);
            }
        });
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                if (mPage != 0) {
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    loadFavorites(mPage, false);
                }
            }
        });

        loadFavorites(1, true);
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, long uid, String name) {
        Intent intent = new Intent(context, WBFavoritesActivity.class);
        intent.putExtra(INTENT_UID, uid);
        intent.putExtra(INTENT_NAME, name);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    private void loadFavorites(int page, final boolean refresh) {
        if (mFApi == null) {
            mFApi = new FavoritesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mFApi.favorites(mRefreshCount, page, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                if (!TextUtils.isEmpty(s)) {
                    List<Favorite> favorites = new ArrayList<Favorite>(mRefreshCount);
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONArray array = obj.optJSONArray("favorites");
                        if (array != null && array.length() > 0) {
                            int length = array.length();
                            for (int i = 0; i < length; i++) {
                                favorites.add(Favorite.parse(array.optJSONObject(i)));
                            }
                        } else {
                            AppToast.showToast(R.string.mark_none);
                            return;
                        }
                        int totalNumber = obj.optInt("total_number", 0);
                        if (refresh) {
                            mAdapter.setStatuses(favorites, true);
                            mSwipeRefresh.getLayoutManager().scrollToPosition(0);
                        } else {
                            mAdapter.addStatuses(favorites, true);
                        }
                        int totalPage = (int) Math.ceil((float) totalNumber / mRefreshCount);
                        if (mPage >= totalPage) {
                            // 表示没有更多数据了
                            mPage = 0;
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        } else {
                            // 更新mPage
                            mPage++;
                            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
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
                AppToast.showToast(R.string.get_mark_failure);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(WBFavoritesActivity.class);
        super.onDestroy();
    }
}
