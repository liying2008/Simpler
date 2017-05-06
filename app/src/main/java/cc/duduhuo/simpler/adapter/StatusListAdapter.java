package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Favorite;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.common.StatusDataSetter;
import cc.duduhuo.simpler.adapter.common.StatusItemViewHolder;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.listener.OnSettingsChangeListener;
import cc.duduhuo.simpler.listener.impl.DelStatusOp;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/6 16:27
 * 版本：1.0
 * 描述：微博列表适配器
 * 备注：
 * =======================================================
 */
public class StatusListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnSettingsChangeListener {
    private static final String TAG = "Status";
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<Status> mStatuses = new ArrayList<>();
    private String mFooterInfo = "";
    private StatusDataSetter mDataSetter;
    private boolean mIsFavorite = false;
    private DelStatusOp.OnDelStatusOpListener mDelStatusOpListener = new DelStatusOp.OnDelStatusOpListener() {
        @Override
        public void onSuccess(int position) {
            mStatuses.remove(position);
            if (position == 0) {
                notifyDataSetChanged();
            } else {
                notifyItemRemoved(position);
            }
            AppToast.showToast(R.string.deleted);
        }

        @Override
        public void onFailure(String msg) {
            AppToast.showToast(R.string.delete_failure);
        }
    };

    public StatusListAdapter(Activity activity, List<Status> statuses) {
        this.mActivity = activity;
        if (statuses != null) {
            this.mStatuses.addAll(statuses);
        }
        this.mIsFavorite = false;
        mDataSetter = new StatusDataSetter(activity, mDelStatusOpListener);
        App.getInstance().setOnSettingsChangeListener(this);
    }

    public StatusListAdapter(Activity activity, List<Favorite> favorites, boolean favorite) {
        this.mActivity = activity;
        if (favorites != null) {
            int size = favorites.size();
            for (int i = 0; i < size; i++) {
                this.mStatuses.add(favorites.get(i).status);
            }
        }
        this.mIsFavorite = favorite;
        mDataSetter = new StatusDataSetter(activity, mDelStatusOpListener);
        App.getInstance().setOnSettingsChangeListener(this);
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mStatuses.size());
    }

    /**
     * 加载更多微博
     *
     * @param statuses 微博List
     */
    public void addStatuses(List<Status> statuses) {
        if (statuses != null) {
            int start = mStatuses.size();
            mStatuses.addAll(statuses);
            notifyItemRangeInserted(start, statuses.size());
        }
    }

    /**
     * 加载更多收藏
     *
     * @param favorites 收藏List
     */
    public void addStatuses(List<Favorite> favorites, boolean favorite) {
        if (favorites != null) {
            int start = mStatuses.size();
            int size = favorites.size();
            for (int i = 0; i < size; i++) {
                this.mStatuses.add(favorites.get(i).status);
            }
            notifyItemRangeInserted(start, size);
        }
    }

    /**
     * 刷新微博
     *
     * @param statuses 微博List
     */
    public void setStatuses(List<Status> statuses) {
        if (statuses != null) {
            mStatuses.clear();
            mStatuses.addAll(statuses);
            notifyDataSetChanged();
        }
    }

    /**
     * 刷新收藏
     *
     * @param favorites 收藏List
     */
    public void setStatuses(List<Favorite> favorites, boolean favorite) {
        if (favorites != null) {
            mStatuses.clear();
            int size = favorites.size();
            for (int i = 0; i < size; i++) {
                this.mStatuses.add(favorites.get(i).status);
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_status, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            final Status status = mStatuses.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            // 设置微博字体大小
            itemHolder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, BaseSettings.sSettings.fontSize);
            itemHolder.tvRetweetedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, BaseSettings.sSettings.fontSize - 2);

            mDataSetter.loadStatusData(position, status, itemHolder, true, false);

        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mStatuses.size() + 1;
    }

    @Override
    public void onTextSizeChange(int size) {
        notifyDataSetChanged();
    }

    @Override
    public void onPicQualityChange() {
        mDataSetter.setPicQuality();
        notifyDataSetChanged();
    }

    @Override
    public void onFavoriteStateChange(int position, boolean favorite) {
        try {
            mStatuses.get(position).favorited = favorite;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.d(TAG, position + ", " + mStatuses.size());
        }
        if (mIsFavorite) {
            if (!favorite) {
                mStatuses.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    private class ItemViewHolder extends StatusItemViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFooter)
        TextView mTvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
