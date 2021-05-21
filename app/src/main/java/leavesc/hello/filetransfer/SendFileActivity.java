package leavesc.hello.filetransfer;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.language.LanguageConfig;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.LongFunction;

import leavesc.hello.filetransfer.adapter.DeviceAdapter;
import leavesc.hello.filetransfer.broadcast.DirectBroadcastReceiver;
import leavesc.hello.filetransfer.callback.DirectActionListener;
import leavesc.hello.filetransfer.common.LoadingDialog;
import leavesc.hello.filetransfer.model.FileTransfer;
import leavesc.hello.filetransfer.task.WifiClientTask;
import leavesc.hello.filetransfer.telephone.PermissinsUtils_one;
import leavesc.hello.filetransfer.telephone.PickContactActivityOne;
import leavesc.hello.filetransfer.util.GetRealPath;
import leavesc.hello.filetransfer.util.MetWorkUtils;
import leavesc.hello.filetransfer.util.PermissinsUtils;

/**
 * 作者：leavesC
 * 时间：2019/2/27 23:52
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
public class SendFileActivity extends BaseActivity {

    private static final String TAG = "SendFileActivity";

    private static final int CODE_CHOOSE_FILE = 100;

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private WifiP2pInfo wifiP2pInfo;

    private boolean wifiP2pEnabled = false;
    private int b=0;
    private DirectActionListener directActionListener = new DirectActionListener() {

        @Override
        public void wifiP2pEnabled(boolean enabled) {
            wifiP2pEnabled = enabled;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            dismissLoadingDialog();
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            b=1;
            Log.e(TAG, "onConnectionInfoAvailable");
            Log.e(TAG, "onConnectionInfoAvailable groupFormed: " + wifiP2pInfo.groupFormed);
            Log.e(TAG, "onConnectionInfoAvailable isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            Log.e(TAG, "onConnectionInfoAvailable getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            Log.e(TAG, "onPeersAvailable123456111 :" + wifiP2pDeviceList.size());
//            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
//                SendFileActivity.this.wifiP2pInfo = wifiP2pInfo;
//            }
            SendFileActivity.this.wifiP2pInfo = wifiP2pInfo;
        }

        @Override
        public void onDisconnection() {
            Log.e(TAG, "onDisconnection");

            showToast("处于非连接状态");
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            SendFileActivity.this.wifiP2pInfo = null;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable");
            Log.e(TAG, "DeviceName: " + wifiP2pDevice.deviceName);
            Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress);
            Log.e(TAG, "Status: " + wifiP2pDevice.status);
        }

        private int a = 0;

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList1) {
            Log.e(TAG, "onPeersAvailable :" + wifiP2pDeviceList1.size());
            SendFileActivity.this.wifiP2pDeviceList.clear();
            SendFileActivity.this.wifiP2pDeviceList.addAll(wifiP2pDeviceList1);
            deviceAdapter.notifyDataSetChanged();
            Intent intent = getIntent();
            String result = intent.getStringExtra("result");
            if (result == null) {
                Toast.makeText(SendFileActivity.this, "请扫描正确二维码", Toast.LENGTH_LONG).show();
                finish();
            } else {
                for (int i = 0; i < SendFileActivity.this.wifiP2pDeviceList.size(); i++) {
                    String deviceName = wifiP2pDeviceList.get(i).deviceName;
                    String deviceAddress1 = wifiP2pDeviceList.get(i).deviceAddress;
                    Log.i("deviceName1", deviceName);
                    Log.i("deviceAddress1", deviceAddress1);
                    String substring1 = deviceAddress1.substring(3);
                    String substring = result.substring(14);
                    String s = substring1 + substring;
                    Log.i("deviceAddress22", s);
                    Log.i("homeAddress", result);
                    if (deviceName != null && s.equalsIgnoreCase(result)) {

                        Log.i("aaaaaaaa", a + "");
                        if (a == 0) {
                            a = 1;
                            connect(deviceAddress1);
                        } else {
                            Log.i("连接上了", "连接上了");
                        }
                    }
                }
                loadingDialog.cancel();
            }
        }

        @Override
        public void onChannelDisconnected() {
            Log.e(TAG, "onChannelDisconnected");
        }

    };

    private List<WifiP2pDevice> wifiP2pDeviceList;

    private DeviceAdapter deviceAdapter;



    private LoadingDialog loadingDialog;

    private BroadcastReceiver broadcastReceiver;

    private WifiP2pDevice mWifiP2pDevice;

    private RecyclerView rv_deviceList;


    private LinearLayoutManager linearLayoutManager;
    private LinearLayout note;
    private LinearLayout photo;
    private LinearLayout calendar;
    private LinearLayout document;
    private LinearLayout telephone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        PermissinsUtils.getPermission(this);
        //不重复操作
        if (b!=0){
            disconnect();
            initView();
            initEvent();
            Seek();

        }else {
            initView();
            initEvent();
            Seek();
        }
        initCliener();
        initTelePhone();
    }

    private void initTelePhone() {
        Intent intent = getIntent();
        String phonename = intent.getStringExtra("phonename");
        if (phonename!=null){
            Log.i("phonename",phonename);
        }
    }



    private void initCliener() {
        //跳转相册
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToChose();
            }
        });
        //跳转通讯录
        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissinsUtils_one.getPermission(SendFileActivity.this);
                Intent intent = new Intent(SendFileActivity.this, PickContactActivityOne.class);
                intent.putExtra("telephone","10");
                startActivityForResult(intent,10);
            }
        });
    }

    private void initEvent() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        Log.e(TAG, "onPeersAvailable123456 :" + wifiP2pDeviceList.size());
    }
        //扫描附近可用设备
    private void Seek() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show("正在验证设备信息", true, false);
        wifiP2pDeviceList.clear();
        deviceAdapter.notifyDataSetChanged();
        //搜寻附近带有 Wi-Fi P2P 的设备
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Log.i("发现成功","发现成功");

            }

            @Override
            public void onFailure(int reasonCode) {
                showToast("没有检测到设备，请重新扫描二维码");
                loadingDialog.cancel();
            }
        });

    }

    private void initView() {
        setTitle("发送文件");
        note = (LinearLayout) findViewById(R.id.note);
        photo = (LinearLayout) findViewById(R.id.photo);
        calendar = (LinearLayout) findViewById(R.id.calendar);
        document = (LinearLayout) findViewById(R.id.document);
        telephone = findViewById(R.id.address);
        rv_deviceList = findViewById(R.id.rv_deviceList);
        wifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(wifiP2pDeviceList);

        deviceAdapter.setClickListener(new DeviceAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {

                mWifiP2pDevice = wifiP2pDeviceList.get(position);
                Log.i("mWifiP2pDevice", wifiP2pDeviceList.get(position) + "");
                showToast(mWifiP2pDevice.deviceName);

            }
        });
        rv_deviceList.setAdapter(deviceAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_deviceList.setLayoutManager(linearLayoutManager);
        deviceAdapter.notifyDataSetChanged();
        Log.e(TAG, "onPeersAvailable123456111 :" + wifiP2pDeviceList.size());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Data(data);
            }
        }else if (requestCode==10&&resultCode==20){
            getIntenty(data);
        }
    }

    private void getIntenty(Intent data) {
        Bundle extras = data.getExtras();
        Serializable telephone = getIntent().getSerializableExtra("telephone");
        Log.i("telephone","收到了");

    }

    private void Data(Intent data){
        String localIpAddress = MetWorkUtils.getLocalIpAddress(this);
        Log.d(TAG, "localIpAddress==: " + localIpAddress);
        // onResult Callback
        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
        for (int i = 0; i < selectList.size(); i++) {
            LocalMedia localMedia = selectList.get(i);
            String path = localMedia.getPath();
            Log.d(TAG, "发送文件路径path: " + path);
            Uri uri = Uri.parse(path);
            Log.d(TAG, "发送文件路径URI: " + uri);
            String banben = Build.VERSION.RELEASE;
            Log.d(TAG + "123", "版本号" + banben);
            if (banben.equals("9")) {
                File file = new File(path);
                if (file.exists()) {
                    FileTransfer fileTransfer = new FileTransfer(file.getPath(), file.length());
                    Log.d(TAG, "路径获取成功: " + "fileTransfer");
//                            new WifiClientTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                    String hostAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                    Log.d(TAG, "hostAddress: " + hostAddress);
                    new WifiClientTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                    Log.d(TAG, "开始传输: " + "11");
                } else {
                    Log.d(TAG, "路径获取失败: " + "11");
                }
            } else {
                String realPathFromUri = GetRealPath.getPathFromUri(this, uri);
                Log.d(TAG, "发送文件路径: " + realPathFromUri);
                File file = new File(realPathFromUri);
                if (file.exists()) {
                    FileTransfer fileTransfer = new FileTransfer(file.getPath(), file.length());
//                            new WifiClientTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                    new WifiClientTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                } else {
                    Log.d(TAG, "路径获取失败: " + "11");
                }
            }
//                    String realPathFromUri = getRealPathFromUri(this, uri);
        }
    }
    //连接指定设备
    private void connect(String deviceAddress) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null) {
            config.deviceAddress = deviceAddress;
            //   Log.i("deviceAddress",mWifiP2pDevice.deviceAddress+"");
            config.wps.setup = WpsInfo.PBC;
            showLoadingDialog("正在连接 ");
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "connect onSuccess");
                    showToast("连接成功");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("连接失败 " + reason);
                    dismissLoadingDialog();
                }
            });
        }
    }
    //返回键监听
    public boolean onKeyDown(int keyCode, KeyEvent event) {
// TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
                //断开连接
                disconnect();
        }
        return super.onKeyDown(keyCode, event);
    }

    //断开连接
    private void disconnect() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "disconnect onFailure:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "disconnect onSuccess");
                Toast.makeText(SendFileActivity.this,"连接断开",Toast.LENGTH_LONG).show();
            }
        });
    }





    private void navToChose() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .setLanguage(LanguageConfig.CHINESE)
                .isAndroidQTransform(true) // Android Q版本下是否需要拷贝文件至应用沙盒内
                .isEnableCrop(false) // 是否开启裁剪
                .freeStyleCropEnabled(false) // 裁剪框是否可拖拽
                .rotateEnabled(false) // 裁剪是否可旋转图片
                .scaleEnabled(false) // 裁剪是否可放大缩小图片
                .isDragFrame(false) // 是否可拖动裁剪框
                .isMultipleSkipCrop(false) // 多图裁剪是否支持跳过
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // 固定屏幕方向为竖屏
                .isCamera(true)
                .isCompress(true)
                .compressQuality(10) // 图片压缩后输出质量 0~ 100
                .minimumCompressSize(500) // 小于多少KB的图片不压缩
                .forResult(1);
    }

}