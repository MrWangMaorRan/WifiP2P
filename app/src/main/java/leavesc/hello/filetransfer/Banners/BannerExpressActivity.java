package leavesc.hello.filetransfer.Banners;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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


import java.util.List;

import leavesc.hello.filetransfer.BaseActivity;
import leavesc.hello.filetransfer.R;


public class BannerExpressActivity extends BaseActivity {
    private TTNativeExpressAd mTTAd;

    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        express_container = findViewById(R.id.express_container);
        mContext = this.getApplicationContext();
        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdManagerHolder.init(this);
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946199398") //广告位id
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
            ad.setDislikeCallback(BannerExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onShow() {

                }



                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    TToast.show(BannerExpressActivity.this, "点击 " + value);
                    express_container.removeAllViews();
                    //用户选择不喜欢原因后，移除广告展示
                    if (enforce) {
                        TToast.show(BannerExpressActivity.this, "模版Banner 穿山甲sdk强制将view关闭了");
                    }
                }

                @Override
                public void onCancel() {
                    TToast.show(mContext, "点击取消 ");
                }




            });
//            //使用默认模板中默认dislike弹出样式

        }

        //在Activity的onDestroy回调方法中销毁广告对象
        @Override
        protected void onDestroy () {
            super.onDestroy();
            if (mTTAd != null) {
                mTTAd.destroy();
            }
        }
    }
