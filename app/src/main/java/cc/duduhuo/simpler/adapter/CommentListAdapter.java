package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.WBCommentActivity;
import cc.duduhuo.simpler.adapter.common.StatusDataSetter;
import cc.duduhuo.simpler.adapter.common.StatusItemViewHolder;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.impl.DelCommentOp;
import cc.duduhuo.simpler.listener.impl.ViewUserOp;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.NumberFormatter;
import cc.duduhuo.simpler.util.UserVerify;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/30 22:44
 * 版本：1.0
 * 描述：微博评论列表适配器
 * 备注：
 * =======================================================
 */
public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEAD = 0x0000;
    private static final int TYPE_ITEM = 0x0001;
    private static final int TYPE_FOOT = 0x0002;
    private Activity mActivity;
    private List<Comment> mComments = new ArrayList<>();
    private Status mStatus;
    private String mFooterInfo = "";
    private static long sNow;
    private StatusDataSetter mDataSetter;
    private DelCommentOp.OnDelCommentOpListener mDelCommentOpListener = new DelCommentOp.OnDelCommentOpListener() {
        @Override
        public void onSuccess(int position) {
            mComments.remove(position - 1);
            if (position == 1) {
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
    private DelCommentOp mDelCommentOp;

    public CommentListAdapter(Activity activity) {
        this.mActivity = activity;
        mComments = new ArrayList<>(1);
        mDataSetter = new StatusDataSetter(activity, null);
        sNow = System.currentTimeMillis();
    }

    public void setStatus(Status status) {
        this.mStatus = status;
        notifyItemChanged(0);
    }

    public void setComments(List<Comment> comments) {
        this.mComments.clear();
        this.mComments.addAll(comments);
        notifyDataSetChanged();
    }

    /**
     * 加载更多评论
     *
     * @param comments
     */
    public void addComments(List<Comment> comments) {
        if (comments != null) {
            int start = mComments.size() + 1;
            mComments.addAll(comments);
            notifyItemRangeInserted(start, comments.size());
        }
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        if (mComments.size() == 0) {
            notifyItemChanged(0);
        } else {
            notifyItemChanged(mComments.size() + 1);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
        } else if (viewType == TYPE_HEAD) {
            return new HeaderItemView(inflater.inflate(R.layout.item_status, parent, false));
        } else if (viewType == TYPE_FOOT) {
            return new FooterViewHolder(inflater.inflate(R.layout.item_footer_view, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final Comment comment = mComments.get(position - 1);
            Glide.with(mActivity).load(comment.user.avatar_large).into(itemHolder.mCivHead);
            itemHolder.mTvUser.setText(comment.user.name);
            itemHolder.mTvSource.setText(Html.fromHtml(comment.source).toString());
            itemHolder.mTvCreatedAt.setText(NumberFormatter.dateTransfer(sNow, comment.created_at));
            if (comment.user.idstr.equals(BaseConfig.sUid)) {
                // 自己的评论，显示删除按钮
                itemHolder.mTvDel.setVisibility(View.VISIBLE);
            } else {
                itemHolder.mTvDel.setVisibility(View.GONE);
            }
            // 评论内容
            mDataSetter.formatContent(true, comment.id, comment.text, itemHolder.mTvComment);
            UserVerify.verify(comment.user, itemHolder.mIvAvatarVip, null);
            // 查看原微博用户主页
            itemHolder.mCivHead.setOnClickListener(new ViewUserOp(mActivity, comment.user.screen_name));
            itemHolder.mTvUser.setOnClickListener(new ViewUserOp(mActivity, comment.user.screen_name));
            // 删除评论
            itemHolder.mTvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = App.getAlertDialogBuilder(mActivity).create();
                    View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_warning, null);
                    ((TextView) rootView.findViewById(R.id.tvTitle)).setText("确定删除评论？");
                    TextView tvConfirm = (TextView) rootView.findViewById(R.id.tvConfirm);
                    TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
                    tvConfirm.setText("删除");
                    dialog.show();
                    DialogUtil.setBottom(dialog, rootView);
                    tvConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (mDelCommentOp == null) {
                                mDelCommentOp = new DelCommentOp(mActivity);
                            }
                            AppToast.showToast("点击：" +itemHolder.getAdapterPosition());
                            mDelCommentOp.onDestroy(comment.id, itemHolder.getAdapterPosition());
                            mDelCommentOp.setOnDelCommentOpListener(mDelCommentOpListener);
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
            // 点击Item复制或回复评论
            itemHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(mActivity);
                    View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_comment_op, null);
                    TextView tvReply = (TextView) rootView.findViewById(R.id.tvReply);
                    TextView tvCopy = (TextView) rootView.findViewById(R.id.tvCopy);
                    builder.setView(rootView);
                    final AlertDialog dialog = builder.create();
                    tvReply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.startActivity(WBCommentActivity.newIntent(mActivity,
                                    mStatus.id, comment.id, comment.user.screen_name, comment.text));
                            dialog.dismiss();
                        }
                    });
                    tvCopy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonUtils.copyText(mActivity, comment.text);
                            dialog.dismiss();
                            AppToast.showToast("已复制。");
                        }
                    });
                    dialog.show();
                }
            });
        } else if (getItemViewType(position) == TYPE_HEAD) {
            HeaderItemView headerView = (HeaderItemView) holder;
            // 给Item加载数据
            mDataSetter.loadStatusData(position, mStatus, headerView, false, true);
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        if (mComments.size() == 0) {
            return 1;
        }
        return mComments.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        } else if (position == 0) {
            return TYPE_HEAD;
        }
        return TYPE_ITEM;
    }

    public class HeaderItemView extends StatusItemViewHolder {

        public HeaderItemView(View itemView) {
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llItem)
        LinearLayout mLlItem;
        @BindView(R.id.civHead)
        CircleImageView mCivHead;
        @BindView(R.id.tvUser)
        TextView mTvUser;
        @BindView(R.id.tvDel)
        TextView mTvDel;
        @BindView(R.id.tvCreatedAt)
        TextView mTvCreatedAt;
        @BindView(R.id.tvSource)
        TextView mTvSource;
        @BindView(R.id.ivAvatarVip)
        ImageView mIvAvatarVip;
        @BindView(R.id.tvComment)
        TextView mTvComment;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
