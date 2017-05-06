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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.bean.Account;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/5 16:47
 * 版本：1.0
 * 描述：授权用户列表适配器
 * 备注：
 * =======================================================
 */
public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {
    private Activity mActivity;
    private List<Account> mAccounts = new ArrayList<>();
    private static final int TYPE_GLOBAL = 0x0000;
    private static final int TYPE_TOP = 0x0001;
    private static final int TYPE_MIDDLE = 0x0002;
    private static final int TYPE_BOTTOM = 0x0003;

    public AccountListAdapter(Activity activity, List<Account> accounts) {
        this.mActivity = activity;
        this.mAccounts.addAll(accounts);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_GLOBAL) {
            holder.rlItem.setBackgroundResource(R.drawable.usercenter_item_global_selector);
        } else if (type == TYPE_TOP) {
            holder.rlItem.setBackgroundResource(R.drawable.usercenter_item_top_selector);
        } else if (type == TYPE_BOTTOM) {
            holder.rlItem.setBackgroundResource(R.drawable.usercenter_item_bottom_selector);
        } else {
            holder.rlItem.setBackgroundResource(R.drawable.usercenter_item_middle_selector);
        }
        Account account = mAccounts.get(position);
        if ("".equals(account.headCachePath)) {
            if (!"".equals(account.headUrl)) {
                // 本地无头像缓存则向网络请求
                Glide.with(mActivity).load(account.headUrl).into(holder.civAccountHead);
            } else {
                // 无地址
                holder.civAccountHead.setImageResource(R.drawable.usercenter_head_default);
            }
        } else {
            Glide.with(mActivity).load(account.headCachePath).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.civAccountHead);
        }
        holder.tvAccountName.setText(account.name);
        if (account.uid.equals(BaseConfig.sUid)) {
            // 当前登录用户
            holder.ivAccountSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivAccountSelected.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1) {
            return TYPE_GLOBAL;
        } else if (position == 0) {
            return TYPE_TOP;
        } else if (position == getItemCount() - 1) {
            return TYPE_BOTTOM;
        }
        return TYPE_MIDDLE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlItem)
        RelativeLayout rlItem;
        /** 用户头像 */
        @BindView(R.id.civAccountHead)
        ImageView civAccountHead;
        /** 用户昵称 */
        @BindView(R.id.tvAccountName)
        TextView tvAccountName;
        /** 是否为当前用户 */
        @BindView(R.id.ivAccountSelected)
        ImageView ivAccountSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
