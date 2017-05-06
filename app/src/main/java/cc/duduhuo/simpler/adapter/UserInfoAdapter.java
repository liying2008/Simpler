package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.bean.KeyValue;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/30 10:52
 * 版本：1.0
 * 描述：用户信息列表适配器
 * 备注：
 * =======================================================
 */
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {
    private Activity mActivity;
    private List<KeyValue> mUserData = new ArrayList<>();   // 用户数据列表

    public UserInfoAdapter(Activity activity, List<KeyValue> userData) {
        this.mActivity = activity;
        if (userData != null) {
            this.mUserData.addAll(userData);
        }
    }

    public void setData(List<KeyValue> userData) {
        this.mUserData.clear();
        this.mUserData.addAll(userData);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_user_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KeyValue keyValue = mUserData.get(position);
        holder.mTvKey.setText(keyValue.key);
        holder.mTvValue.setText(keyValue.value);
    }

    @Override
    public int getItemCount() {
        return mUserData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvKey)
        TextView mTvKey;
        @BindView(R.id.tvValue)
        TextView mTvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
