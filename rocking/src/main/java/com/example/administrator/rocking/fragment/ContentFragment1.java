package com.example.administrator.rocking.fragment;

import android.app.Fragment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.rocking.MainActivity;
import com.example.administrator.rocking.R;
import com.example.administrator.rocking.adapater.ListViewAdapter;
import com.example.administrator.rocking.application.App;
import com.example.administrator.rocking.application.Constant;
import com.example.administrator.rocking.bean.HeartBean;
import com.example.administrator.rocking.utils.ParseUtil;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;

import net.tsz.afinal.FinalDb;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.fragment_content_1)
public class ContentFragment1 extends Fragment {
    @ViewById
    TextView tv_title;
    @ViewById
    SwipeRefreshLayout swipe_container;
    @ViewById
    ListView lv_data;

    ProgressBar pb_list_foot;
    ListViewAdapter listViewAdapter;

    int page = 0;
    boolean is_loading = false;//是否加载中
    boolean is_load_all = false;//是否加载全部
    @AfterViews
    public void creatAfter() {
        tv_title.setText("心率数据");


        //设置刷新时动画的颜色，可以设置5个
        swipe_container.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.black);


        //下拉刷新
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                is_load_all = false;
                if (lv_data.getFooterViewsCount()  <= 0)
                    lv_data.addFooterView(pb_list_foot);
                loadData(true);

            }
        });

        lv_data.setOnScrollListener(new AbsListView.OnScrollListener() {
            Handler handler = new Handler();
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listViewAdapter != null) {
                    //如果加载到了最后一条记录
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && !is_load_all) {
                        pb_list_foot.setVisibility(View.VISIBLE);
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                loadData();
                            }
                        }, 100);

                    }
                }
            }
        });

        pb_list_foot = (ProgressBar) View.inflate(getActivity(), R.layout.item_list_data_foot,null);
        lv_data.addFooterView(pb_list_foot);
        listViewAdapter = new ListViewAdapter(
                getActivity(),
                R.layout.item_list_data,
                new ArrayList<Object>(),
                HeartBean.class);
        lv_data.setAdapter(listViewAdapter);


    }

    //加载数据
    public void loadData(){
        loadData(false);
    }
    public void loadData(final boolean down_load) {

        if (is_loading || is_load_all) {
            return;
        }

        if (is_load_all) {
            swipe_container.setRefreshing(false);
            //pb_list_foot.setVisibility(View.GONE);
            return;
        }

        if (down_load)
            page = 0;
        is_loading = true;
        Log.e("page",page+"");
        LiteHttp liteHttp = ((App) getActivity().getApplication()).getLiteHttp();
        String url = Constant.heartData + "?page="+page;
        liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
            @Override
            public void onSuccess(String s, Response<String> response) {
                // 成功：主线程回调，反馈一个string

                Log.i("http", "success");
                Log.i("http", response.getResult() + "");
                List<HeartBean> datas = ParseUtil.parseHeart(response.getResult());
                if (datas == null || datas.size() == 0) {
                    is_load_all = true;
                    Toast.makeText(getActivity(), "已加载全部", Toast.LENGTH_LONG).show();
                    lv_data.removeFooterView(pb_list_foot);
                }

                List<Object> list = listViewAdapter.getList();
                if (down_load)
                    list.clear();
                list.addAll(datas);
                listViewAdapter.notifyDataSetChanged();
                datas = null;

                swipe_container.setRefreshing(false);
            }

            @Override
            public void onFailure(HttpException e, Response<String> response) {
                // 失败：主线程回调，反馈异常
                /*Log.i("http", "onFailure");
                Log.i("http", e.toString() + "");
                Log.i("http", response.getResult() + "");
                Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();*/
                List<HeartBean> datas = FinalDb.create(getActivity()).findAllByWhere(HeartBean.class, "username='" + Constant.username + "'", "id desc limit 30");
                if (datas == null || datas.size() == 0) {
                    is_load_all = true;
                    Toast.makeText(getActivity(), "已加载全部", Toast.LENGTH_LONG).show();
                    lv_data.removeFooterView(pb_list_foot);
                }

                List<Object> list = listViewAdapter.getList();
                    list.clear();
                list.addAll(datas);
                listViewAdapter.notifyDataSetChanged();
                datas = null;

                swipe_container.setRefreshing(false);
                pb_list_foot.setVisibility(View.GONE);
            }
        }));
        page++;
        is_loading = false;
    }
    @Click({R.id.iv_title})
    public void openMenu(View v) {
        switch (v.getId()) {

            case R.id.iv_title:
                ((MainActivity) getActivity()).openMenu();
                break;

        }

    }
}
