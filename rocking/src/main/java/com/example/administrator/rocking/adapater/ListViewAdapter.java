package com.example.administrator.rocking.adapater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.administrator.rocking.R;
import com.example.administrator.rocking.bean.HeartBean;
import com.example.administrator.rocking.bean.WeightBean;

import java.util.List;

/**
 * Created by Administrator on 2015/10/27.
 */
public class ListViewAdapter extends ArrayAdapter<Object> {

    List<Object> list;
    int resource;
    Class data_class;

    public List<Object> getList() {
        return list;
    }

    public ListViewAdapter(Context context, int resource, List<Object> list, Class data_class) {
        super(context, resource, list);
        this.resource = resource;
        this.list = list;
        this.data_class = data_class;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(getContext(), resource, null);
        }

        TextView tv_1 = (TextView) convertView.findViewById(R.id.tv_1);
        TextView tv_2 = (TextView) convertView.findViewById(R.id.tv_2);
        TextView tv_3 = (TextView) convertView.findViewById(R.id.tv_3);

        if (data_class == HeartBean.class) {
            HeartBean heartBean = (HeartBean) list.get(position);
            tv_1.setText(heartBean.getHid() + "");
            tv_2.setText(heartBean.getHdate() + "");
            tv_3.setText(heartBean.getHtime() + "");

        } else if (data_class == WeightBean.class) {
            WeightBean weightBean = (WeightBean) list.get(position);
            tv_1.setText(weightBean.getId() + "");
            tv_2.setText(weightBean.getData() + "");
            tv_3.setText(weightBean.getDate() + "");
        }

        return convertView;
    }
}
