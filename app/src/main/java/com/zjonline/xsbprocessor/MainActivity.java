package com.zjonline.xsbprocessor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zjonline.lib_annotation.LayoutAnn;
import com.zjonline.lib_annotation.LayoutAnnInit;

@LayoutAnn(layout = R.layout.activity_main, titleStringRes = R.string.hello, rightImgRes = {R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round})
public class MainActivity extends AppCompatActivity {
    TitleView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutAnnInit.bind(this, R.id.xsb_view_title);

    }

    public void start(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void setTitleView(View titleView) {
        this.titleView = (TitleView) titleView;
    }

    /**
     * 自动生成的代码调用
     *
     * @param layoutId 布局的layout
     * @return 返回可以滑动结束的布局
     */
    public View createSwipeBackView(int layoutId) {
        return MySlidingPaneLayout.swipeBackView(this, layoutId);
    }
}
