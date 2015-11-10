package com.example.administrator.viewpagedemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ViewPager vp_test;
    List<View> views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp_test = (ViewPager) findViewById(R.id.vp_test);
        views = new ArrayList<View>();

        View view1 = View.inflate(this, R.layout.item_viewpage1, null);
        View view2 = View.inflate(this, R.layout.item_viewpage2, null);
        View view3 = View.inflate(this, R.layout.item_viewpage3, null);
        View viewend = View.inflate(this, R.layout.item_viewpage1, null);
        View viewstart = View.inflate(this, R.layout.item_viewpage3, null);

        views.add(viewstart);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(viewend);
        vp_test.setAdapter(new MyPagerAdapter());
        vp_test.setCurrentItem(1, false);
        vp_test.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0){
                    vp_test.setCurrentItem(3,false);
                }
                if (position == 4){
                    vp_test.setCurrentItem(1,false);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            Log.i("isViewFromObject", String.valueOf("isViewFromObject:"+arg0 == arg1));
            return arg0 == arg1;    //这行代码很重要，它用于判断你当前要显示的页面
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            Log.d("tag", String.valueOf(position));
            return views.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }



    }

}
