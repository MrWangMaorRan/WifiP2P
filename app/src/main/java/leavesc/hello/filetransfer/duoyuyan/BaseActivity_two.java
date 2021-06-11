package leavesc.hello.filetransfer.duoyuyan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;


public class BaseActivity_two extends AppCompatActivity {
    private static final String TAG = "BaseAc";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.ACTION);  //这个ACTION和后面activity的ACTION一样就行，要不然收不到的
        registerReceiver(myBroadcastReceive, intentFilter);
        changeAppLanguage();
    }
    public void changeAppLanguage() {
        String sta = Store.getLanguageLocal(this);
        if(sta != null && !"".equals(sta)){
            // 本地语言设置
     /*       Locale myLocale = new Locale(sta);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);*/
            // 本地语言设置
            //  Locale locale = new Locale("ug", Locale.CHINA.getCountry());
            Locale myLocale=null;
            if(sta.equals("zh_CN")){
                myLocale = new Locale(sta, Locale.CHINESE.getCountry());
            }else if(sta.equals("zh_TW")){
                myLocale = new Locale("TW", Locale.TRADITIONAL_CHINESE.getCountry());
            }else  if(sta.equals("en")||sta.equals("en_US")){
                myLocale = new Locale( "en", Locale.ENGLISH.getCountry());
            }
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

    }
    BroadcastReceiver myBroadcastReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("广播", "----接收到的是----" + intent.getStringExtra("msg"));
            if(intent.getStringExtra("msg").equals("EVENT_REFRESH_LANGUAGE")){
                changeAppLanguage();
                recreate();//刷新界面

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceive);
        EventBus.getDefault().unregister(this);
    }

}
