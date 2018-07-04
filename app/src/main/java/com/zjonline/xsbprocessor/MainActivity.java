package com.zjonline.xsbprocessor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zjonline.lib_annotation.LayoutAnnInterface;
import com.zjonline.lib_annotation.LayoutAnn;
import com.zjonline.lib_annotation.LayoutAnnInit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@LayoutAnn(layout = R.layout.activity_main, titleStringRes = R.string.hello, rightText = {0, R.string.app_name}, rightImgRes = {R.mipmap.ic_launcher_round})
public class MainActivity extends AppCompatActivity implements LayoutAnnInterface {
    TitleView titleView;

    public int layoutId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutAnnInit.bind(this, R.id.xsb_view_title);
    }

    public void start(View view) {
        startActivity(new Intent(this, MyActivity.class));
    }

    @Override
    public void setTitleView(View titleView) {
        this.titleView = (TitleView) titleView;
    }

    /**
     * 自动生成的代码调用
     *
     * @param layoutId 布局的layout
     * @return 返回可以滑动结束的布局
     */
    @Override
    public View createSwipeBackView(int layoutId) {
        return MySlidingPaneLayout.swipeBackView(this, layoutId);
    }
}
