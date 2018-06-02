package com.zjonline.lib_annotation;

import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by 39157 on 2018/5/11.
 * 绑定布局的工具类  bind();
 */
public class LayoutAnnInit {

    static final Map<Class<?>, Constructor<?>> BINDINGS = new LinkedHashMap<>();


    public static void bind(LayoutAnnInterface target) {
        Constructor<?> constructor = findOneParamConstructor(target.getClass(), 1);
        if (constructor != null) {
            try {
                constructor.newInstance(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param target      当前activity
     * @param titleViewId 标题viewID
     */
    public static void bind(LayoutAnnInterface target, int titleViewId) {
        Constructor<?> constructor = findOneParamConstructor(target.getClass(), 2);
        if (constructor != null) {
            try {
                constructor.newInstance(target, titleViewId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param target      当前activity
     * @param titleViewId 标题viewID
     */
    public static View bind(LayoutAnnInterface target, ViewGroup container, int titleViewId) {
        Constructor<?> constructor = findOneParamConstructor(target.getClass(), 3);
        if (constructor != null) {
            try {
                Object o = constructor.newInstance(target, titleViewId, container);
                return (View) o.getClass().getDeclaredMethod("getView").invoke(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 初始化对象的构造参数
     *
     * @param cls  传进来的对象
     * @param type 构造参数的类型 1 一个参数 2两个参数 3三个参数
     * @return 返回构造参数
     */
    private static Constructor<?> findOneParamConstructor(Class<?> cls, int type) {
        Class<?> targetClass = cls.getClass();
        Constructor<?> bindingCtor = BINDINGS.get(targetClass);
        if (bindingCtor != null) return bindingCtor;
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) return null;
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_LayoutAnn");
            switch (type) {
                case 1:
                    bindingCtor = bindingClass.getConstructor(cls);
                    break;
                case 2:
                    bindingCtor = bindingClass.getConstructor(cls, int.class);
                    break;
                case 3:
                    bindingCtor = bindingClass.getConstructor(cls, int.class, ViewGroup.class);
                    break;
            }
        } catch (ClassNotFoundException e) {
            bindingCtor = findOneParamConstructor(cls.getSuperclass(), type);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;

    }
}
