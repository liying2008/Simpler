package cc.duduhuo.imageselector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import cc.duduhuo.imageselector.ImageSelectConfig;
import cc.duduhuo.imageselector.R;
import cc.duduhuo.imageselector.bean.Image;
import cc.duduhuo.imageselector.common.Global;
import cc.duduhuo.imageselector.common.OnItemClickListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 16:43
 * 版本：1.0
 * 描述：图片列表适配器
 * 备注：
 * =======================================================
 */
public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CAMERA = 0x0001;
    private static final int TYPE_PHOTO = 0x0000;
    private boolean showCamera;
    private boolean multiSelect;

    private ImageSelectConfig config;
    private Context context;
    private OnItemClickListener listener;
    private List<Image> imageList;

    public ImageListAdapter(Context context, List<Image> imageList, ImageSelectConfig config) {
        this.context = context;
        this.config = config;
        this.imageList = imageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PHOTO) {
            View photoView = LayoutInflater.from(context).inflate(R.layout.item_image_select, parent, false);
            return new PhotoViewHolder(photoView);
        } else if (viewType == TYPE_CAMERA) {
            View cameraView = LayoutInflater.from(context).inflate(R.layout.item_take_photo, parent, false);
            return new CameraViewHolder(cameraView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            final Image image = imageList.get(position);
            final PhotoViewHolder photoHolder = (PhotoViewHolder) holder;

            photoHolder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(holder.getAdapterPosition(), image);
                }
            });

            if (multiSelect) {
                photoHolder.ivPhotoChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int ret = listener.onCheckedClick(holder.getAdapterPosition(), image);
                            if (ret == 1) { // 局部刷新
                                if (Global.imageList.contains(image.path)) {
                                    photoHolder.ivPhotoChecked.setImageResource(R.drawable.photo_selected_large);
                                } else {
                                    photoHolder.ivPhotoChecked.setImageResource(R.drawable.photo_unselected_large);
                                }
                            }
                        }
                    }
                });
            }

            config.loader.displayImage(context, image.path, photoHolder.ivImage);

            if (multiSelect) {
                photoHolder.ivPhotoChecked.setVisibility(View.VISIBLE);
                if (Global.imageList.contains(image.path)) {
                    photoHolder.ivPhotoChecked.setImageResource(R.drawable.photo_selected_large);
                } else {
                    photoHolder.ivPhotoChecked.setImageResource(R.drawable.photo_unselected_large);
                }
            } else {
                photoHolder.ivPhotoChecked.setVisibility(View.GONE);
            }

        } else if (getItemViewType(position) == TYPE_CAMERA) {
            ((CameraViewHolder) holder).rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageClick(holder.getAdapterPosition(), null);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showCamera) {
            return TYPE_CAMERA;
        }
        return TYPE_PHOTO;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlItem;
        private ImageView ivImage;
        private ImageView ivPhotoChecked;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            rlItem = (RelativeLayout) itemView.findViewById(R.id.rlItem);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            ivPhotoChecked = (ImageView) itemView.findViewById(R.id.ivPhotoChecked);
        }
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlItem;

        public CameraViewHolder(View itemView) {
            super(itemView);
            rlItem = (RelativeLayout) itemView.findViewById(R.id.rlItem);
        }
    }
}
