package com.zjonline.xsbprocessor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjonline.lib_annotation.LayoutAnnInterface;
import com.zjonline.lib_annotation.LayoutAnn;
import com.zjonline.lib_annotation.LayoutAnnInit;

/**
 * Created by 39157 on 2018/5/15.
 */
@LayoutAnn(layout = R.layout.fragment_my, title = "myFragment")
public class MyFragment extends Fragment implements LayoutAnnInterface {
    TitleView titleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutAnnInit.bind(this, container, R.id.tv_title);
    }

    @Override
    public int layoutId() {
        return 0;
    }

    @Override
    public void setTitleView(View titleView) {
        this.titleView = (TitleView) titleView;
    }

    @Override
    public View createSwipeBackView(int layoutId) {
        return null;
    }

    @Override
    public void setContentView(int layoutId) {

    }

    @Override
    public void setContentView(View view) {
    }

    @Override
    public <T extends View> T findViewById(int viewId) {
        return null;
    }
}
