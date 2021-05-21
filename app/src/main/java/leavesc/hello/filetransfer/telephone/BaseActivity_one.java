package leavesc.hello.filetransfer.telephone;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity基础类
 * Created by Administrator on 2018/5/11.
 */

public abstract class BaseActivity_one extends AppCompatActivity {
    // context
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        // 将Activity添加到栈里
        AppManager.addActivity(this);
        mContext = this;

        getIntentData();
        init(savedInstanceState);

    }

    /**
     * 获取 跳转传递的值
     *
     * @return 资源id
     */
    public abstract void getIntentData();

    /**
     * 获取 layout 资源id
     *
     * @return 资源id
     */
    public abstract int getLayoutId();

    /**
     * 初始化操作
     *
     * @param savedInstanceState savedInstanceState
     */
    public abstract void init(Bundle savedInstanceState);


}
