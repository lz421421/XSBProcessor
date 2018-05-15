package com.zjonline.lib_annotation;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by 39157 on 2018/4/30.
 * 初始化布局的接口
 * 使用注解的类 最好要实现此接口
 */

public interface LayoutAnnInterface {

    int layoutId();

    void setTitleView(View titleView);

    View createSwipeBackView(int layoutId);

    void setContentView(int layoutId);//主要是给fragment使用的

    void setContentView(View view);//主要是给fragment使用的

    <T extends View> T findViewById(int viewId);//主要给fragment使用

    LayoutInflater getLayoutInflater();

}
