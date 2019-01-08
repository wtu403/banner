package com.example.dcang.myapplication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewUtil {
    //是否需要执行反射方法
    private static boolean reflexMethods = true;
    /**
     * 是否需要执行方法的反射
     * @param reflexMethods
     */
    public static void setReflexMethods(boolean reflexMethods) {
        ViewUtil.reflexMethods = reflexMethods;
    }

    @UiThread
    public static void inject(@NonNull Activity target){
        View sourceView = target.getWindow().getDecorView();
        bindView(target,sourceView);
    }

    @UiThread
    public static void inject(@NonNull Object target, @NonNull View source){
        bindView(target,source);
    }

    private static void bindView(@NonNull Object target, @NonNull View source){
        Class<?> handlerType = target.getClass();
        Field[] fields = handlerType.getDeclaredFields();
        if (fields != null && fields.length > 0){
            int length = fields.length;
            for (int i = 0;i < length;i++){
                Field field = fields[i];
                IFindById iFindById = (IFindById)field.getAnnotation(IFindById.class);
                if (iFindById == null){
                    continue;
                }
                View view = source.findViewById(iFindById.value());
                if (view != null){
                    try {
                        field.setAccessible(true);
                        field.set(target, view);
                    }catch (IllegalAccessException e){

                    }
                }
            }
        }

        if (!reflexMethods){
            return;
        }
        //是否需要用OnClick来添加事件，从理论上来，还不如用系统的事件，因为通过反射来处理这个，有一定性能上的消耗
        Method[] methods = handlerType.getMethods();
        if (methods != null && methods.length > 0){
            int length = methods.length;
            for (int i = 0;i < length;i++){
                Method method = methods[i];
                OnClick click = (OnClick)method.getAnnotation(OnClick.class);
                if (click != null){
                    int[] value = click.value();
                    int valueLength = value.length;
                    if (valueLength > 0){
                        for (int v = 0;v < valueLength;v++){
                            View view = source.findViewById(value[v]);
                            view.setOnClickListener(new DeclaredOnClickListener(method,target));
                        }
                    }
                }
            }
        }
    }
    static class DeclaredOnClickListener implements View.OnClickListener {
        private Method mResolvedMethod;
        private Object object;

        DeclaredOnClickListener (Method method,Object object){
            mResolvedMethod = method;
            this.object = object;
        }

        @Override
        public void onClick(View v) {
            try {
                mResolvedMethod.setAccessible(true);
                mResolvedMethod.invoke(object,v);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }
}
