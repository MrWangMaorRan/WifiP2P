package leavesc.hello.filetransfer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import leavesc.hello.filetransfer.Banners.DislikeDialog;
import leavesc.hello.filetransfer.Banners.TTAdManagerHolder;
import leavesc.hello.filetransfer.Banners.TToast;
import leavesc.hello.filetransfer.broadcast.DirectBroadcastReceiver;
import leavesc.hello.filetransfer.callback.DirectActionListener;
import leavesc.hello.filetransfer.model.FileTransfer;
import leavesc.hello.filetransfer.service.WifiServerService;
import leavesc.hello.filetransfer.util.FileType;

/**
 * ?????????leavesC
 * ?????????2019/2/27 23:52
 * ?????????
 * GitHub???https://github.com/leavesC
 * Blog???https://www.jianshu.com/u/9df45b87cfdf
 */
public class ReceiveFileActivity extends BaseActivity {

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private boolean connectionInfoAvailable;
    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAd;
    private DirectActionListener directActionListener = new DirectActionListener() {
        @Override
        public void wifiP2pEnabled(boolean enabled) {
           // log("wifiP2pEnabled: " + enabled);
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
//            log("onConnectionInfoAvailable");
//            log("isGroupOwner???" + wifiP2pInfo.isGroupOwner);
//            log("groupFormed???" + wifiP2pInfo.groupFormed);
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionInfoAvailable = true;
                if (wifiServerService != null) {
                    startService(WifiServerService.class);
                }
            }
        }

