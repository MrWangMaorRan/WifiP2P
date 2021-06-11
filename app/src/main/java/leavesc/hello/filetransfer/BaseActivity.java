package leavesc.hello.filetransfer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import leavesc.hello.filetransfer.common.LoadingDialog;
import leavesc.hello.filetransfer.duoyuyan.Config;
import leavesc.hello.filetransfer.duoyuyan.Store;

/**
 * 作者：leavesC
 * 时间：2019/2/27 23:52
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show(message, true, false);
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    protected <T extends Activity> void startActivity(Class<T> tClass) {
        startActivity(new Intent(this, tClass));
    }

    protected <T extends Service> void startService(Class<T> tClass) {
        startService(new Intent(this, tClass));
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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
