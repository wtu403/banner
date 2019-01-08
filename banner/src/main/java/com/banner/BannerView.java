package com.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerView extends RelativeLayout implements ViewPager.OnPageChangeListener ,NotifyChanged{
    private final String tag = "BannerView";
    //改变ViewPager切换的速率
    private BannerScroller mScroller;
    private BannerViewPager viewPager;
    private int scrollTime = 850;
    //视图切换时间
    private int delayTime = 5000;
    private boolean isAutoPlay = true;
    private int currentItem = 0;
    private WeakHandler handler = new WeakHandler();
    private Activity mActivity;

    private LinearLayout circleIndicator;

    private TextView bannerTitle;
    private int titleHeight;
    private int titleBackground;
    private int titleTextColor;
    private int titleTextSize;


    private List<String> titleList;
    private int count = 0;


    public BannerView(Context context) {
        this(context,null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }
    private void initView(Context context,AttributeSet attrs){
        mActivity = (Activity) context;
        View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        viewPager = view.findViewById(R.id.banner_vp);
        circleIndicator = view.findViewById(R.id.circleIndicator);
        bannerTitle = view.findViewById(R.id.bannerTitle);
        initTypedArray(context,attrs);
        initViewPagerScroll();
        setTitleStyleUI();
        if (isAutoPlay){
            startAutoPlay();
        }
        mActivity.getApplication().registerActivityLifecycleCallbacks(activityCallbacks);
    }
    private void initTypedArray(Context context, AttributeSet attrs){
        if (attrs == null){
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        scrollTime = typedArray.getInt(R.styleable.Banner_scroll_time, 800);
        delayTime = typedArray.getInt(R.styleable.Banner_delay_time, 2000);
        isAutoPlay = typedArray.getBoolean(R.styleable.Banner_is_auto_play, true);

        titleBackground = typedArray.getColor(R.styleable.Banner_title_background, -1);
        titleHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_title_height, -1);
        titleTextColor = typedArray.getColor(R.styleable.Banner_title_textcolor, -1);
        titleTextSize = typedArray.getDimensionPixelSize(R.styleable.Banner_title_textsize,-1);
        typedArray.recycle();
    }

    //设置滚动时间
    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerScroller(viewPager.getContext());
            mScroller.setDuration(scrollTime);
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }
    }

    private void setTitleStyleUI() {
        if (titleBackground != -1) {
            bannerTitle.setBackgroundColor(titleBackground);
        }
        if (titleHeight != -1) {
            bannerTitle.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
        }
        if (titleTextColor != -1) {
            bannerTitle.setTextColor(titleTextColor);
        }
        if (titleTextSize != -1) {
            bannerTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        }
    }

    public void setAdapter(BannerAdapter adapter){
        adapter.setNotifyChanged(this);
        viewPager.setAdapter(adapter,isAutoPlay);
        count = adapter.getCount();
        titleList = adapter.getTitles();
        createIndicatorView();
        viewPager.addOnPageChangeListener(this);
    }
    public BannerView setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    public BannerView setOffscreenPageLimit(int limit) {
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    public void startAutoPlay() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, delayTime);
    }

    public void stopAutoPlay() {
        handler.removeCallbacks(task);
    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1 && isAutoPlay) {
                viewPager.setCurrentItem(currentItem+1);
                handler.postDelayed(task, delayTime);

            }
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        setTitle(position%count);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //参数arg0有三种取值：0：什么都没做 1：开始滑动 2：滑动结束
    }

    /**
     * 设置title
     * @param position
     */
    private void setTitle(int position){
        if (titleList != null){
            if(titleList.size() > position){
                bannerTitle.setVisibility(VISIBLE);
                bannerTitle.setText(titleList.get(position));
            }
        }else {
            bannerTitle.setVisibility(GONE);
        }
    }
    private void releaseBanner() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    //处理滑动与自动播放导致冲突
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        releaseBanner();
        mActivity.getApplication().unregisterActivityLifecycleCallbacks(activityCallbacks);
        super.onDetachedFromWindow();
    }

    Application.ActivityLifecycleCallbacks activityCallbacks = new BannerActivityLifecycleCallbacks() {
        @Override
        public void onActivityResumed(Activity activity) {
            if (isAutoPlay){
                startAutoPlay();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (isAutoPlay){
                stopAutoPlay();
            }
        }
    };

    @Override
    public void notifyDataSetChanged(List<String> title,int count) {
        this.titleList = title;
        this.count = count;
        createIndicatorView();
        // TODO 是否需要切换，BUG没有自动播放
        viewPager.getAdapter().notifyDataSetChanged();
    }

    //创建指示器
    private void createIndicatorView(){
        int size = circleIndicator.getChildCount();
        if (size < count){
            addIndicatorView(count - size);
        }else if (size > count){
          int num = size - count;
           for (int i = 0;i < num;i++){
               circleIndicator.removeViewAt(i);
           }
        }
    }

    private void addIndicatorView(int size){
        for (int i = 0; i < size; i++){
            IndicatorView view = new IndicatorView(mActivity);
            circleIndicator.addView(view);
            LinearLayout.LayoutParams params = new
                    LinearLayout.LayoutParams(30,30);
            params.leftMargin = params.rightMargin = 5;
            view.setLayoutParams(params);
        }
    }
}
