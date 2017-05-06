package cc.duduhuo.simpler.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

import cc.duduhuo.simpler.config.BaseConfig;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/11 22:44
 * 版本：1.0
 * 描述：可以下拉刷新和分段加载的SwipeRefreshLayout <br />
 * 备注：
 * =======================================================
 */
public class DSwipeRefresh extends SwipeRefreshLayout {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private boolean mGrid = false;
    private static int sLastVisibleItem;
    private OnLoadingListener mOnLoadingListener;

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == SCROLL_STATE_IDLE && sLastVisibleItem + 1 == mAdapter.getItemCount()) {
                if (mOnLoadingListener != null) {
                    mOnLoadingListener.onLoading();
                }
            }
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    // 当屏幕停止滚动时加载图片
                    try {
                        if (mContext != null) {
                            Glide.with(getContext()).resumeRequests();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SCROLL_STATE_DRAGGING:
                    // 当屏幕滚动且用户的手指还在屏幕上时，停止加载图片
                case SCROLL_STATE_SETTLING:
                    // 屏幕惯性滑动时，停止加载图片
                    try {
                        if (mContext != null) {
                            Glide.with(getContext()).pauseRequests();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mGrid) {
                sLastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
            } else {
                sLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        }
    };

    public DSwipeRefresh(Context context) {
        this(context, null);
    }

    public DSwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    /**
     * 设置RecyclerView及其Adapter
     *
     * @param recyclerView RecyclerView
     * @param adapter      RecyclerView.Adapter
     */
    public void setRecyclerViewAndAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        this.mGrid = false;
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 设置RecyclerView及其Adapter
     *
     * @param recyclerView RecyclerView
     * @param adapter      RecyclerView.Adapter
     * @param spanCount    列数
     */
    public void setRecyclerViewAndAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter, int spanCount) {
        this.mGrid = true;
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
        mGridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void init() {
        // 设置进度圈颜色
        setColorSchemeResources(BaseConfig.sSwipeRefreshColor);
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.mOnLoadingListener = onLoadingListener;
    }

    /**
     * 得到RecyclerView的布局管理器
     *
     * @return
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        if (mGrid) {
            return mGridLayoutManager;
        } else {
            return mLayoutManager;
        }
    }

    /**
     * “分段加载”监听器
     */
    public interface OnLoadingListener {
        void onLoading();
    }
}
