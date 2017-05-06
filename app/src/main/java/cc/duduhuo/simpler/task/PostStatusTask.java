package cc.duduhuo.simpler.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.bean.Photo;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.util.BitmapUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/22 0:48
 * 版本：1.0
 * 描述：发布微博的任务类
 * 备注：
 * =======================================================
 */
public class PostStatusTask extends AsyncTask<Void, Void, Integer> {
    private static final int POST_SUCCESS = 0x0000;
    private static final int POST_FAILURE = 0x0001;
    private static final int POST_PIC_NOT_FOUND = 0x0002;
    private static final int POST_PIC_UPLOAD_FAIL = 0x0003;
    private static final int POST_PIC_UPLOADED = 0x0004;

    private Context mContext;
    private String mContent;
    private List<Photo> mPhotoList;
    private String mLatitude;
    private String mLongitude;
    private int mVisible;
    private String mGroupId;
    private OnPostStatusListener mListener;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == POST_PIC_NOT_FOUND) {
                AppToast.showToast("文件 " + mPhotoList.get(msg.arg1).path + " 不存在");
            } else if (msg.what == POST_PIC_UPLOAD_FAIL) {
                AppToast.showToast("图片 " + mPhotoList.get(msg.arg1).path + " 上传失败");
            } else if (msg.what == POST_PIC_UPLOADED) {
                AppToast.showToast("已上传" + msg.arg1 + "张图片");
            }
            return true;
        }
    });
    public PostStatusTask(Context context, String content, List<Photo> photoList, String latitude, String longitude, int visible, String groupId) {
        this.mContext = context;
        this.mContent = content;
        this.mPhotoList = photoList;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mVisible = visible;
        this.mGroupId = groupId;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        List<String> picIds = new ArrayList<>(9);
        StatusesAPI mSApi = new StatusesAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        if (mPhotoList.size() == 0) {
            // 不上传图片
            String msg = mSApi.updateSync(mContent, mVisible, mGroupId, mLatitude, mLongitude);
            if (!TextUtils.isEmpty(msg)) {
                try {
                    JSONObject obj = new JSONObject(msg);
                    if (obj.isNull("error")) {
                        return POST_SUCCESS;
                    } else {
                        return POST_FAILURE;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return POST_FAILURE;
                }
            }
        } else {
            // 上传图片
            int size = mPhotoList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bitmap = BitmapUtils.getUploadImage(mPhotoList.get(i).path, 5);
                if (bitmap == null) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = POST_PIC_NOT_FOUND;
                    msg.arg1 = i;
                    mHandler.sendMessage(msg);
                    continue;
                }
                String msg = mSApi.uploadPicSync(bitmap);
                if (!TextUtils.isEmpty(msg)) {
                    try {
                        JSONObject obj = new JSONObject(msg);
                        String picId = obj.optString("pic_id", "");
                        if (TextUtils.isEmpty(picId)) {
                            Message message = mHandler.obtainMessage();
                            message.what = POST_PIC_UPLOAD_FAIL;
                            message.arg1 = i;
                            mHandler.sendMessage(message);
                        } else {
                            picIds.add(picId);
                            Message message = mHandler.obtainMessage();
                            message.what = POST_PIC_UPLOADED;
                            message.arg1 = picIds.size();
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message message = mHandler.obtainMessage();
                        message.what = POST_PIC_UPLOAD_FAIL;
                        message.arg1 = i;
                        mHandler.sendMessage(message);
                    }
                }
            }

            if (picIds.size() > 0) {
                String picIdStr = "";
                for (int i = 0; i < picIds.size(); i++) {
                    picIdStr += picIds.get(i) + ",";
                }
                picIdStr = picIdStr.substring(0, picIdStr.length() - 1);
                String msg = mSApi.uploadUrlTextSync(mContent, mVisible, mGroupId, null, picIdStr,
                        mLatitude, mLongitude);
                if (!TextUtils.isEmpty(msg)) {
                    try {
                        JSONObject obj = new JSONObject(msg);
                        if (obj.isNull("error")) {
                            return POST_SUCCESS;
                        } else {
                            return POST_FAILURE;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return POST_FAILURE;
                    }
                }
            } else {
                // 没有图片上传成功
                return POST_PIC_UPLOAD_FAIL;
            }
        }
        return POST_FAILURE;
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        switch (status) {
            case POST_SUCCESS:
                if (mListener != null) {
                    mListener.onPostSuccess();
                }
                break;
            case POST_FAILURE:
                if (mListener != null) {
                    mListener.onPostFailure();
                }
                break;
            case POST_PIC_UPLOAD_FAIL:
                if (mListener != null) {
                    mListener.onPicUploadFail();
                }
                break;
            default:
                break;
        }
    }

    public void setOnPostStatusListener(OnPostStatusListener listener) {
        this.mListener = listener;
    }

    /**
     * 微博发布监听接口
     */
    public interface OnPostStatusListener {
        /**
         * 微博发布成功
         */
        void onPostSuccess();

        /**
         * 微博发布失败
         */
        void onPostFailure();

        /**
         * 图片上传失败
         */
        void onPicUploadFail();
    }
}
