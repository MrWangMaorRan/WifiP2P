package leavesc.hello.filetransfer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import leavesc.hello.filetransfer.telephone.PermissinsUtils_one;
import leavesc.hello.filetransfer.util.DisplayUtil;
import leavesc.hello.filetransfer.util.PermissinsUtils;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class HomePageActivity extends AppCompatActivity {

    public static final  String ACTION_BONED = "ACTION_BONED";

    public static final String ACTION_BONED_DATA = "ACTION_BONED_DATA";
    private TextView agreement;
    private TextView cancel;
    private TextView consent;
    private TextView policy;
    private Dialog mShareDialog;
    private Button old;
    private Button news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        onTobat();
        onDialog();
        initView();
        onButtonListener();
    }

    private void onButtonListener() {
        old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissinsUtils.getPermission(HomePageActivity.this);
                PermissinsUtils_one.getPermission(HomePageActivity.this);
                Intent intent = new Intent(HomePageActivity.this, ScannerActivity.class);
                // 设置扫码框的宽
                intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, DisplayUtil.dip2px(HomePageActivity.this, 200));
                // 设置扫码框的高
                intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, DisplayUtil.dip2px(HomePageActivity.this, 200));
                // 设置扫码框距顶部的位置
                intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, DisplayUtil.dip2px(HomePageActivity.this, 100));
                // 可以从相册获取
                // intent.putExtra(Constant.EXTRA_IS_ENABLE_SCAN_FROM_PIC, true);
                startActivityForResult(intent, REQUEST_CODE);
                //startActivityForResult(intent,REQUEST_CODE);
            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissinsUtils.getPermission(HomePageActivity.this);
                PermissinsUtils_one.getPermission(HomePageActivity.this);
                Intent intent = new Intent(HomePageActivity.this, ReceiveFileActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null || data.getExtras() == null) {
                        return;
                    }
                     String result = data.getExtras().getString(Constant.EXTRA_RESULT_CONTENT);
                    Toast.makeText(getApplicationContext(), result + "", Toast.LENGTH_LONG).show();
//                        Intent intent1 = new Intent("com.BroadcastAction");
//                        intent1.putExtra("result", result);
//                        sendBroadcast(intent1);
                        Intent intent = new Intent(HomePageActivity.this, SendFileActivity.class);
                        intent.putExtra("result",result);
                        startActivity(intent);


//                    String result = data.getExtras().getString(Constant.EXTRA_RESULT_CONTENT);
//                    Log.e("zq", "二维码扫描结果：" + result);
//                    if (TextUtils.isEmpty(result)) {
//                        return;
//                    }
                } else {
                }
        }
    }



    private void initView() {
        old = findViewById(R.id.old);
        news = findViewById(R.id.news);
    }

    public void onDialog(){
        mShareDialog = new Dialog(this, R.style.dialog_bottom_full);
        mShareDialog.setCanceledOnTouchOutside(false); //手指触碰到外界取消
        mShareDialog.setCancelable(false);             //可取消 为true(屏幕返回键监听)
        Window window = mShareDialog.getWindow();      // 得到dialog的窗体
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.share_animation);
        window.getDecorView().setPadding(150, 0, 150, 0);

        View view = View.inflate(this, R.layout.dialog_lay_share_dialog, null); //获取布局视图
        agreement = view.findViewById(R.id.agreement);
        cancel = view.findViewById(R.id.cancel);
        consent = view.findViewById(R.id.consent);
        policy = view.findViewById(R.id.policy);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏

        DialogListener();

        mShareDialog.show();
    }

    private void DialogListener() {
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AgreementActivity.class);
                startActivity(intent);
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, PolicyActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        consent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
            }
        });
    }

    public void  onTobat(){
        Toolbar tobar = findViewById(R.id.tobar);
        tobar.setTitle("");
        setSupportActionBar(tobar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // groupId--1:分组的id;itemId--100:菜单项的id;order--1:菜单项排序用的;title--"菜单1":菜单名称;
        //MenuItem item = menu.add(1, 100, 1, "菜单项");
        // 在API>=11时，是不显示图标的
       // item.setIcon(R.drawable.ic_launcher);

        menu.add(1, 101, 1, "分享给好友");
        menu.add(1, 102, 1, "给个好评");
        menu.add(1, 103, 1, "隐私政策");
        menu.add(1, 103, 1, "用户协议");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // 创建菜单项的点击事件
        switch (item.getItemId()) {
            case 101:
                Toast.makeText(this, "你点击了分享", Toast.LENGTH_SHORT).show();
                break;
            case 102:
                Toast.makeText(this, "你点击了好评", Toast.LENGTH_SHORT).show();
                break;
            case 103:
                Intent intent = new Intent(HomePageActivity.this, AgreementActivity.class);
                startActivity(intent);
                Toast.makeText(this, "你点击了隐私政策", Toast.LENGTH_SHORT).show();
                break;
            case 104:
                Intent intent1 = new Intent(HomePageActivity.this, AgreementActivity.class);
                startActivity(intent1);
                Toast.makeText(this, "你点击了用户协议", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
