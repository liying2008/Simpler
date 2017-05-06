package cc.duduhuo.weibores.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.weibores.R;
import cc.duduhuo.weibores.entities.Emoticon;
import cc.duduhuo.weibores.manager.RecentEmoticonManager;
import cc.duduhuo.weibores.utils.EmoticonUtils;
import cc.duduhuo.weibores.view.EmoticonIndicator;


/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/teambition/yykEmoji
 * 日期：2017/4/10 22:10
 * 版本：1.0
 * 描述：微博表情面板Fragment
 * 备注：
 * =======================================================
 */
public class EmoticonPanelFragment extends Fragment implements View.OnClickListener {

    public static EmoticonPanelFragment Instance() {
        EmoticonPanelFragment instance = new EmoticonPanelFragment();
        Bundle bundle = new Bundle();
        instance.setArguments(bundle);
        return instance;
    }

    ViewPager viewPager;
    EmoticonIndicator indicator;
    TextView tvRecent;
    TextView tvAll;

    ArrayList<View> ViewPagerItems = new ArrayList<>();
    ArrayList<Emoticon> emoticonList;
    ArrayList<Emoticon> recentlyEmoticonList;
    private int columns = 7;
    private int rows = 4;

    private OnEmoticonClickListener listener;
    private RecentEmoticonManager recentManager;

    public void setListener(OnEmoticonClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnEmoticonClickListener) {
            this.listener = (OnEmoticonClickListener) activity;
        }
        recentManager = RecentEmoticonManager.make(activity);
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        emoticonList = EmoticonUtils.getEmoticonList();
        try {
            if (recentManager.getCollection(RecentEmoticonManager.PREFERENCE_NAME) != null) {
                recentlyEmoticonList = (ArrayList<Emoticon>) recentManager.getCollection(RecentEmoticonManager.PREFERENCE_NAME);
            } else {
                recentlyEmoticonList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emoticon, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        indicator = (EmoticonIndicator) view.findViewById(R.id.indicator);
        tvRecent = (TextView) view.findViewById(R.id.tvRecent);
        tvAll = (TextView) view.findViewById(R.id.tvAll);
        initViews();
        return view;
    }

    private void initViews() {
        initViewPager(emoticonList);
        tvAll.setSelected(true);
        tvAll.setOnClickListener(this);
        tvRecent.setOnClickListener(this);
    }

    private void initViewPager(ArrayList<Emoticon> list) {
        intiIndicator(list);
        ViewPagerItems.clear();
        for (int i = 0; i < getPagerCount(list); i++) {
            ViewPagerItems.add(getViewPagerItem(i, list));
        }
        ViewPagerAdapter mVpAdapter = new ViewPagerAdapter(ViewPagerItems);
        viewPager.setAdapter(mVpAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator.playBy(oldPosition, position);
                oldPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void intiIndicator(ArrayList<Emoticon> list) {
        indicator.init(getPagerCount(list));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvAll) {
            if (indicator.getVisibility() == View.GONE) {
                indicator.setVisibility(View.VISIBLE);
            }
            if (!tvAll.isSelected()) {
                tvAll.setSelected(true);
                initViewPager(emoticonList);
            }
            tvRecent.setSelected(false);
        } else if (v.getId() == R.id.tvRecent) {
            if (indicator.getVisibility() == View.VISIBLE) {
                indicator.setVisibility(View.GONE);
            }
            if (!tvRecent.isSelected()) {
                tvRecent.setSelected(true);
                initViewPager(recentlyEmoticonList);
            }
            tvAll.setSelected(false);
        }

    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount(ArrayList<Emoticon> list) {
        int count = list.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private View getViewPagerItem(int position, ArrayList<Emoticon> list) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_emoticon_grid, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.gvEmoticon);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         */
        final List<Emoticon> subList = new ArrayList<>();
        subList.addAll(list.subList(position * (columns * rows - 1),
                (columns * rows - 1) * (position + 1) > list.size() ? list.size() : (columns * rows - 1) * (position + 1)));
        /**
         * 末尾添加删除图标
         */
        if (subList.size() < (columns * rows - 1)) {
            for (int i = subList.size(); i < (columns * rows - 1); i++) {
                subList.add(null);
            }
        }
        Emoticon deleteEmoticon = new Emoticon();
        deleteEmoticon.setImageUri(R.drawable.emoticon_delete);
        subList.add(deleteEmoticon);
        GridViewAdapter mGvAdapter = new GridViewAdapter(subList, getActivity());
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == columns * rows - 1) {
                    if (listener != null) {
                        listener.onEmoticonDelete();
                    }
                    return;
                }
                if (listener != null) {
                    listener.onEmoticonClick(subList.get(position));
                }
                insertToRecentList(subList.get(position));
            }
        });

        return gridview;
    }

    private void insertToRecentList(Emoticon emoticon) {
        if (emoticon != null) {
            if (recentlyEmoticonList.contains(emoticon)) {
                //如果已经有该表情，就把该表情放到第一个位置
                int index = recentlyEmoticonList.indexOf(emoticon);
                Emoticon emoticon0 = recentlyEmoticonList.get(0);
                recentlyEmoticonList.set(index, emoticon0);
                recentlyEmoticonList.set(0, emoticon);
                return;
            }
            if (recentlyEmoticonList.size() == (rows * columns - 1)) {
                //去掉最后一个
                recentlyEmoticonList.remove(rows * columns - 2);
            }
            recentlyEmoticonList.add(0, emoticon);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            recentManager.putCollection(RecentEmoticonManager.PREFERENCE_NAME, recentlyEmoticonList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class GridViewAdapter extends BaseAdapter {
        private List<Emoticon> list;
        private Context mContext;

        public GridViewAdapter(List<Emoticon> list, Context mContext) {
            super();
            this.list = list;
            this.mContext = mContext;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_emoticon, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.ivEmoticon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (list.get(position) != null) {
                holder.iv.setImageBitmap(EmoticonUtils.decodeSampledBitmapFromResource(getActivity().getResources(),
                        list.get(position).getImageUri(),
                        EmoticonUtils.dip2px(getActivity(), 32), EmoticonUtils.dip2px(getActivity(), 32)));
            }
            return convertView;
        }

        class ViewHolder {
            ImageView iv;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {
        // 界面列表
        private List<View> views;

        public ViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) (object));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        // 初始化position位置的界面
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        // 判断View是否关联到一个对象上
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

    /**
     * 表情点击监听
     */
    public interface OnEmoticonClickListener {
        /**
         * 删除
         */
        void onEmoticonDelete();

        /**
         * 输入表情
         *
         * @param emoticon
         */
        void onEmoticonClick(Emoticon emoticon);
    }
}
