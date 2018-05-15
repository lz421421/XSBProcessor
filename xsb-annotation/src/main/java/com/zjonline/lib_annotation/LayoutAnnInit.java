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
        bind(target, 0);
    }

    /**
     * @param target      当前activity
     * @param titleViewId 标题viewID
     */
    public static void bind(LayoutAnnInterface target, int titleViewId) {
        createBinding(target, titleViewId, null);
    }


    /**
     * @param target      当前activity
     * @param titleViewId 标题viewID
     */
    public static View bind(LayoutAnnInterface target, ViewGroup container, int titleViewId) {
        Object o = createBinding(target, titleViewId, container);
        try {
            return (View) o.getClass().getMethod("getView").invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Object createBinding(LayoutAnnInterface target, int titleViewId, ViewGroup container) {
        Class<?> targetClass = target.getClass();
        Constructor<?> constructor = findBindingConstructorForClass(targetClass, container);
        if (constructor == null) return null;
        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            if (container == null) return constructor.newInstance(target, titleViewId);
            else return constructor.newInstance(target, titleViewId, container);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    private static Constructor<?> findBindingConstructorForClass(Class<?> cls, ViewGroup container) {
        Constructor<?> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null) return bindingCtor;
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) return null;
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_LayoutAnn");
            //noinspection unchecked
            if (container == null) bindingCtor = bindingClass.getConstructor(cls, int.class);
            else bindingCtor = bindingClass.getConstructor(cls, int.class, ViewGroup.class);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass(), null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }
}
