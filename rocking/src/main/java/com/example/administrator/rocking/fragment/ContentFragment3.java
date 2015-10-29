package com.example.administrator.rocking.fragment;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.rocking.MainActivity;
import com.example.administrator.rocking.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_content_3)
public class ContentFragment3 extends Fragment {
    @ViewById
    TextView tv_title;

    @AfterViews
    public void creatAfter() {
        tv_title.setText("控制中心");
    }
    @Click({R.id.iv_title})
    public void openMenu(View v){
        switch (v.getId()){

            case R.id.iv_title:
                ((MainActivity)getActivity()).openMenu();
                break;

        }

    }
}
