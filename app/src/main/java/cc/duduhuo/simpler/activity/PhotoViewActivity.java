package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.fragment.PictureFragment;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.view.PicViewPager;

public class PhotoViewActivity extends BaseActivity {
    private static final String TAG = "PhotoViewActivity";
    @BindView(R.id.picViewPager)
    PicViewPager mPicViewPager;
    @BindView(R.id.tvPicNum)
    TextView mTvPicNum;
    /** 图片总数 */
    private int mPicNum;
    /** 当前是第几张图片 */
    private int mCurPicNum;
    /** 所有图片的URL */
    private ArrayList<String> mPicUrls;

    private SamplePagerAdapter mSamplePagerAdapter;
    private List<PictureFragment> mFragmentList = new ArrayList<>();

    /** ViewPager滑动事件监听 */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // 设置页码
            mTvPicNum.setText((position + 1) + "/" + mPicNum);
            mCurPicNum = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mPicNum = intent.getIntExtra("picNum", 0);
        mCurPicNum = intent.getIntExtra("curPicNum", 0);
        mPicUrls = intent.getStringArrayListExtra("picUrls");

        for (int i = 0; i < mPicUrls.size(); i++) {
            PictureFragment fragment = PictureFragment.newInstance(mPicUrls.get(i));
            mFragmentList.add(fragment);
        }

        mSamplePagerAdapter = new SamplePagerAdapter(getSupportFragmentManager(), mFragmentList);
        mPicViewPager.setAdapter(mSamplePagerAdapter);
        // 注意：setCurrentItem()一定要在setAdapter()之后调用
        mPicViewPager.setCurrentItem(mCurPicNum);
        mPicViewPager.setOnPageChangeListener(pageChangeListener);
    }

    public static Intent newIntent(Context context, int picNum, int curPicNum, ArrayList<String> picUrls) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra("picNum", picNum);
        intent.putExtra("curPicNum", curPicNum);
        intent.putStringArrayListExtra("picUrls", picUrls);
        return intent;
    }

    public static Intent newIntent(Context context, String picUrl) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra("picNum", 1);
        intent.putExtra("curPicNum", 0);
        ArrayList<String> list = new ArrayList<>(1);
        list.add(picUrl);
        intent.putStringArrayListExtra("picUrls", list);
        return intent;
    }

    @OnClick(R.id.ivSave)
    void savePic() {
        String url = mPicUrls.get(mCurPicNum);
        String path = Constants.Dir.PIC_DIR + File.separator + CommonUtils.getFileNameFromUrl(url);
        BaseDownloadTask downloadTask = createDownloadTask(mPicUrls.get(mCurPicNum), path, null, false);
        downloadTask.start();
    }

    @OnClick(R.id.ivShare)
    void sharePic() {
        String url = mPicUrls.get(mCurPicNum);
        String path = Constants.Dir.PIC_DIR + File.separator + CommonUtils.getFileNameFromUrl(url);
        BaseDownloadTask downloadTask = createDownloadTask(mPicUrls.get(mCurPicNum), path, null, true);
        downloadTask.start();
    }

    private class SamplePagerAdapter extends FragmentPagerAdapter {
        private List<PictureFragment> fragments;
        private FragmentManager fm;

        public SamplePagerAdapter(FragmentManager fm, List<PictureFragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void setFragments(ArrayList<PictureFragment> fragments) {
            if (this.fragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.fragments = fragments;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置页码
        mTvPicNum.setText((mCurPicNum + 1) + "/" + mPicNum);
    }
}
