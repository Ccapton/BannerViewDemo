package com.capton.bannerview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by CAPTON on 2017/1/15.
 */

public class BannerView extends RelativeLayout implements ViewPager.OnPageChangeListener{
      private Context context;
      public static final String INDICATOR_LOCATION_LEFT="left";
      public static final String INDICATOR_LOCATION_CENTER="center";
      public static final String INDICATOR_LOCATION_RIGHT="right";
      public static final int DOT_SMALL=6;
      public static final int DOT_NORMAL=8;
      public static final int DOT_LARGE=12;
      private String indicatorLocation=INDICATOR_LOCATION_CENTER;
      private int dotSize=DOT_NORMAL;
      private int interval=3000;
      private ViewPager mViewPager;
      private Indicator mIndicator;
      private int dotColor= Color.WHITE;
      private  Handler handler;
    public BannerView(Context context) {this(context,null);}
    public BannerView(Context context, AttributeSet attrs) {this(context, attrs,0);}
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        handler=new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==mViewPager.getAdapter().getCount()-1){
                     mViewPager.setCurrentItem(mViewPager.getAdapter().getCount()/2,false);
                }else {
                    mViewPager.setCurrentItem(msg.what + 1,smooth);
                }
            }
        };
        obtainAttrs(attrs);
        View view=LayoutInflater.from(context).inflate(R.layout.viewpager,null);
         mViewPager= (ViewPager) view.findViewById(R.id.viewpager);
    }

    private void obtainAttrs(AttributeSet attrs){
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.BannerView);
        if(ta.getString(R.styleable.BannerView_incator_location)!=null) {
            if(ta.getString(R.styleable.BannerView_incator_location).equals(INDICATOR_LOCATION_LEFT)
            ||ta.getString(R.styleable.BannerView_incator_location).equals(INDICATOR_LOCATION_CENTER)
            ||ta.getString(R.styleable.BannerView_incator_location).equals(INDICATOR_LOCATION_RIGHT)) {
                indicatorLocation = ta.getString(R.styleable.BannerView_incator_location);
            }else {
                indicatorLocation=INDICATOR_LOCATION_CENTER;
            }
        }
        dotSize=ta.getDimensionPixelSize(R.styleable.BannerView_dot_size,DisplayUtil.dip2px(context,dotSize));
        dotColor=ta.getColor(R.styleable.BannerView_dot_color,dotColor);
        interval=ta.getInt(R.styleable.BannerView_interval,interval);
        ta.recycle();
    }

    boolean once;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        if(!once) {
            LayoutParams lp = new LayoutParams(widthSize, heightSize);
            lp.width=widthSize;
            lp.height=heightSize;
            mViewPager.setLayoutParams(lp);
            addView(mViewPager);
            addView(mIndicator);
            setMeasuredDimension(widthSize, heightSize);
          once=true;
        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setIndicatorLayout();
    }

    private boolean smooth=true;
    public BannerView smoothPlay(boolean smooth){
        this.smooth=smooth;
        return this;
    }

    private void setIndicatorLayout(){
        int indicatorWidth=mIndicator.getWidth();
        int indicatorHeight=mIndicator.getHeight();
        int left,top,right,bottom;
        switch (indicatorLocation){
            case INDICATOR_LOCATION_CENTER:
                left=(getMeasuredWidth()-indicatorWidth)/2;
                top=getMeasuredHeight()-indicatorHeight*2;
                right= (getMeasuredWidth()+indicatorWidth)/2;
                bottom=getMeasuredHeight()-indicatorHeight;
                mIndicator.layout(left,top,right,bottom);
                break;
            case INDICATOR_LOCATION_LEFT:
                left=indicatorWidth/2;
                top=getMeasuredHeight()-indicatorHeight*2;
                right=indicatorWidth*3/2;
                bottom=getMeasuredHeight()-indicatorHeight;
                mIndicator.layout(left,top,right,bottom);
                break;
            case INDICATOR_LOCATION_RIGHT:
                left=getMeasuredWidth()-indicatorWidth*3/2;
                top=getMeasuredHeight()-indicatorHeight*2;
                right= getMeasuredWidth()-indicatorWidth/2;
                bottom=getMeasuredHeight()-indicatorHeight;
                mIndicator.layout(left,top,right,bottom);
                break;
        }
    }

    public void loadUrl(final ArrayList<String> urlList){
        final ArrayList<View> viewlist=new ArrayList<>();
        for (int i = 0; i <urlList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(urlList.get(i)).centerCrop().into(imageView);
            viewlist.add(imageView);
        }
        for (int i = 0; i <urlList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(urlList.get(i)).centerCrop().into(imageView);
            viewlist.add(imageView);
        }
        mViewPager.setTag(viewlist.size()/2);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.removeView(viewlist.get(position%viewlist.size()));
                container.addView(viewlist.get(position%viewlist.size()),0);
                return viewlist.get(position%viewlist.size());
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(viewlist.get(position%viewlist.size()));
            }

        });
        mViewPager.setCurrentItem(viewlist.size()*3000);
        mViewPager.setOnPageChangeListener(this);
        mIndicator=new Indicator(context,urlList.size(),dotColor);
        mIndicator.setDotSize(DisplayUtil.px2dip(context,dotSize));
        setIndicatorLocation(indicatorLocation);
        new AutoPlayThread(interval).start();
    }
    public void loadFile(Activity activity, ArrayList<File> fileList){
        final ArrayList<View> viewlist=new ArrayList<>();
        for (int i = 0; i <fileList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(fileList.get(i)).into(imageView);
            viewlist.add(imageView);
        }
        for (int i = 0; i <fileList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(fileList.get(i)).into(imageView);
            viewlist.add(imageView);
        }
        mViewPager.setTag(viewlist.size()/2);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.removeView(viewlist.get(position%viewlist.size()));
                container.addView(viewlist.get(position%viewlist.size()),0);
                return viewlist.get(position%viewlist.size());
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(viewlist.get(position%viewlist.size()));
            }

        });
        mViewPager.setCurrentItem(viewlist.size()*3000);
        mViewPager.setOnPageChangeListener(this);
        mIndicator=new Indicator(context,mViewPager.getAdapter().getCount(),dotColor);
        mIndicator.setDotSize(DisplayUtil.px2dip(context,dotSize));
        setIndicatorLocation(indicatorLocation);
        new AutoPlayThread(interval).start();
    }
    public void loadUri(Activity activity, ArrayList<Uri> uriList){
        final ArrayList<View> viewlist=new ArrayList<>();
        for (int i = 0; i <uriList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(uriList.get(i)).into(imageView);
            viewlist.add(imageView);
        }
        for (int i = 0; i <uriList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(uriList.get(i)).into(imageView);
            viewlist.add(imageView);
        }
        mViewPager.setTag(viewlist.size()/2);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.removeView(viewlist.get(position%viewlist.size()));
                container.addView(viewlist.get(position%viewlist.size()),0);
                return viewlist.get(position%viewlist.size());
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(viewlist.get(position%viewlist.size()));
            }

        });
        mViewPager.setCurrentItem(viewlist.size()*3000);
        mViewPager.setOnPageChangeListener(this);
        mIndicator=new Indicator(context,mViewPager.getAdapter().getCount(),dotColor);
        mIndicator.setDotSize(DisplayUtil.px2dip(context,dotSize));
        setIndicatorLocation(indicatorLocation);
        new AutoPlayThread(interval).start();
    }
    public void loadSourceId(Activity activity, ArrayList<Integer> idList){
        final ArrayList<View> viewlist=new ArrayList<>();
        for (int i = 0; i <idList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(idList.get(i)).into(imageView);
            viewlist.add(imageView);
        }
        for (int i = 0; i <idList.size(); i++) {
            ImageView imageView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image,null);
            Glide.with(context).load(idList.get(i)).into(imageView);
            viewlist.add(imageView);
        }
        mViewPager.setTag(viewlist.size()/2);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.removeView(viewlist.get(position%viewlist.size()));
                container.addView(viewlist.get(position%viewlist.size()),0);
                return viewlist.get(position%viewlist.size());
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(viewlist.get(position%viewlist.size()));
            }

        });
        mViewPager.setCurrentItem(viewlist.size()*3000);
        mViewPager.setOnPageChangeListener(this);
        mIndicator=new Indicator(context,mViewPager.getAdapter().getCount(),dotColor);
        mIndicator.setDotSize(DisplayUtil.px2dip(context,dotSize));
        setIndicatorLocation(indicatorLocation);
        new AutoPlayThread(interval).start();
    }

    boolean isAutoPlaying=true;
    public void setAutoPlay(boolean play){
        if(play){
            this.play=true;
            isAutoPlaying=true;
        }
        else {
            this.play=false;
            isAutoPlaying=false;
        }
    }

    public BannerView setIndicatorLocation(String location){
        if(location!=null) {
            if (location.equals(INDICATOR_LOCATION_LEFT)
                    || location.equals(INDICATOR_LOCATION_CENTER)
                    || location.equals(INDICATOR_LOCATION_RIGHT)) {
                indicatorLocation = location;
            } else {
                indicatorLocation = INDICATOR_LOCATION_CENTER;
                Log.e("setIndicatorLocation", " Wrong setting!Please input a correct indicatorLocation:'AutoPlayViewpager." +
                        "INDICATOR_LOCATION_LEFT','AutoPlayViewpager.INDICATOR_LOCATION_CENTER'or'AutoPlayViewpager.INDICATOR_LOCATION_RIGHT'");
            }
            setIndicatorLayout();
        }else {
            Log.e("setIndicatorLocation", " Wrong setting!Please input a correct indicatorLocation:'AutoPlayViewpager." +
                    "INDICATOR_LOCATION_LEFT','AutoPlayViewpager.INDICATOR_LOCATION_CENTER'or'AutoPlayViewpager.INDICATOR_LOCATION_RIGHT'");
        }
        return this;
    }

    public BannerView setDotSize(int dotSize) {
        mIndicator.setDotSize(DisplayUtil.px2dip(context,dotSize));
        return this;
    }

    public BannerView setDotColor(int color) {
        mIndicator.setDotColor(color);
        return this;
    }
    public BannerView setInterval(int interval){
        this.interval=interval;
        return this;
    }
    @Override
    public void onPageSelected(int position) {
        mIndicator.setDotChecked(position%(int)mViewPager.getTag());
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageScrollStateChanged(int state) {

        if(isAutoPlaying) {
            switch (state) {
                case 0:
                    if (!play) {
                        play = true;
                    }
                    break;
                case 1:
                    if (play) {
                        play = false;
                    }
                    break;
            }
        }
    }

    boolean play=true;
    int current;
    class AutoPlayThread extends Thread{
        private long interval;
        public AutoPlayThread(long interval) {
            this.interval=interval;
        }
        public void run(){
                while (true) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(play) {
                        current = mViewPager.getCurrentItem();
                        handler.sendEmptyMessage(current);
                    }
                }
        }
    }
    public int getPageMargin() {
        return mViewPager.getPageMargin();
    }
    public int getOffscreenPageLimit() {
        return   mViewPager.getOffscreenPageLimit();
    }
    public PagerAdapter getAdapter() {
        return mViewPager.getAdapter();
    }
    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }
    public int getchildCount() {
        return mViewPager.getChildCount();
    }
    public void setPageMarginDrawable(Drawable drawable) {
        mViewPager.setPageMarginDrawable(drawable);
    }
    public void setPageMarginDrawable(int resId) {
        mViewPager.setPageMarginDrawable(resId);
    }
    public void setPageMargin(int margin) {
        mViewPager.setPageMargin(margin);
    }
    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item);
    }
    public ViewPager getViewPager(){
        return mViewPager;
    }
}