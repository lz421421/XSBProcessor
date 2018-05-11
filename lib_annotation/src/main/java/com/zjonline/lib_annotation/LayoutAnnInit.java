package com.zjonline.lib_annotation;

import android.app.Activity;

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

    public static void bind(Activity target) {
        createBinding(target);
    }

    private static void createBinding(Activity target) {
        Class<?> targetClass = target.getClass();
        Constructor<?> constructor = findBindingConstructorForClass(targetClass);
        if (constructor == null) return;
        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            constructor.newInstance(target);
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

    private static Constructor<?> findBindingConstructorForClass(Class<?> cls) {
        Constructor<?> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null) return bindingCtor;
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) return null;
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_LayoutAnn");
            //noinspection unchecked
            bindingCtor = bindingClass.getConstructor(cls);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }
}
