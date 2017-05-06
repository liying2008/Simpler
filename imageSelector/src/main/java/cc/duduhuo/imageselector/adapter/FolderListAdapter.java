package cc.duduhuo.imageselector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.imageselector.ImageSelectConfig;
import cc.duduhuo.imageselector.R;
import cc.duduhuo.imageselector.bean.Folder;
import cc.duduhuo.imageselector.common.OnFolderChangeListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 15:47
 * 版本：1.0
 * 描述：图片文件夹列表适配器
 * 备注：
 * =======================================================
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.ViewHolder> {
    private Context context;
    private List<Folder> folderList = new ArrayList<>(1);
    private ImageSelectConfig config;

    private int selected = 0;
    private OnFolderChangeListener listener;

    public FolderListAdapter(Context context, List<Folder> folderList, ImageSelectConfig config) {
        this.context = context;
        this.folderList = folderList;
        this.config = config;
    }

    public void setData(List<Folder> folders) {
        if (folders.size() > 0) {
            folderList.clear();
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_select_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        if (position == 0) {
            holder.tvFolderName.setText("全部图片");
            holder.tvImageNum.setText("共" + getTotalImageSize() + "张");
            if (folderList.size() > 0) {
                config.loader.displayImage(context, folder.cover.path, holder.ivFolder);
            }
        } else {
            holder.tvFolderName.setText(folder.name);
            holder.tvImageNum.setText("共" + folder.images.size() + "张");
            if (folderList.size() > 0) {
                config.loader.displayImage(context, folder.cover.path, holder.ivFolder);
            }
        }

        if (selected == position) {
            holder.ivIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.ivIndicator.setVisibility(View.GONE);
        }

        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectIndex(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (folderList != null) {
            return folderList.size();
        } else {
            return 0;
        }
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int position) {
        if (listener != null) {
            listener.onSelect(position, folderList.get(position));
        }
        if (selected == position) {
            return;
        }
        if (listener != null) {
            listener.onChange(position, folderList.get(position));
        }
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selected;
    }

    public void setOnFolderChangeListener(OnFolderChangeListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFolder;
        private TextView tvFolderName;
        private TextView tvImageNum;
        private ImageView ivIndicator;
        private RelativeLayout rlItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFolder = (ImageView) itemView.findViewById(R.id.ivFolder);
            tvFolderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            tvImageNum = (TextView) itemView.findViewById(R.id.tvImageNum);
            ivIndicator = (ImageView) itemView.findViewById(R.id.ivIndicator);
            rlItem = (RelativeLayout) itemView.findViewById(R.id.rlItem);
        }
    }
}
