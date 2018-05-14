package com.zjonline.xsbprocessor;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zjonline.lib_annotation.LayoutAnn;
import com.zjonline.lib_annotation.LayoutAnnInit;

@LayoutAnn(layout = R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutAnnInit.bind(this);
        View vl = new TextView(this);
        Log.e("MainActivity", vl.getClass().getName());

        try {
            vl.getClass().getMethod("",Integer.class).invoke(vl,"");
        } catch (Exception e) {

        }
    }

    /**
     * 绑定布局和 标题栏
     *
     * @param title          标题
     * @param titleStringRes 标题资源文件
     * @param leftImgRes     左边第一个
     * @param rightImgRes    右边的图片资源
     * @param rightText      右边的文字资源
     * @param isSwipeBack    是否需要滑动返回
     */
    final protected void initLayoutAndTitle(String title,
                                            @StringRes int titleStringRes, int leftImgRes,
                                            int rightImgRes[], int rightText[], boolean isSwipeBack) {
    }
}
