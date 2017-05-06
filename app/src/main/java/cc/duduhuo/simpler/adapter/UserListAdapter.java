package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
import cc.duduhuo.simpler.activity.WBUserHomeActivity;
import cc.duduhuo.simpler.listener.OnFriendshipListener;
import cc.duduhuo.simpler.util.UserVerify;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/29 18:46
 * 版本：1.0
 * 描述：粉丝列表、关注列表适配器
 * 备注：
 * =======================================================
 */
public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<User> mFollowers = new ArrayList<>();
    private String mFooterInfo = "";
    private OnFriendshipListener mListener;
    private boolean mIsAuth;

    /**
     * 构造方法
     * @param activity
     * @param isAuth 是否是当前授权用户的关注或粉丝
     */
    public UserListAdapter(Activity activity, boolean isAuth) {
        this.mActivity = activity;
        this.mIsAuth = isAuth;
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mFollowers.size());
    }

    public void setData(List<User> followers) {
        this.mFollowers.clear();
        this.mFollowers.addAll(followers);
        notifyDataSetChanged();
    }

    public void addData(List<User> followers) {
        int start = mFollowers.size();
        mFollowers.addAll(followers);
        notifyItemRangeInserted(start, followers.size());
    }

    /**
     * 成功关注某用户
     * @param position
     */
    public void createSuccess(int position) {
        this.mFollowers.get(position).following = true;
        notifyItemChanged(position);
    }

    /**
     * 成功取消关注某用户
     * @param position
     */
    public void destroySuccess(int position) {
        this.mFollowers.get(position).following = false;
        notifyItemChanged(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_user, parent, false);
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
            final User user = mFollowers.get(position);
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            // 加载头像
            Glide.with(mActivity).load(user.avatar_large).into(itemHolder.mCivHead);
            // 加载昵称
            itemHolder.mTvName.setText(user.name);
            // 加载描述/认证信息
            UserVerify.verify(user, itemHolder.mIvAvatarVip, itemHolder.mTvDescription);
            if (mIsAuth) {
                itemHolder.mTvRelation.setVisibility(View.VISIBLE);
                // 授权用户是否关注该用户
                if (user.following) {
                    itemHolder.mTvRelation.setBackgroundResource(R.drawable.selector_relation_btn);
                    itemHolder.mTvRelation.setTextColor(ContextCompat.getColor(mActivity, R.color.unfollow_color));
                    itemHolder.mTvRelation.setText("已关注");
                } else {
                    itemHolder.mTvRelation.setBackgroundResource(R.drawable.selector_relation_blue_btn);
                    itemHolder.mTvRelation.setTextColor(ContextCompat.getColor(mActivity, R.color.follow_color));
                    itemHolder.mTvRelation.setText("加关注");
                }
                // 点击关注或取消关注
                itemHolder.mTvRelation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user.following) {
                            if (mListener != null) {
                                mListener.onDestroy(itemHolder.getAdapterPosition(), user.id, user.screen_name);
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onCreate(itemHolder.getAdapterPosition(), user.id, user.screen_name);
                            }
                        }
                    }
                });
            } else {
                itemHolder.mTvRelation.setVisibility(View.GONE);
            }

            // 点击条目打开用户HomeActivity
            itemHolder.mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = WBUserHomeActivity.newIntent(mActivity, user.screen_name);
                    mActivity.startActivity(intent);
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footHolder = (FooterViewHolder) holder;
            footHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mFollowers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    public void setOnFriendshipListener(OnFriendshipListener listener) {
        this.mListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlItem)
        RelativeLayout mRlItem;
        @BindView(R.id.civHead)
        CircleImageView mCivHead;
        @BindView(R.id.ivAvatarVip)
        ImageView mIvAvatarVip;
        @BindView(R.id.tvName)
        TextView mTvName;
        @BindView(R.id.tvDescription)
        TextView mTvDescription;
        @BindView(R.id.tvRelation)
        TextView mTvRelation;

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
}
