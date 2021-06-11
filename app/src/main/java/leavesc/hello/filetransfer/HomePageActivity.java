package leavesc.hello.filetransfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.lwy.righttopmenu.MenuItem;
import com.lwy.righttopmenu.RightTopMenu;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import leavesc.hello.filetransfer.Banners.BannerExpressActivity;
import leavesc.hello.filetransfer.Banners.DislikeDialog;
import leavesc.hello.filetransfer.Banners.TTAdManagerHolder;
import leavesc.hello.filetransfer.Banners.TToast;
import leavesc.hello.filetransfer.duoyuyan.BaseActivity_two;
import leavesc.hello.filetransfer.duoyuyan.Config;
import leavesc.hello.filetransfer.duoyuyan.LanguageUtils;
import leavesc.hello.filetransfer.duoyuyan.Store;
import leavesc.hello.filetransfer.telephone.PermissinsUtils_one;
import leavesc.hello.filetransfer.util.DisplayUtil;
import leavesc.hello.filetransfer.util.PermissinsUtils;
import leavesc.hello.filetransfer.util.SharedPreferencesUtil;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class HomePageActivity extends BaseActivity {

    public static final  String ACTION_BONED = "ACTION_BONED";

    public static final String ACTION_BONED_DATA = "ACTION_BONED_DATA";
    private TextView agreement;
    private TextView cancel;
    private TextView consent;
    private TextView policy;
    private Dialog mShareDialog;
    private Button old;
    private Button news;
    private ImageView mMenuIV;
    private RightTopMenu mRightTopMenu;
    private TTNativeExpressAd mTTAd;

    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        onTobat();
        String ok = SharedPreferencesUtil.getSharedPreferences(HomePageActivity.this).getString("OK", "");
        if (ok==null||!ok.equals("123")){
            onDialog();
        }
        initView();
        initBanners();
        onButtonListener();
    }

    private void initBanners() {
        express_container = findViewById(R.id.express_container);
        mContext = this.getApplicationContext();
        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdManagerHolder.init(this);
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。



        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946200858") //广告位id
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(FrameLayout.LayoutParams.MATCH_PARENT, 150) //期望模板广告view的size,单位dp
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                Log.i("失败",code+""+message);
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.i("请求成功","成");
                Log.i("请求成功",ads.size()+"");
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                if (mTTAd!=null){
                    mTTAd.render();
                    bindAdListener(mTTAd);

                }

                startTime = System.currentTimeMillis();
                TToast.show(mContext, "load success!");
            }
        });

    }
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                TToast.show(mContext, "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", view+"");
                //返回view的宽高 单位 dp
                TToast.show(mContext, "渲染成功");
                if (view!=null){
                    express_container .removeAllViews();
                    express_container.addView(view);
                }

            }
        });
        //dislike设置
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(mContext, "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(mContext, "下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(mContext, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(mContext, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(mContext, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(mContext, "点击安装", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 设置广告的不喜欢，开发者可自定义样式
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle){
        if (customStyle) {
            //使用自定义样式，用户选择"为什么看到此广告"，开发者需要执行startPersonalizePromptActivity逻辑进行跳转
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(this, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    TToast.show(mContext, "点击 " + filterWord.getName());
                    //用户选择不喜欢原因后，移除广告展示
                    express_container.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(mContext, "点击了为什么看到此广告");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(HomePageActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }



            @Override
            public void onSelected(int position, String value, boolean enforce) {
                TToast.show(HomePageActivity.this, "点击 " + value);
                express_container.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    TToast.show(HomePageActivity.this, "模版Banner 穿山甲sdk强制将view关闭了");
                }
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "点击取消 ");
            }




        });
//            //使用默认模板中默认dislike弹出样式

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

            private String ok="123";

            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
                SharedPreferencesUtil.getSharedPreferences(HomePageActivity.this).putString("OK",ok);
            }
        });
    }
    //在Activity的onDestroy回调方法中销毁广告对象
    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }
    public void  onTobat(){
        mMenuIV = findViewById(R.id.menu_iv);
        mMenuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MenuItem> menuItems = new ArrayList<>();
                menuItems.add(new MenuItem(R.mipmap.fenxiang, getResources().getString(R.string.Share_with_friends), 100));
                menuItems.add(new MenuItem(R.mipmap.pinglun,getResources().getString(R.string.Rate_us), 0));
                menuItems.add(new MenuItem(R.mipmap.yinsi, getResources().getString(R.string.Privacy_Policy)));
                menuItems.add(new MenuItem(R.mipmap.xieyi, getResources().getString(R.string.Terms_of_Service)));
                if (mRightTopMenu == null) {
                    Log.i("点击了","点击了");
                    mRightTopMenu = new RightTopMenu.Builder(HomePageActivity.this)
//                            .windowHeight(480)     //当菜单数量大于3个时，为wrap_content,反之取默认高度320
//                        .windowWidth()      //默认宽度wrap_content
                            .dimBackground(true)           //背景变暗，默认为true
                            .needAnimationStyle(true)   //显示动画，默认为true
                            .animationStyle(R.style.RTM_ANIM_STYLE)  //默认为R.style.RTM_ANIM_STYLE

                            .menuItems(menuItems)
                            .onMenuItemClickListener(new RightTopMenu.OnMenuItemClickListener() {
                                @Override
                                public void onMenuItemClick(int position) {
                                    final String[] cities = {getString(R.string.lan_chinese), getString(R.string.lan_en),getString(R.string.lan_zh_rTYW),getString(R.string.Follow_the_system)};
                                    final String[] locals = {"zh_CN", "en","zh_TW","111"};
                                    if (position==0){
                                        Toast.makeText(HomePageActivity.this, "点击菜单:" + 0, Toast.LENGTH_SHORT).show();
                                    }else if (position==1){
                                        Toast.makeText(HomePageActivity.this, "点击菜单:" + 1, Toast.LENGTH_SHORT).show();
                                    }else if (position==2){
                                        Toast.makeText(HomePageActivity.this, "点击菜单:" + 2, Toast.LENGTH_SHORT).show();
                                    }else if (position==3){
                                        Toast.makeText(HomePageActivity.this, "点击菜单:" + 3, Toast.LENGTH_SHORT).show();
                                       // Duoyuyan();
                                    }

                                }
                            }).build();
                }
                mRightTopMenu.showAsDropDown(mMenuIV, 0, 0);
            }
        });

    }
}
