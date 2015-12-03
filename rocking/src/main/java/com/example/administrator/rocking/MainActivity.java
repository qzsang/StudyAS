package com.example.administrator.rocking;


import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.rocking.fragment.ContentFragment1_;
import com.example.administrator.rocking.fragment.ContentFragment2_;
import com.example.administrator.rocking.fragment.ContentFragment3_;
import com.example.administrator.rocking.fragment.ContentFragmentIndex;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @ViewById
    DrawerLayout drawer_layout;
    @ViewById
    RelativeLayout rl_content;

    @ViewById
    LinearLayout side_nav_bar;

    Fragment fragment_content[];
    @AfterViews
    public void creatAfter() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragment_content = new Fragment[] {new ContentFragmentIndex(),new ContentFragment1_(),new ContentFragment2_(),new ContentFragment3_()};


        getFragmentManager().beginTransaction()
                .add(R.id.rl_content, fragment_content[0]).hide(fragment_content[0])
                .add(R.id.rl_content, fragment_content[1]).hide(fragment_content[1])
                .add(R.id.rl_content, fragment_content[2]).hide(fragment_content[2])
                .add(R.id.rl_content, fragment_content[3]).hide(fragment_content[3])
                .show(fragment_content[0])
                .commit();


        //点图像返回首页
        side_nav_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSideBarById(v.getId());
            }
        });
    }



    public void openMenu(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    int old_index = 0;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_manage:
                //关闭左侧
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START);
                }
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
        }

        changeSideBarById(item.getItemId());
        return true;
    }


    public void changeSideBarById(int id) {
        int index = 0;
        switch (id) {

            case R.id.nav_camara:
                index = 1;

                break;

            case R.id.nav_gallery:
                index = 2;

                break;

            case R.id.nav_slideshow:
                index = 3;

                break;
        }


        //关闭左侧
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        }

        getFragmentManager().beginTransaction()
                .hide(fragment_content[old_index])
                .show(fragment_content[index])
                .commit();

        old_index = index;
    }
}
