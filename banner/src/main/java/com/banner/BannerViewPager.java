package com.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BannerViewPager extends ViewPager {
    private BannerAdapter mAdapter;
    private boolean isAutoPlay = true;
    private List<View> listViews;

    public BannerViewPager(@NonNull Context context) {
        super(context);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(BannerAdapter adapter,boolean isAutoPlay){
        this.mAdapter = adapter;
        this.isAutoPlay = isAutoPlay;
        listViews = new ArrayList<>();
        setAdapter(new BannerPagerAdapter());
    }
    private class BannerPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            if (mAdapter.getCount() > 1){
                return isAutoPlay ? Integer.MAX_VALUE:mAdapter.getCount();
            }else {
                return mAdapter.getCount();
            }
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View convertView = getConvertView();
            View bannerView = mAdapter.getView(position%mAdapter.getCount(),convertView);
            container.addView(bannerView );
            return bannerView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // 销毁回调的方法  移除页面即可
            container.removeView((View) object);
            listViews.add((View) object);
        }

        private View getConvertView(){
            View convertView = null;
            int size = listViews.size();
            for (int i = 0;i<size;i++){
                if (listViews.get(i).getParent() == null){
                    convertView = listViews.get(i);
                    listViews.remove(i);
                    break;
                }
            }
            return convertView;
        }
    }
}
