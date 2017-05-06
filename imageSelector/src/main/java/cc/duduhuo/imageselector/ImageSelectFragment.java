package cc.duduhuo.imageselector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.imageselector.adapter.FolderListAdapter;
import cc.duduhuo.imageselector.adapter.ImageListAdapter;
import cc.duduhuo.imageselector.adapter.PreviewAdapter;
import cc.duduhuo.imageselector.bean.Folder;
import cc.duduhuo.imageselector.bean.Image;
import cc.duduhuo.imageselector.common.Callback;
import cc.duduhuo.imageselector.common.Global;
import cc.duduhuo.imageselector.common.OnFolderChangeListener;
import cc.duduhuo.imageselector.common.OnItemClickListener;
import cc.duduhuo.imageselector.dialog.ImageFolderDialog;
import cc.duduhuo.imageselector.utils.FileUtils;
import cc.duduhuo.imageselector.widget.GridItemDividerDecoration;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 20:00
 * 版本：1.0
 * 描述：图片选择器Fragment
 * 备注：
 * =======================================================
 */
public class ImageSelectFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rvImageList;
    private TextView tvAlbumSelected;
    private ViewPager viewPager;

    private ImageSelectConfig config;
    private Callback callback;
    private List<Folder> folderList = new ArrayList<>();
    private List<Image> imageList = new ArrayList<>();

    private ImageListAdapter imageListAdapter;
    private FolderListAdapter folderListAdapter;
    private PreviewAdapter previewAdapter;

    private boolean hasFolderGened = false;

    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private static final int REQUEST_CAMERA = 5;

    private static final int CAMERA_REQUEST_CODE = 1;

    private File tempFile;

    public static ImageSelectFragment instance() {
        ImageSelectFragment fragment = new ImageSelectFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_select, container, false);
        rvImageList = (RecyclerView) view.findViewById(R.id.rvImageList);
        tvAlbumSelected = (TextView) view.findViewById(R.id.tvAlbumSelected);
        tvAlbumSelected.setOnClickListener(this);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (config.needCamera) {
                    callback.onPreviewChanged(position + 1, imageList.size() - 1, true);
                } else {
                    callback.onPreviewChanged(position + 1, imageList.size(), true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        config = Global.config;
        try {
            callback = (Callback) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvAlbumSelected.setText(R.string.all_images);
        Global.curFolderName = getActivity().getString(R.string.all_images);

        rvImageList.setLayoutManager(new GridLayoutManager(rvImageList.getContext(), 3));
        rvImageList.addItemDecoration(new GridItemDividerDecoration(rvImageList.getContext()));
        if (config.needCamera) {
            imageList.add(new Image());
        }
        imageListAdapter = new ImageListAdapter(getActivity(), imageList, config);
        imageListAdapter.setShowCamera(config.needCamera);
        imageListAdapter.setMultiSelect(config.multiSelect);
        rvImageList.setAdapter(imageListAdapter);
        imageListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public int onCheckedClick(int position, Image image) {
                return checkedImage(image);
            }

            @Override
            public void onImageClick(int position, Image image) {
                if (config.needCamera && position == 0) {
                    showCameraAction();
                } else {
                    if (config.multiSelect) {
                        viewPager.setAdapter((previewAdapter = new PreviewAdapter(getActivity(), imageList, config)));
                        previewAdapter.setListener(new OnItemClickListener() {
                            @Override
                            public int onCheckedClick(int position, Image image) {
                                return checkedImage(image);
                            }

                            @Override
                            public void onImageClick(int position, Image image) {
                                hidePreview();
                            }
                        });
                        if (config.needCamera) {
                            callback.onPreviewChanged(position, imageList.size() - 1, true);
                        } else {
                            callback.onPreviewChanged(position + 1, imageList.size(), true);
                        }
                        viewPager.setCurrentItem(config.needCamera ? position - 1 : position);
                        viewPager.setVisibility(View.VISIBLE);
                    } else {
                        if (callback != null) {
                            callback.onSingleImageSelected(image.path);
                        }
                    }
                }
            }
        });

        folderListAdapter = new FolderListAdapter(getActivity(), folderList, config);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    private int checkedImage(Image image) {
        if (image != null) {
            if (Global.imageList.contains(image.path)) {
                Global.imageList.remove(image.path);
                if (callback != null) {
                    callback.onImageUnselected(image.path);
                }
            } else {
                if (Global.imageList.size() >= config.maxNum) {
                    AppToast.showToast(String.format(getString(R.string.max_num), config.maxNum));
                    return 0;
                }

                Global.imageList.add(image.path);
                if (callback != null) {
                    callback.onImageSelected(image.path);
                }
            }
            return 1;
        }
        return 0;
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<Image> tempImageList = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        if (!image.path.endsWith(".gif"))
                            tempImageList.add(image);
                        if (!hasFolderGened) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            if (folderFile == null) {
                                Log.d("Image", path);
                                return;
                            }
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!folderList.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                folderList.add(folder);
                            } else {
                                Folder f = folderList.get(folderList.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (data.moveToNext());

                    imageList.clear();
                    if (config.needCamera) {
                        imageList.add(new Image());
                    }
                    imageList.addAll(tempImageList);

                    imageListAdapter.notifyDataSetChanged();

                    if (Global.imageList != null && Global.imageList.size() > 0) {
                        //imageListAdapter.setDefaultSelected(Global.imageList);
                    }

                    folderListAdapter.notifyDataSetChanged();
                    hasFolderGened = true;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void showFolderList(int index) {
        final ImageFolderDialog dialog = new ImageFolderDialog(getActivity(), index, folderListAdapter);
        dialog.show();

        folderListAdapter.setOnFolderChangeListener(new OnFolderChangeListener() {
            @Override
            public void onChange(int position, Folder folder) {
                if (position == 0) {
                    getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                    tvAlbumSelected.setText(R.string.all_images);
                    Global.curFolderName = getActivity().getString(R.string.all_images);
                    ((ImageSelectActivity) getActivity()).tvTitle.setText(R.string.all_images);
                } else {
                    imageList.clear();
                    if (config.needCamera) {
                        imageList.add(new Image());
                    }
                    imageList.addAll(folder.images);
                    imageListAdapter.notifyDataSetChanged();
                    tvAlbumSelected.setText(folder.name);
                    Global.curFolderName = folder.name;
                    ((ImageSelectActivity) getActivity()).tvTitle.setText(folder.name);
                }
            }

            @Override
            public void onSelect(int position, Folder folder) {
                dialog.dismiss();   // 关闭Dialog
            }
        });
    }

    @Override
    public void onClick(View v) {
        int index = folderListAdapter.getSelectIndex();
        showFolderList(index);
    }

    private void showCameraAction() {
        if (config.maxNum <= Global.imageList.size()) {
            AppToast.showToast(String.format(getString(R.string.max_num), config.maxNum));
            return;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            tempFile = new File(FileUtils.getRootPath(getActivity()) + File.separator
                    + getActivity().getString(R.string.app_name) + "_" + System.currentTimeMillis() + ".jpg");
            // 创建临时照片文件
            FileUtils.createFile(tempFile);

            Uri uri = FileProvider.getUriForFile(getActivity(), FileUtils.getApplicationId(getActivity()) + ".provider", tempFile);

            List<ResolveInfo> resInfoList = getActivity().getPackageManager()
                    .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            AppToast.showToast(R.string.open_camera_failure);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tempFile != null) {
                    if (callback != null) {
                        callback.onCameraShot(tempFile);
                    }
                }
            } else {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCameraAction();
                } else {
                    AppToast.showToast(R.string.permission_camera_denied);
                }
                break;
            default:
                break;
        }
    }

    public boolean hidePreview() {
        if (viewPager.getVisibility() == View.VISIBLE) {
            viewPager.setVisibility(View.GONE);
            callback.onPreviewChanged(0, 0, false);
            imageListAdapter.notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }
}
