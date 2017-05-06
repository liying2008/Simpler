package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.bean.Photo;
import cc.duduhuo.simpler.listener.PostPicClickListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/7 20:42
 * 版本：1.0
 * 描述：发布带图片微博的图片列表适配器
 * 备注：
 * =======================================================
 */
public class PostPhotosAdapter extends RecyclerView.Adapter<PostPhotosAdapter.ViewHolder> {
    private static final int TYPE_PHOTO = 0x0000;
    private static final int TYPE_ADD = 0x0001;
    private Activity mActivity;
    private List<Photo> mPhotos = new ArrayList<>(1);
    private PostPicClickListener mListener;

    public PostPhotosAdapter(Activity activity, PostPicClickListener listener) {
        this.mActivity = activity;
        this.mListener = listener;
    }

    public void setData(Photo photo) {
        mPhotos.clear();
        mPhotos.add(photo);
        notifyDataSetChanged();
    }

    public void setData(List<Photo> photos) {
        this.mPhotos.clear();
        this.mPhotos.addAll(photos);
        notifyDataSetChanged();
    }

    public void addData(Photo photo) {
        if (photo != null) {
            mPhotos.add(photo);
            notifyDataSetChanged();
        }
    }

    public void deleteData(int position) {
        this.mPhotos.remove(position);
        notifyItemRemoved(position);
        if (this.mPhotos.size() == 8) {
            // 图片小于9个，显示“添加”图标
            notifyDataSetChanged();
        }
        if (mPhotos.isEmpty()) {
            notifyItemRemoved(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_post_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            Photo photo = mPhotos.get(position);
            holder.mIvDelete.setVisibility(View.VISIBLE);
            Glide.with(mActivity).load(photo.path).into(holder.mIvPhoto);
        } else {
            holder.mIvDelete.setVisibility(View.GONE);
            if (mPhotos.size() == 9 || mPhotos.size() == 0) {
                holder.mIvPhoto.setVisibility(View.GONE);
                return;
            } else {
                holder.mIvPhoto.setVisibility(View.VISIBLE);
                holder.mIvPhoto.setImageResource(R.drawable.post_pic_add);
                holder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onAdd();
                        }
                    }
                });
            }
        }

        holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    mListener.onDelete(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mPhotos.isEmpty()) {
            return 0;
        }
        return mPhotos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_ADD;
        }
        return TYPE_PHOTO;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPhoto)
        ImageView mIvPhoto;
        @BindView(R.id.ivDelete)
        ImageView mIvDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
