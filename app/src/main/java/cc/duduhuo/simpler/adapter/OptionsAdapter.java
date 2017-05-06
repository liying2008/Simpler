package cc.duduhuo.simpler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.bean.Option;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/15 14:11
 * 版本：1.0
 * 描述：设置中的选项列表适配器
 * 备注：
 * =======================================================
 */
public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {
    private OnOptionItemSelectListener mListener;
    private List<Option> mOptionList = new ArrayList<>(1);

    public OptionsAdapter(List<Option> options) {
        this.mOptionList.addAll(options);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Option option = mOptionList.get(position);
        holder.mTvItemName.setText(option.name);
        if (option.selected) {
            holder.mIvSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mIvSelected.setVisibility(View.GONE);
        }
        holder.mRlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // FIXME: 不清楚为什么 holder.getAdapterPosition() 总是返回-1
                    setOptionSelectState(holder.getPosition());
                    mListener.onSelect(holder.getPosition());
                }
            }
        });
    }

    /**
     * 重新设置选中状态
     *
     * @param position 选中选项的位置
     */
    private void setOptionSelectState(int position) {
        int count = getItemCount();
        Option option;
        for (int i = 0; i < count; i++) {
            option = mOptionList.get(i);
            option.selected = false;
        }
        mOptionList.get(position).selected = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mOptionList == null) {
            return 0;
        }
        return mOptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlItem)
        RelativeLayout mRlItem;
        @BindView(R.id.tvItemName)
        TextView mTvItemName;
        @BindView(R.id.ivSelected)
        ImageView mIvSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnOptionItemSelect(OnOptionItemSelectListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置选项选择监听接口
     */
    public interface OnOptionItemSelectListener {
        /**
         * 选择一个选项
         *
         * @param position
         */
        void onSelect(int position);
    }
}
