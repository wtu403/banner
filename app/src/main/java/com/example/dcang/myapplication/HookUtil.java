package com.example.dcang.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static java.lang.reflect.Proxy.newProxyInstance;

public class HookUtil {
    private Context mContext;
    public HookUtil(Context mContext){
        this.mContext = mContext;
    }
    public void hookStartActivity() throws Exception{

        //ActivityManagerNative.gDefault
        Class<?> anmClass = Class.forName("android.app.ActivityManagerNative");
        Field defaulField = anmClass.getDeclaredField("gDefault");
        defaulField.setAccessible(true);
        Object gDefaulObject = defaulField.get(null);

        //gDefault中的singleton的instance，他就是AMS
        Class<?> singletionClass = Class.forName("android.util.Singleton");
        Field insField = singletionClass.getDeclaredField("mInstance");
        insField.setAccessible(true);
        Object amsObj = insField.get(gDefaulObject);

        //动态代理Hook下钩子
        amsObj = newProxyInstance(mContext.getClass().getClassLoader(),
                amsObj.getClass().getInterfaces(),
                new StartActivityInvocationHandler(amsObj));
        insField.set(gDefaulObject,amsObj);

    }

    public void hookLaunchActivity() throws Exception{
//        // 获取ActivityThread
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field  currentActivityThread = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        currentActivityThread.setAccessible(true);
        Object currentObject = currentActivityThread.get(null);
        //获取Handler mH
        Field mHField  = activityThreadClass.getDeclaredField("mH");
        mHField .setAccessible(true);
        Handler mh = (Handler) mHField.get(currentObject);
        // 设置Callback
        Field callBackField = Handler.class.getDeclaredField("mCallback");
        callBackField.setAccessible(true);
        callBackField.set(mh, new ActivityThreadHandlerCallBack());
//
//
        // TODO 兼容AppCompatActivity报错问题(有问题，只能做参考)
//        Class<?> forName = Class.forName("android.app.ActivityThread");
//        Field field = forName.getDeclaredField("sCurrentActivityThread");
//        field.setAccessible(true);
//        Object activityThread = field.get(null);
//
//        Method getPackageManager = activityThread.getClass().getDeclaredMethod("getPackageManager");
//        Object iPackageManager = getPackageManager.invoke(activityThread);
//
//        StartActivityInvocationHandler handler = new StartActivityInvocationHandler(iPackageManager);
//        Class<?> iPackageManagerIntercept = Class.forName("android.content.pm.IPackageManager");
//        Object proxy = newProxyInstance(Thread.currentThread().getContextClassLoader(),
//                new Class<?>[]{iPackageManagerIntercept}, handler);
//
//        // 获取 sPackageManager 属性
//        Field iPackageManagerField = activityThread.getClass().getDeclaredField("sPackageManager");
//        iPackageManagerField.setAccessible(true);
//        iPackageManagerField.set(activityThread, proxy);

    }

    private void handleLaunchActivity(Message msg) {
        try {
            Object obj = msg.obj;
            Field intentField = obj.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent proxyIntent = (Intent) intentField.get(obj);
            // 代理意图
            Intent originIntent = proxyIntent.getParcelableExtra("realIntent");
            if (originIntent != null) {
                // 替换意图
                intentField.set(obj, originIntent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class StartActivityInvocationHandler implements InvocationHandler {
        private Object mAmsObj;
        StartActivityInvocationHandler(Object amsObj){
            this.mAmsObj = amsObj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 拦截到所有AMS中被调用的方法
            if ("startActivity".equals(method.getName())){
                //从源码得知，取第三个
                Intent intent = (Intent) args[2];
                //ProxyActivity 用于占坑的
                Intent proxyIntent = new Intent();
                proxyIntent.setComponent(new ComponentName(mContext,ProxyActivity.class));
                // 把原来的Intent绑在代理Intent上面
                proxyIntent.putExtra("realIntent",intent);
                // 让proxyIntent去晒太阳，借尸
                args[2] = proxyIntent;
            }
            return method.invoke(mAmsObj,args);
        }
    }

    class ActivityThreadHandlerCallBack implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100)
                handleLaunchActivity(msg);
            return false;
        }
    }
}

