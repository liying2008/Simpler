package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.PhotoViewActivity;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/25 22:10
 * 版本：1.0
 * 描述：微博相册列表适配器
 * 备注：
 * =======================================================
 */
public class AlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AlbumsAdapter";
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private String mFooterInfo = "";
    private List<String> mPhotoList = new ArrayList<>(1);
    private ArrayList<String> mLargePhotos = new ArrayList<>(1);

    public AlbumsAdapter(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mPhotoList.size());
    }

    /**
     * 刷新相册
     *
     * @param albums
     */
    public void setAlbums(List<String> albums) {
        if (albums != null) {
            mPhotoList.clear();
            mLargePhotos.clear();
            mPhotoList.addAll(albums);
            for (int i = 0; i < albums.size(); i++) {
                mLargePhotos.add(albums.get(i).replace("/thumbnail/", "/large/"));
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 加载更多图片
     *
     * @param albums
     */
    public void addAlbums(List<String> albums) {
        if (albums != null) {
            int start = mPhotoList.size();
            mPhotoList.addAll(albums);
            for (int i = 0; i < albums.size(); i++) {
                mLargePhotos.add(albums.get(i).replace("/thumbnail/", "/large/"));
            }
            notifyItemRangeInserted(start, albums.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_album, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            String url = mPhotoList.get(position).replace("/thumbnail/", "/bmiddle/");
            if (url.endsWith(".gif")) {
                itemHolder.mIvGifTag.setVisibility(View.VISIBLE);
            } else {
                itemHolder.mIvGifTag.setVisibility(View.GONE);
            }
            Glide.with(mActivity).load(url).asBitmap().into(itemHolder.mIvPhoto);
            itemHolder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PhotoViewActivity.newIntent(mActivity, mPhotoList.size(), holder.getAdapterPosition(), mLargePhotos);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, "image");
                    ActivityCompat.startActivity(mActivity, intent, options.toBundle());
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPhoto)
        ImageView mIvPhoto;
        @BindView(R.id.ivGifTag)
        ImageView mIvGifTag;

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
