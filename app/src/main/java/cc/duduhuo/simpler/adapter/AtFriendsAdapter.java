package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.util.UserVerify;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/11 21:20
 * 版本：1.0
 * 描述：At列表适配器
 * 备注：
 * =======================================================
 */
public class AtFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<User> mUsers = new ArrayList<>();
    private String mFooterInfo = "";
    private OnAtListener mListener;

    public AtFriendsAdapter(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mUsers.size());
    }

    public void setData(List<User> followers) {
        this.mUsers.clear();
        this.mUsers.addAll(followers);
        notifyDataSetChanged();
    }

    public void addData(List<User> followers) {
        int start = mUsers.size();
        mUsers.addAll(followers);
        notifyItemRangeInserted(start, followers.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_at_friend, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            final User user = mUsers.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            // 加载头像
            Glide.with(mActivity).load(user.profile_image_url).into(itemHolder.mCivHead);
            // 加载昵称
            itemHolder.mTvName.setText(user.screen_name);
            UserVerify.verify(user, itemHolder.mIvAvatarVip, null);

            // 点击条目打开用户HomeActivity
            itemHolder.mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAt(user.screen_name);
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footHolder = (FooterViewHolder) holder;
            footHolder.mTvFooter.setText(mFooterInfo);
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlItem)
        RelativeLayout mRlItem;
        @BindView(R.id.civHead)
        ImageView mCivHead;
        @BindView(R.id.ivAvatarVip)
        ImageView mIvAvatarVip;
        @BindView(R.id.tvName)
        TextView mTvName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnAtListener(OnAtListener listener) {
        this.mListener = listener;
    }

    /**
     * 点击某用户
     */
    public interface OnAtListener {
        void onAt(String screenName);
    }
}
