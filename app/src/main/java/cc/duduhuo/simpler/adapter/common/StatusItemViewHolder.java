package cc.duduhuo.simpler.adapter.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/31 9:38
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public abstract class StatusItemViewHolder extends RecyclerView.ViewHolder {
    /** 微博被删除 */
    @BindView(R.id.tvGone)
    public TextView tvGone;
    /** 微博条目 */
    @BindView(R.id.llStatus)
    public LinearLayout llStatus;
    /** 微博可见分组 */
    @BindView(R.id.tvVisibleGroup)
    public TextView tvVisibleGroup;
    /** 头像 */
    @BindView(R.id.civHead)
    public ImageView civHead;
    /** 用户昵称 */
    @BindView(R.id.tvUser)
    public TextView tvUser;
    /** 删除 */
    @BindView(R.id.tvDel)
    public TextView mTvDel;
    /** 微博来源 */
    @BindView(R.id.tvSource)
    public TextView tvSource;
    /** 对微博操作 */
    @BindView(R.id.tvOp)
    public TextView tvOp;
    /** 微博创建日期 */
    @BindView(R.id.tvCreatedAt)
    public TextView tvCreatedAt;
    /** 是否是微博认证用户 */
    @BindView(R.id.ivAvatarVip)
    public ImageView ivAvatarVip;
    /** 微博信息内容 */
    @BindView(R.id.tvText)
    public TextView tvText;
    /** 被转发的原微博 */
    @BindView(R.id.llRetweetedStatus)
    public LinearLayout llRetweetedStatus;
    /** 原微博被作者删除 */
    @BindView(R.id.llRetweetedGone)
    public LinearLayout llRetweetedGone;
    /** 删除提示信息 */
    @BindView(R.id.tvGoneInfo)
    public TextView tvGoneInfo;
    /** 原微博作者昵称 */
    @BindView(R.id.tvRetweetedUser)
    public TextView tvRetweetedUser;
    /** 原微博内容 */
    @BindView(R.id.tvRetweetedText)
    public TextView tvRetweetedText;
    /** 原微博评论数布局 */
    @BindView(R.id.llRetweetedComments)
    public LinearLayout llRetweetedComments;
    /** 原微博转发数布局 */
    @BindView(R.id.llRetweetedReposts)
    public LinearLayout llRetweetedReposts;
    /** 原微博表态数布局 */
    @BindView(R.id.llRetweetedAttitudes)
    public LinearLayout llRetweetedAttitudes;
    /** 原微博的评论数 */
    @BindView(R.id.tvRetweetedCommentsCount)
    public TextView tvRetweetedCommentsCount;
    /** 原微博的转发数 */
    @BindView(R.id.tvRetweetedRepostsCount)
    public TextView tvRetweetedRepostsCount;
    /** 原微博的表态数 */
    @BindView(R.id.tvRetweetedAttitudesCount)
    public TextView tvRetweetedAttitudesCount;
    /** 微博发送的地理位置 */
    @BindView(R.id.tvPoi)
    public TextView tvPoi;
    /** 微博表态数/转发数/评论数布局 */
    @BindView(R.id.rlCount)
    public RelativeLayout rlCount;
    /** 微博表态数 */
    @BindView(R.id.tvAttitudesCount)
    public TextView tvAttitudesCount;
    /** 微博评论数 */
    @BindView(R.id.tvCommentsCount)
    public TextView tvCommentsCount;
    /** 微博转发数 */
    @BindView(R.id.tvRepostsCount)
    public TextView tvRepostsCount;
    /** 微博图片布局 */
    @BindView(R.id.llPics)
    public LinearLayout llPics;
    /** 原微博图片布局 */
    @BindView(R.id.llRetweetedPics)
    public LinearLayout llRetweetedPics;
    /** 单张图片显示 */
    @BindView(R.id.ivSinglePic)
    public ImageView ivSinglePic;
    /** 原微博单张图片显示 */
    @BindView(R.id.ivRetweetedSinglePic)
    public ImageView ivRetweetedSinglePic;
    /** 微博图片 */
    @BindViews({R.id.iv_1, R.id.iv_2, R.id.iv_3, R.id.iv_4, R.id.iv_5, R.id.iv_6, R.id.iv_7, R.id.iv_8, R.id.iv_9})
    public List<ImageView> ivs;
    /** 转发微博的图片 */
    @BindViews({R.id.ivRetweeted_1, R.id.ivRetweeted_2, R.id.ivRetweeted_3, R.id.ivRetweeted_4, R.id.ivRetweeted_5, R.id.ivRetweeted_6, R.id.ivRetweeted_7, R.id.ivRetweeted_8, R.id.ivRetweeted_9})
    public List<ImageView> ivRetweeteds;
    /** 微博图片行 */
    @BindViews({R.id.ll_1, R.id.ll_2, R.id.ll_3})
    public List<LinearLayout> lls;
    /** 原微博图片行 */
    @BindViews({R.id.llRetweeted_1, R.id.llRetweeted_2, R.id.llRetweeted_3})
    public List<LinearLayout> llRetweeteds;
    /** 微博单图Gif标签 */
    @BindView(R.id.ivGifTag)
    public ImageView ivGifTag;
    /** 原微博单图Gif标签 */
    @BindView(R.id.ivRetweetedGifTag)
    public ImageView ivRetweetedGifTag;
    /** 微博多图Gif标签 */
    @BindViews({R.id.ivGifTag_1, R.id.ivGifTag_2, R.id.ivGifTag_3, R.id.ivGifTag_4, R.id.ivGifTag_5, R.id.ivGifTag_6, R.id.ivGifTag_7, R.id.ivGifTag_8, R.id.ivGifTag_9})
    public List<ImageView> ivGifTags;
    /** 原微博多图Gif标签 */
    @BindViews({R.id.ivRetweetedGifTag_1, R.id.ivRetweetedGifTag_2, R.id.ivRetweetedGifTag_3, R.id.ivRetweetedGifTag_4, R.id.ivRetweetedGifTag_5, R.id.ivRetweetedGifTag_6, R.id.ivRetweetedGifTag_7, R.id.ivRetweetedGifTag_8, R.id.ivRetweetedGifTag_9})
    public List<ImageView> ivRetweetedGifTags;
    /** 微博单图Long标签 */
    @BindView(R.id.ivLongTag)
    public ImageView ivLongTag;
    /** 原微博单图Long标签 */
    @BindView(R.id.ivRetweetedLongTag)
    public ImageView ivRetweetedLongTag;
    /** 微博多图Long标签 */
    @BindViews({R.id.ivLongTag_1, R.id.ivLongTag_2, R.id.ivLongTag_3, R.id.ivLongTag_4, R.id.ivLongTag_5, R.id.ivLongTag_6, R.id.ivLongTag_7, R.id.ivLongTag_8, R.id.ivLongTag_9})
    public List<ImageView> ivLongTags;
    /** 原微博多图Long标签 */
    @BindViews({R.id.ivRetweetedLongTag_1, R.id.ivRetweetedLongTag_2, R.id.ivRetweetedLongTag_3, R.id.ivRetweetedLongTag_4, R.id.ivRetweetedLongTag_5, R.id.ivRetweetedLongTag_6, R.id.ivRetweetedLongTag_7, R.id.ivRetweetedLongTag_8, R.id.ivRetweetedLongTag_9})
    public List<ImageView> ivRetweetedLongTags;

//    /**
//     * 微博视频布局
//     */
//    @BindView(R.id.rlVideo)
//    RelativeLayout rlVideo;
//    /**
//     * 原微博视频布局
//     */
//    @BindView(R.id.rlRetweetedVideo)
//    RelativeLayout rlRetweetedVideo;
//    /**
//     * 微博视频截图
//     */
//    @BindView(R.id.ivVideoShot)
//    ImageView ivVideoShot;
//    /**
//     * 原微博视频截图
//     */
//    @BindView(R.id.ivRetweetedVideoShot)
//    ImageView ivRetweetedVideoShot;

    public StatusItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
