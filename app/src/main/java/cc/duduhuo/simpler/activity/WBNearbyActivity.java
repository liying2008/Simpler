package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.PlaceAPI;
import com.sina.weibo.sdk.openapi.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.StatusListAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.util.BDLocationUtil;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class WBNearbyActivity extends BaseActivity {
    private static final String TAG = "WBNearbyActivity";
    private static final int HANDLE_ADDRESS_OK = 0x0000;
    private static final int HANDLE_SERVER_ERROR = 0x0001;
    private static final int HANDLE_NETWORK_EXCEPTION = 0x0002;
    private static final int HANDLE_CRITERIA_EXCEPTION = 0x0003;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private StatusListAdapter mAdapter;

    private int mRefreshCount;
    private int mPage = 1;      // 当前页数
    private PlaceAPI mPApi;
    /** 纬度 */
    private String mLatitude;
    /** 经度 */
    private String mLongitude;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ADDRESS_OK:
                    // 获取微博信息
                    loadStatuses(1, true);
                    mSwipeRefresh.setRefreshing(true);
                    break;
                case HANDLE_SERVER_ERROR:
                    AppToast.showToast(R.string.location_type_server_error);
                    break;
                case HANDLE_NETWORK_EXCEPTION:
                    AppToast.showToast(R.string.location_type_network_exception);
                    break;
                case HANDLE_CRITERIA_EXCEPTION:
                    AppToast.showToast(R.string.location_type_criteria_exception);
                    break;
                default:
                    break;
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_nearby);
        ButterKnife.bind(this);

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.setLocOption(BDLocationUtil.getLocationClientOption());
        mLocationClient.start();

        // 每次刷新微博数
        mRefreshCount = BaseSettings.sSettings.refreshCount;
        mAdapter = new StatusListAdapter(this, null);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新
                if (TextUtils.isEmpty(mLongitude) || TextUtils.isEmpty(mLatitude)) {
                    mSwipeRefresh.setRefreshing(false);
                    return;
                }
                mPage = 1;
                loadStatuses(1, true);
                mSwipeRefresh.setRefreshing(true);
            }
        });
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                if (mPage != 0) {
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    loadStatuses(mPage, false);
                }
            }
        });
    }

    private void loadStatuses(int page, final boolean refresh) {
        if (mPApi == null) {
            mPApi = new PlaceAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mPApi.nearbyTimeline(mLatitude, mLongitude, 2000, 0, 0, 0, mRefreshCount, page, false, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        List<Status> statuses = new ArrayList<>(mRefreshCount);
                        JSONObject obj = new JSONObject(s);
                        JSONArray array = obj.optJSONArray("statuses");
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                Status status = Status.parse(array.getJSONObject(i));
                                statuses.add(status);
                            }
                        }
                        int totalNumber = obj.optInt("total_number", 0);
                        if (refresh) {
                            mAdapter.setStatuses(statuses);
                            mSwipeRefresh.getLayoutManager().scrollToPosition(0);
                        } else {
                            mAdapter.addStatuses(statuses);
                        }
                        int totalPage = (int) Math.ceil((float) totalNumber / mRefreshCount);
                        if (mPage >= totalPage) {
                            // 表示没有更多数据了
                            mPage = 0;
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        } else {
                            // 更新mPage
                            mPage++;
                            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppToast.showToast(R.string.resolve_result_failure);
                    }
                }
                // 停止定位。只获取一次结果
                mLocationClient.stop();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("加载周边动态失败");
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, WBNearbyActivity.class);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 百度地图定位结果回调接口
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // 在回调方法运行在remote进程
            // 获取定位类型
            int locType = location.getLocType();
            if (locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation
                    || locType == BDLocation.TypeOffLineLocation) {
                // 定位成功。GPS定位结果 || 网络定位结果 || 离线定位结果
                // 纬度
                mLatitude = String.valueOf(location.getLatitude());
                // 经度
                mLongitude = String.valueOf(location.getLongitude());
                mHandler.sendEmptyMessage(HANDLE_ADDRESS_OK);
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                mHandler.sendEmptyMessage(HANDLE_SERVER_ERROR);
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                mHandler.sendEmptyMessage(HANDLE_NETWORK_EXCEPTION);
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                mHandler.sendEmptyMessage(HANDLE_CRITERIA_EXCEPTION);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

}
