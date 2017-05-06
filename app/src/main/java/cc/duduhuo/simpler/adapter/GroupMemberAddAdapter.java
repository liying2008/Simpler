package cc.duduhuo.simpler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Group;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/16 19:03
 * 版本：1.0
 * 描述：将关注的人添加到分组
 * 备注：
 * =======================================================
 */
public class GroupMemberAddAdapter extends RecyclerView.Adapter<GroupMemberAddAdapter.ViewHolder> {
    private List<Group> mGroups;
    private boolean[] mFlag;
    private OnSelectListener mListener;

    public GroupMemberAddAdapter() {
    }

    public void setGroups(List<Group> groups) {
        this.mGroups = groups;
        if (mGroups != null && mGroups.size() > 0) {
            mFlag = new boolean[mGroups.size()];
            notifyDataSetChanged();
        }
    }

    public void setGroups(List<Group> groups, List<Group> selections) {
        this.mGroups = groups;
        if (mGroups != null && mGroups.size() > 0) {
            mFlag = new boolean[mGroups.size()];
            setSelections(selections);
            notifyDataSetChanged();
        }
    }

    /**
     * 设置已经选中的分组
     *
     * @param groups
     */
    private void setSelections(List<Group> groups) {
        if (groups != null && !groups.isEmpty()) {
            for (int i = 0; i < groups.size(); i++) {
                for (int j = 0; j < mGroups.size(); j++) {
                    if (groups.get(i).id == mGroups.get(j).id) {
                        if (!mFlag[j]) {
                            mFlag[j] = true;
                            if (mListener != null) {
                                mListener.onSelect(mGroups.get(j).id, true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Group group = mGroups.get(position);
        holder.mTvGroupName.setText(group.name);
        holder.mCb.setChecked(mFlag[position]); //用数组中的值设置CheckBox的选中状态

        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mCb.setChecked(!holder.mCb.isChecked());
            }
        });
        // 再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFlag[holder.getAdapterPosition()] = isChecked;
                if (mListener != null) {
                    mListener.onSelect(group.id, holder.mCb.isChecked());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mGroups == null) {
            return 0;
        }
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llItem)
        LinearLayout mLlItem;
        @BindView(R.id.cb)
        CheckBox mCb;
        @BindView(R.id.tvGroupName)
        TextView mTvGroupName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
    }

    /**
     * 分组选择监听
     */
    public interface OnSelectListener {
        /**
         * 选择或取消选择某一分组
         *
         * @param gid
         * @param checked
         */
        void onSelect(long gid, boolean checked);
    }
}
