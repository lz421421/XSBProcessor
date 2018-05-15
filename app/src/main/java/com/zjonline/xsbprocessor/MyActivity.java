package com.zjonline.xsbprocessor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zjonline.lib_annotation.LayoutAnnInterface;
import com.zjonline.lib_annotation.LayoutAnn;
import com.zjonline.lib_annotation.LayoutAnnInit;

/**
 * Created by 39157 on 2018/5/15.
 */
@LayoutAnn(layout = R.layout.activity_my)
public class MyActivity extends AppCompatActivity implements LayoutAnnInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutAnnInit.bind(this);

        MyFragment fragment = new MyFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, fragment).commit();


    }

    @Override
    public int layoutId() {
        return 0;
    }

    @Override
    public void setTitleView(View titleView) {

    }

    @Override
    public View createSwipeBackView(int layoutId) {
        return null;
    }
}
