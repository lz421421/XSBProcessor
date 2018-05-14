package com.zjonline.xsbprocessor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zjonline.lib_annotation.LayoutAnn;
import com.zjonline.lib_annotation.LayoutAnnInit;

@LayoutAnn(layout = R.layout.activity_main, title = "你好")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutAnnInit.bind(this, R.id.xsb_view_title);

    }

    public void start(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
