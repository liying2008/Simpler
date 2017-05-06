package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.bean.OpenSourceLib;
import cc.duduhuo.simpler.listener.OnDialogOpListener;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/5/6 14:18
 * 版本：1.0
 * 描述：应用中用得到开源库列表
 * 备注：
 * =======================================================
 */
public class ThirdOpensAdapter extends RecyclerView.Adapter<ThirdOpensAdapter.ViewHolder> {
    private List<OpenSourceLib> mLibs;
    private Activity mActivity;

    public ThirdOpensAdapter(Activity activity, List<OpenSourceLib> libs) {
        this.mActivity = activity;
        this.mLibs = libs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_third_open, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OpenSourceLib lib = mLibs.get(position);
        holder.mTvName.setText(lib.name);
        holder.mTvAuthor.setText(lib.author);
        holder.mTvDesc.setText(lib.description);
        holder.mRlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showSelectDialog(mActivity, "复制开源地址", "在浏览器中打开", new OnDialogOpListener() {
                    @Override
                    public void onOp1() {
                        CommonUtils.copyText(mActivity, lib.url);
                        AppToast.showToast(R.string.copied);
                    }

                    @Override
                    public void onOp2() {
                        CommonUtils.openBrowser(mActivity, lib.url);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mLibs == null) return 0;
        return mLibs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlItem)
        RelativeLayout mRlItem;
        @BindView(R.id.tvName)
        TextView mTvName;
        @BindView(R.id.tvAuthor)
        TextView mTvAuthor;
        @BindView(R.id.tvDesc)
        TextView mTvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
