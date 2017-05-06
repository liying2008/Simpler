package cc.duduhuo.imageselector;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.imageselector.common.Callback;
import cc.duduhuo.imageselector.common.Global;
import cc.duduhuo.imageselector.utils.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 13:39
 * 版本：1.0
 * 描述：图片选择器主界面
 * 备注：
 * =======================================================
 */
public class ImageSelectActivity extends FragmentActivity implements View.OnClickListener, Callback {

    public static final String INTENT_RESULT = "result";
    private static final int IMAGE_CROP_CODE = 1;
    private static final int STORAGE_REQUEST_CODE = 1;

    private ImageSelectConfig config;

    public TextView tvTitle;
    private TextView tvOk;
    private String cropImagePath;

    private ImageSelectFragment fragment;

    private ArrayList<String> result = new ArrayList<>();

    public static void startActivity(Activity activity, ImageSelectConfig config, int RequestCode) {
        Intent intent = new Intent(activity, ImageSelectActivity.class);
        Global.config = config;
        activity.startActivityForResult(intent, RequestCode);
    }

    public static void startActivity(Fragment fragment, ImageSelectConfig config, int RequestCode) {
        Intent intent = new Intent(fragment.getActivity(), ImageSelectActivity.class);
        Global.config = config;
        fragment.startActivityForResult(intent, RequestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        config = Global.config;

        // Android 6.0 checkSelfPermission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        } else {
            fragment = ImageSelectFragment.instance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flImageList, fragment, null)
                    .commit();
        }

        initView();
        if (!FileUtils.isSdCardAvailable()) {
            AppToast.showToast(R.string.sd_disabled);
        }
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOk = (TextView) findViewById(R.id.tvOk);
        tvOk.setOnClickListener(this);

        TextView tvBack = (TextView) findViewById(R.id.tvBack);
        tvBack.setOnClickListener(this);

        if (config != null) {
            if (config.multiSelect) {
                if (!config.rememberSelected) {
                    Global.imageList.clear();
                }
                tvOk.setText(String.format(getString(R.string.confirm_format), Global.imageList.size(), config.maxNum));
            } else {
                Global.imageList.clear();
                tvOk.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvOk) {
            if (Global.imageList != null && !Global.imageList.isEmpty()) {
                exit();
            } else {
                AppToast.showToast(R.string.min_num);
            }
        } else if (id == R.id.tvBack) {
            onBackPressed();
        }
    }

    @Override
    public void onSingleImageSelected(String path) {
        if (config.needCrop) {
            crop(path);
        } else {
            Global.imageList.add(path);
            exit();
        }
    }

    @Override
    public void onImageSelected(String path) {
        tvOk.setText(String.format(getString(R.string.confirm_format), Global.imageList.size(), config.maxNum));
    }

    @Override
    public void onImageUnselected(String path) {
        tvOk.setText(String.format(getString(R.string.confirm_format), Global.imageList.size(), config.maxNum));
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            if (config.needCrop) {
                crop(imageFile.getAbsolutePath());
            } else {
                Global.imageList.add(imageFile.getAbsolutePath());
                config.multiSelect = false; // 多选点击拍照，强制更改为单选
                exit();
            }
        }
    }

    @Override
    public void onPreviewChanged(int select, int sum, boolean visible) {
        if (visible) {
            tvTitle.setText(select + "/" + sum);
        } else {
            tvTitle.setText(Global.curFolderName);
        }
    }

    private void crop(String imagePath) {
        File file = new File(FileUtils.getRootPath(this) + File.separator + System.currentTimeMillis() + ".jpg");

        cropImagePath = file.getAbsolutePath();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(imagePath), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", config.aspectX);
        intent.putExtra("aspectY", config.aspectY);
        intent.putExtra("outputX", config.outputX);
        intent.putExtra("outputY", config.outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, IMAGE_CROP_CODE);
    }

    public Uri getImageContentUri(String filePath) {
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (new File(filePath).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            Global.imageList.add(cropImagePath);
            config.multiSelect = false; // 多选点击拍照，强制更改为单选
            exit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void exit() {
        Intent intent = new Intent();
        result.clear();
        result.addAll(Global.imageList);
        intent.putStringArrayListExtra(INTENT_RESULT, result);
        setResult(RESULT_OK, intent);

        if (!config.multiSelect) {
            Global.imageList.clear();
        }

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.flImageList, ImageSelectFragment.instance(), null)
                            .commitAllowingStateLoss();
                } else {
                    AppToast.showToast(R.string.permission_storage_denied);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment == null || !fragment.hidePreview()) {
            Global.imageList.clear();
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.config = null;
    }
}
