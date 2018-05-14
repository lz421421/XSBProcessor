package com.zjonline.xsbprocessor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 39157 on 2017/6/9.
 * 标题
 */

public class TitleView extends FrameLayout implements View.OnClickListener {

    LinearLayout ll_left, ll_right;
    ImageButton imb_left_one, imb_left_two, imb_right_one, imb_right_two;
    TextView tv_right_one, xsb_title_text, tv_right_two;

    public final static int IMB_LEFT_ONE = 0, IMB_LEFT_TWO = 1, IMB_RIGHT_ONE = 2, IMB_RIGHT_TWO = 3;
    public final static int TEXT_RIGHT_ONE = 4, TEXT_RIGHT_TWO = 5;

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        View titleView = LayoutInflater.from(context).inflate(R.layout.xsb_view_title_view, this, true);
        ll_left = titleView.findViewById(R.id.ll_left);
        ll_right = titleView.findViewById(R.id.ll_right);

        imb_left_one = titleView.findViewById(R.id.imb_left_one);
        imb_left_one.setOnClickListener(this);
        imb_left_two = titleView.findViewById(R.id.imb_left_two);
        imb_left_two.setOnClickListener(this);

        imb_right_one = titleView.findViewById(R.id.imb_right_one);
        imb_right_one.setOnClickListener(this);
        imb_right_two = titleView.findViewById(R.id.imb_right_two);
        imb_right_two.setOnClickListener(this);

        xsb_title_text = titleView.findViewById(R.id.xsb_title_text);
        tv_right_one = titleView.findViewById(R.id.tv_right_one);
        tv_right_one.setOnClickListener(this);
        tv_right_two = titleView.findViewById(R.id.tv_right_two);
        tv_right_two.setOnClickListener(this);
    }

    private TitleView setImage(ImageButton one, int oneRes) {
        if (oneRes == 0) one.setVisibility(GONE);
        else {
            one.setVisibility(VISIBLE);
            one.setImageResource(oneRes);
        }
        return this;
    }

    private TitleView setText(TextView one, String text) {
        if (TextUtils.isEmpty(text)) {
            one.setVisibility(GONE);
        } else {
            one.setVisibility(VISIBLE);
            one.setText(text);
        }
        return this;
    }

    /**
     * 设置左边两个图片
     *
     * @param one 左边第一个 0 则不显示
     * @param two 左边第二个 0 则不显示
     * @return TitleView
     */
    public TitleView setLeftImge(int one, int two) {
        setImage(imb_left_one, one);
        setImage(imb_left_two, two);
        return this;
    }

    /**
     * 设置左边两个图片
     *
     * @param one 左边第一个 0 则不显示
     * @return TitleView
     */
    public TitleView setLeftOneImge(int one) {
        setImage(imb_left_one, one);
        return this;
    }


    /**
     * 设置左边两个图片
     *
     * @param two 左边第二个 0 则不显示
     * @return TitleView
     */
    public TitleView setLeftTwoImge(int two) {
        setImage(imb_left_two, two);
        return this;
    }


    /**
     * 设置左边两个图片
     *
     * @param one 右边第一个 0 则不显示
     * @param two 右边第二个 0 则不显示
     * @returnt TitleView
     */
    public TitleView setRightImge(int one, int two) {
        setImage(imb_right_one, one);
        setImage(imb_right_two, two);
        return this;
    }

    public TitleView setRightOneImge(int one) {
        return setImage(imb_right_one, one);
    }

    public TitleView setRightTwoImge(int two) {
        return setImage(imb_right_two, two);
    }


    /**
     * 设置标题
     *
     * @param title 标题
     * @return TitleView
     */
    public TitleView setTitle(int title) {
        if (title == 0) return this;
        return setText(xsb_title_text, getContext().getResources().getString(title));
    }

    /**
     * 设置标题
     *
     * @param title 标题
     * @return TitleView
     */
    public TitleView setTitle(String title) {
        if (title == null) return this;
        return setText(xsb_title_text, title);
    }

    /**
     * 设置右边的文字
     *
     * @param one 右边第一个文字
     * @param two 右边第二个文字
     * @return TitleView
     */
    public TitleView setRightText(String one, String two) {
        setText(tv_right_one, one);
        setText(tv_right_two, two);
        return this;
    }

    public TitleView setRightOneText(String one) {
        setText(tv_right_one, one);
        return this;
    }

    public TitleView setRightTwoText(String two) {
        setText(tv_right_two, two);
        return this;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imb_left_one && onLeftClickListener != null) onLeftClickListener.onLeftClick(v, IMB_LEFT_ONE);
        if (id == R.id.imb_left_two && onLeftClickListener != null) onLeftClickListener.onLeftClick(v, IMB_LEFT_TWO);

        if (id == R.id.imb_right_one && onRightClickListener != null) onRightClickListener.onRightClick(v, IMB_RIGHT_ONE);
        if (id == R.id.imb_right_two && onRightClickListener != null) onRightClickListener.onRightClick(v, IMB_RIGHT_TWO);

        if (id == R.id.tv_right_one && onRightClickListener != null) onRightClickListener.onRightClick(v, TEXT_RIGHT_ONE);
        if (id == R.id.tv_right_two && onRightClickListener != null) onRightClickListener.onRightClick(v, TEXT_RIGHT_TWO);


    }

    OnLeftClickListener onLeftClickListener;

    public interface OnLeftClickListener {
        void onLeftClick(View view, int which);
    }

    public TitleView setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
        return this;
    }

    OnRightClickListener onRightClickListener;

    public interface OnRightClickListener {
        void onRightClick(View view, int which);
    }

    public TitleView setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
        return this;
    }
}