        @Override
        public void onDisconnection() {
            connectionInfoAvailable = false;
            //log("onDisconnection");
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
//            log("onSelfDeviceAvailable");
//            log(wifiP2pDevice.toString());
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
           // log("onPeersAvailable,size:" + wifiP2pDeviceList.size());
            for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
               // log(wifiP2pDevice.toString());
            }
        }

        @Override
        public void onChannelDisconnected() {
            //log("onChannelDisconnected");
        }
    };

    private BroadcastReceiver broadcastReceiver;

    private WifiServerService wifiServerService;

    private ProgressDialog progressDialog;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiServerService.MyBinder binder = (WifiServerService.MyBinder) service;
            wifiServerService = binder.getService();
            wifiServerService.setProgressChangListener(progressChangListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            wifiServerService = null;
            bindService();
        }
    };

    private WifiServerService.OnProgressChangListener progressChangListener = new WifiServerService.OnProgressChangListener() {
        @Override
        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("???????????? " + new File(fileTransfer.getFilePath()).getName());
                    progressDialog.setProgress(progress);
                    progressDialog.show();
                }
            });
        }

        @Override
        public void onTransferFinished(final File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    if (file != null && file.exists()) {
                        FileType fileType = new FileType();
                        String mimeType = fileType.getMIMEType(file);
                        Log.i("mimeType", mimeType);
                        if (mimeType.equals("image/jpeg")) {
                            try {
                                String name = file.getName();
                                MediaStore.Images.Media.insertImage(ReceiveFileActivity.this.getContentResolver(), file.getAbsolutePath(), name, null);
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(file);
                                intent.setData(uri);
                                ReceiveFileActivity.this.sendBroadcast(intent);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }else {


                        }
                    }
                }
            });
        }
    };

    int a = 0;
    public String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);

        initView();
        initBanners();
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }

        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        bindService();
        if (a == 0) {
            Log.i("aaaa", a + "");
            //??????????????????????????????
            wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    //log("createGroup onSuccess");
                    dismissLoadingDialog();
                    showToast("onSuccess");
                    a++;
                }

                @Override
                public void onFailure(int reason) {
                    dismissLoadingDialog();
                    showToast("????????????????????????????????????");
                    finish();
                }
            });
        }else {
            Log.i("????????????????????????","????????????????????????");
        }
    }
    private void initBanners() {
        express_container = findViewById(R.id.express_container);
        mContext = this.getApplicationContext();
        //??????TTAdNative?????????createAdNative(Context context) context????????????Activity??????
        TTAdManagerHolder.init(this);
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this);
        //step3:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);


        //step4:????????????????????????AdSlot,??????????????????????????????
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946200858") //?????????id
                .setAdCount(1) //?????????????????????1???3???
                .setExpressViewAcceptedSize(FrameLayout.LayoutParams.MATCH_PARENT, 150) //??????????????????view???size,??????dp
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //??????????????????
            @Override
            public void onError(int code, String message) {
                Log.i("??????",code+""+message);
            }

            //??????????????????
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.i("????????????","???");
                Log.i("????????????",ads.size()+"");
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
                TToast.show(mContext, "???????????????");
            }

            @Override
            public void onAdShow(View view, int type) {
                TToast.show(mContext, "????????????");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", view+"");
                //??????view????????? ?????? dp
                TToast.show(mContext, "????????????");
                if (view!=null){
                    express_container .removeAllViews();
                    express_container.addView(view);
                }

            }
        });
        //dislike??????
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(mContext, "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(mContext, "????????????????????????", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(mContext, "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(mContext, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(mContext, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(mContext, "????????????", Toast.LENGTH_LONG);
            }
        });
    }
    /**
     * ??????????????????????????????????????????????????????
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle){
        if (customStyle) {
            //????????????????????????????????????"????????????????????????"????????????????????????startPersonalizePromptActivity??????????????????
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(this, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    TToast.show(mContext, "?????? " + filterWord.getName());
                    //???????????????????????????????????????????????????
                    express_container.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(mContext, "?????????????????????????????????");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //???????????????????????????dislike????????????
        ad.setDislikeCallback(ReceiveFileActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }



            @Override
            public void onSelected(int position, String value, boolean enforce) {
                TToast.show(ReceiveFileActivity.this, "?????? " + value);
                express_container.removeAllViews();
                //???????????????????????????????????????????????????
                if (enforce) {
                    TToast.show(ReceiveFileActivity.this, "??????Banner ?????????sdk?????????view?????????");
                }
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "???????????? ");
            }




        });
//            //???????????????????????????dislike????????????

    }

    public static int getNum(int endNum){
    if(endNum > 0){
        Random random = new Random();
        return random.nextInt(endNum);
    }
    return 0;
}

    private void initView() {
        //??????mac??????????????????
        macAddress = getMacAddress();
        int num = getNum(100);
        Log.i("?????????",num+"");
        String macAddress = getMacAddress();
        String substring = macAddress.substring(3);
        Log.i("macAddress111",substring);

        ImageView Img = findViewById(R.id.im);
        TextView download = findViewById(R.id.download);
        ImageView zxing_img_back = findViewById(R.id.zxing_img_back);
        QRcode qrcode = new QRcode();
        Bitmap bitmap= qrcode.qrcode(substring+num);
        Img.setImageBitmap(bitmap);
        setTitle("????????????");
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("??????????????????");
        progressDialog.setMax(100);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiveFileActivity.this,DownloadActivity.class);
                startActivity(intent);
            }
        });
        zxing_img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiServerService != null) {
            wifiServerService.setProgressChangListener(null);
            unbindService(serviceConnection);
        }
        unregisterReceiver(broadcastReceiver);
        stopService(new Intent(this, WifiServerService.class));
        removeGroup();
    }

    //????????????
    private void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
               // log("removeGroup onSuccess");
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
               // log("removeGroup onFailure");
                showToast("onFailure");
            }
        });
    }


    private void bindService() {
        Intent intent = new Intent(ReceiveFileActivity.this, WifiServerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
    public String getMacAddress() {
        List<NetworkInterface> interfaces = null;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (networkInterface != null && TextUtils.isEmpty(networkInterface.getName()) == false) {
//                     Log.i("networkInterface.getInetAddresses()", networkInterface.getInetAddresses() + "");
//                    Log.i("networkInterface.getInterfaceAddresses()", networkInterface.getInterfaceAddresses() + "");
                    if ("wlan0".equalsIgnoreCase(networkInterface.getName())) {
                        byte[] macBytes = networkInterface.getHardwareAddress();
                        if (macBytes != null && macBytes.length > 0) {
                            StringBuilder str = new StringBuilder();
                            for (byte b : macBytes) {
                                str.append(String.format("%02X:", b));
                            }
                            if (str.length() > 0) {
                                str.deleteCharAt(str.length() - 1);
                            }
                            return str.toString();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

}