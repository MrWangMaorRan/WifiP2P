package leavesc.hello.filetransfer;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import java.util.Locale;

import leavesc.hello.filetransfer.Banners.TTAdManagerHolder;

public class MianApplication extends Application {
    private Resources resources;

    private Configuration config;
    private static Context context;
    private DisplayMetrics dm;
    @Override
    public void onCreate() {
        super.onCreate();
        setlanguage();
        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this);
        MianApplication.context = getApplicationContext();

    }


    public void setlanguage() {
        //获取系统当前的语言
        String able= getResources().getConfiguration().locale.getLanguage();
        resources =getResources();//获得res资源对象
        config = resources.getConfiguration();//获得设置对象
        dm = resources.getDisplayMetrics();
        //根据系统语言进行设置
        if (able.equals("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            resources.updateConfiguration(config, dm);
        } else if(able.equals("en")) {
            config.locale = Locale.US;
            resources.updateConfiguration(config, dm);
        }else if (able.equals("zh_TW")){
            config.locale = Locale.TRADITIONAL_CHINESE;
            resources.updateConfiguration(config, dm);
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
    public static Context getAppContext() {
        return MianApplication.context;
    }
}
