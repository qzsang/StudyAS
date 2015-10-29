package com.example.administrator.androidtest;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById
    EditText et_test;
    @ViewById
    Button b_test;


    @Click({R.id.b_test})
    public void onclick(View v) {

        switch (v.getId()){
            case R.id.b_test:
                et_test.setError("哈 测试");
                break;
        }
        getSupportActionBar().hide();
    }

}
