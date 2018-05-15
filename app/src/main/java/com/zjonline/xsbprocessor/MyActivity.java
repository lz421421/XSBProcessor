package com.zjonline.xsbprocessor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.net.URL;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://stcbeta.8531.cn/assets/20180509/1525829171241_5af24e339949d8745a229fee.jpeg");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Log.e("MyActivity","--->"+bitmap);
                    inputStream.close();
                    Log.e("MyActivity","--->"+connection.getResponseCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
