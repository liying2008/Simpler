package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.GroupMembersActivity;
import cc.duduhuo.simpler.activity.WBUserHomeActivity;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.listener.impl.GroupMemberMgrOp;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.UserVerify;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/30 15:53
 * 版本：1.0
 * 描述：分组好友列表适配器
 * 备注：
 * =======================================================
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<User> mFriends = new ArrayList<>();
    private String mFooterInfo = "";
    private long mListId;
    private int mGroupPosition;
    private GroupMemberMgrOp mGroupMemberMgrOp;

    public GroupMembersAdapter(Activity activity, long listId, int position) {
        this.mActivity = activity;
        this.mListId = listId;
        this.mGroupPosition = position;
        mGroupMemberMgrOp = new GroupMemberMgrOp(activity);
        mGroupMemberMgrOp.setOnGroupMemberDestroyListener(new MyGroupMemberDestroyListener());
    }

    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mFriends.size());
    }

    public void setData(List<User> followers) {
        if (followers != null) {
            this.mFriends.clear();
            this.mFriends.addAll(followers);
            notifyDataSetChanged();
        }
    }

    public void addData(List<User> followers) {
        if (followers != null && !followers.isEmpty()) {
            int start = this.mFriends.size();
            this.mFriends.addAll(followers);
            notifyItemRangeInserted(start, followers.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_group_member, parent, false);
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
            final User user = mFriends.get(position);
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            // 加载头像
            Glide.with(mActivity).load(user.avatar_large).into(itemHolder.mCivHead);
            // 加载昵称
            itemHolder.mTvName.setText(user.name);
            // 加载描述/认证信息
            UserVerify.verify(user, itemHolder.mIvAvatarVip, itemHolder.mTvDescription);
            itemHolder.mTvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = App.getAlertDialogBuilder(mActivity).create();
                    View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_warning, null);
                    ((TextView) rootView.findViewById(R.id.tvTitle)).setText("确定从该分组中移出？");
                    TextView tvConfirm = (TextView) rootView.findViewById(R.id.tvConfirm);
                    TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
                    tvConfirm.setText("移出");
                    dialog.show();
                    DialogUtil.setBottom(dialog, rootView);
                    tvConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            AppToast.showToast(R.string.in_operation);
                            mGroupMemberMgrOp.onDestroy(user.id, mListId, itemHolder.getAdapterPosition());
                        }
                    });
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            // 点击条目打开用户HomeActivity
            itemHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
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
        return mFriends.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llItem)
        LinearLayout mLlItem;
        @BindView(R.id.civHead)
        CircleImageView mCivHead;
        @BindView(R.id.ivAvatarVip)
        ImageView mIvAvatarVip;
        @BindView(R.id.tvName)
        TextView mTvName;
        @BindView(R.id.tvDescription)
        TextView mTvDescription;
        @BindView(R.id.tvRemove)
        TextView mTvRemove;

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

    private class MyGroupMemberDestroyListener implements GroupMemberMgrOp.OnGroupMemberDestroyListener {
        private Intent data;
        private int count = 0;

        public MyGroupMemberDestroyListener() {
            data = new Intent();
            data.putExtra(GroupMembersActivity.INTENT_POSITION, mGroupPosition);
        }

        @Override
        public void onSuccess(int position, String groupName) {
            AppToast.showToast("已从 [" + groupName + "] 分组中移出");
            mFriends.remove(position);
            notifyItemRemoved(position);
            count++;
            data.putExtra(GroupMembersActivity.INTENT_MEMBER_DEL, count);
            mActivity.setResult(Activity.RESULT_OK, data);
        }

        @Override
        public void onFailure() {
            AppToast.showToast("移出失败");
        }
    }
}
