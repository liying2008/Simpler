package cc.duduhuo.imageselector.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cc.duduhuo.imageselector.R;
import cc.duduhuo.imageselector.adapter.FolderListAdapter;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 15:46
 * 版本：1.0
 * 描述：图片文件夹选择Dialog
 * 备注：
 * =======================================================
 */
public class ImageFolderDialog extends AlertDialog {
    private Context mContext;
    private TextView tvCancel;
    private RecyclerView rvFolderList;
    private FolderListAdapter mAdapter;

    public ImageFolderDialog(@NonNull Context context, int index, FolderListAdapter adapter) {
        super(context);
        this.mContext = context;
        this.mAdapter = adapter;
        mAdapter.setSelectIndex(index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_folder);

        Window window = getWindow();
        // 在5.0以上手机必须加上这句代码
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.BottomDialogStyle);  //添加动画

        tvCancel = (TextView) findViewById(R.id.tvCancel);
        rvFolderList = (RecyclerView) findViewById(R.id.rvFolderList);
        rvFolderList.setLayoutManager(new LinearLayoutManager(mContext));
        rvFolderList.setAdapter(mAdapter);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
