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
import com.sina.weibo.sdk.openapi.models.Group;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.GroupEditActivity;
import cc.duduhuo.simpler.activity.GroupManagerActivity;
import cc.duduhuo.simpler.activity.GroupMembersActivity;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.impl.GroupMgrOp;
import cc.duduhuo.simpler.util.DialogUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/29 17:02
 * 版本：1.0
 * 描述：分组列表适配器
 * 备注：
 * =======================================================
 */
public class GroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<Group> mGroups = new ArrayList<>(1);
    private String mFooterInfo = "";
    private GroupMgrOp mGroupMgrOp;

    public GroupsAdapter(Activity activity) {
        this.mActivity = activity;
        mGroupMgrOp = new GroupMgrOp(activity);
        mGroupMgrOp.setOnGroupDestroyListener(new MyGroupDestroyListener());
    }

    public void setGroups(List<Group> groups) {
        if (groups != null) {
            mGroups.clear();
            mGroups.addAll(groups);
            notifyDataSetChanged();
        }
    }

    /**
     * 更新Item
     *
     * @param position 位置
     * @param name     更新的分组名称
     * @param desc     更新的分组描述
     */
    public void updateGroupItem(int position, String name, String desc) {
        Group group = mGroups.get(position);
        group.name = name;
        group.description = desc;
        notifyItemChanged(position);
    }

    /**
     * 更新Item
     *
     * @param position 位置
     * @param decCount 分组成员减少的数量
     */
    public void updateGroupItem(int position, int decCount) {
        Group group = mGroups.get(position);
        group.member_count -= decCount;
        notifyItemChanged(position);
    }

    public void addGroup(Group group) {
        if (group != null) {
            mGroups.add(group);
            notifyItemInserted(mGroups.size() - 1);
        }
    }

    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mGroups.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_group, parent, false);
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
            final Group group = mGroups.get(position);
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Glide.with(mActivity).load(group.profile_image_url).into(itemHolder.mCivImage);
            itemHolder.mTvGroupName.setText(group.name);
            itemHolder.mTvMemberCount.setText(group.member_count + "个成员");

            itemHolder.mTvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = GroupEditActivity.newIntent(mActivity, GroupEditActivity.TYPE_EDIT,
                            group.id, itemHolder.getAdapterPosition(), group.name, group.description);
                    mActivity.startActivityForResult(intent, GroupManagerActivity.REQUEST_GROUP_EDIT);
                }
            });
            itemHolder.mTvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = App.getAlertDialogBuilder(mActivity).create();
                    View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_warning, null);
                    ((TextView) rootView.findViewById(R.id.tvTitle)).setText("确认删除该分组？");
                    TextView tvConfirm = (TextView) rootView.findViewById(R.id.tvConfirm);
                    TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
                    tvConfirm.setText("删除 [" + group.name + "]");
                    dialog.show();
                    DialogUtil.setBottom(dialog, rootView);
                    tvConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            AppToast.showToast(R.string.in_operation);
                            mGroupMgrOp.onDestroy(group.id, itemHolder.getAdapterPosition());
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
            itemHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = GroupMembersActivity.newIntent(mActivity, group.id, group.name, itemHolder.getAdapterPosition());
                    mActivity.startActivityForResult(intent, GroupManagerActivity.REQUEST_MEMBER_DEL);
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mGroups.size() + 1;
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
        @BindView(R.id.civImage)
        ImageView mCivImage;
        @BindView(R.id.tvGroupName)
        TextView mTvGroupName;
        @BindView(R.id.tvMemberCount)
        TextView mTvMemberCount;
        @BindView(R.id.tvEdit)
        TextView mTvEdit;
        @BindView(R.id.tvDelete)
        TextView mTvDelete;

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

    private class MyGroupDestroyListener implements GroupMgrOp.OnGroupDestroyListener {

        @Override
        public void onSuccess(int position) {
            // 删除成功
            AppToast.showToast("已删除");
            mGroups.remove(position);
            if (BaseConfig.sGroups != null) {
                BaseConfig.sGroups.remove(position);
            }
            notifyItemRemoved(position);
        }

        @Override
        public void onFailure() {
            // 删除失败
            AppToast.showToast("分组删除失败");
        }
    }
}
