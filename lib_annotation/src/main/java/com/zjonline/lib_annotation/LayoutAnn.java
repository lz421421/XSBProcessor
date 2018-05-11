package com.zjonline.lib_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 39157 on 2018/4/27.
 * 布局的注解类
 * layout 布局
 * title  标题
 * titleStringRes 标题资源
 * ...
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface LayoutAnn {
    /**
     */
    int layout() default 0;

    /**
     */
    String title() default "";

    /**
     */
    int titleStringRes() default 0;

    /**
     */
    int leftImgRes() default 0;

    /**
     */
    int[] rightImgRes() default {0, 0};

    int[] rightText() default {0, 0};

    boolean isSwipeBack() default true; //
}
