package cc.duduhuo.imageselector.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
 * 日期：2017/4/9 18:45
 * 版本：1.0
 * 描述：大图预览界面ViewPager适配器
 * 备注：
 * =======================================================
 */
public class PreviewAdapter extends PagerAdapter {

    private Activity activity;
    private List<Image> images;
    private ImageSelectConfig config;
    private OnItemClickListener listener;

    public PreviewAdapter(Activity activity, List<Image> images, ImageSelectConfig config) {
        this.activity = activity;
        this.images = images;
        this.config = config;
    }

    @Override
    public int getCount() {
        if (config.needCamera)
            return images.size() - 1;
        else
            return images.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View root = View.inflate(activity, R.layout.item_image_preview, null);
        final ImageView photoView = (ImageView) root.findViewById(R.id.ivImage);
        final ImageView ivChecked = (ImageView) root.findViewById(R.id.ivPhotoChecked);

        if (config.multiSelect) {

            ivChecked.setVisibility(View.VISIBLE);
            final Image image = images.get(config.needCamera ? position + 1 : position);
            if (Global.imageList.contains(image.path)) {
                ivChecked.setImageResource(R.drawable.photo_selected_large);
            } else {
                ivChecked.setImageResource(R.drawable.photo_unselected_large);
            }

            ivChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(position, image);
                        if (ret == 1) { // 局部刷新
                            if (Global.imageList.contains(image.path)) {
                                ivChecked.setImageResource(R.drawable.photo_selected_large);
                            } else {
                                ivChecked.setImageResource(R.drawable.photo_unselected_large);
                            }
                        }
                    }
                }
            });

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageClick(position, images.get(position));
                    }
                }
            });
        } else {
            ivChecked.setVisibility(View.GONE);
        }

        container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        displayImage(photoView, images.get(config.needCamera ? position + 1 : position).path);

        return root;
    }

    private void displayImage(ImageView photoView, String path) {
        config.loader.displayImage(activity, path, photoView);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
