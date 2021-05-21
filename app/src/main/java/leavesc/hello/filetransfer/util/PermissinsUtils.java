package leavesc.hello.filetransfer.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/29.
 */

public class PermissinsUtils {

    /**
     * 是否允许
     *
     * @param activity
     * @param mPermissions
     * @return
     */
    public static boolean hasPermissions(Activity activity, String[] mPermissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        boolean isAllPermissions = false;
        int permissionsCount = 0;

        for (int i = 0; i < mPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, mPermissions[i]) == PackageManager.PERMISSION_GRANTED) {
                permissionsCount++;
            }
        }

        if (permissionsCount == mPermissions.length) {
            isAllPermissions = true;
        } else {
            isAllPermissions = false;
        }

        return isAllPermissions;
    }

    /**
     * 是否允许
     *
     * @param activity
     * @param mPermissions
     * @return
     */
    public static boolean hasPermissions(Activity activity, String mPermissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        boolean isAllPermissions = false;
        if (ContextCompat.checkSelfPermission(activity, mPermissions) == PackageManager.PERMISSION_GRANTED) {
            isAllPermissions = true;
        } else {
            isAllPermissions = false;
        }

        return isAllPermissions;
    }

    /**
     * 请求权限
     *
     * @param activity
     * @param mPermissions
     * @param requestCode
     */
    public static void requestPermissions(Activity activity, String[] mPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        ArrayList<String> requestPermissions = new ArrayList<String>();
        for (int i = 0; i < mPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(mPermissions[i]);
            }
        }
        if (requestPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity, requestPermissions.toArray(new String[requestPermissions.size()]), requestCode);
        }
    }

    /**
     * 请求权限
     *
     * @param activity
     * @param mPermissions
     * @param requestCode
     */
    public static void requestPermissions(Activity activity, String mPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        if (ContextCompat.checkSelfPermission(activity, mPermissions) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{mPermissions}, requestCode);
        }
    }

    /**
     * 是否获取到权限
     *
     * @param permissions
     * @param grantResults
     * @return
     */
    public static boolean isPermissions(String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        boolean isPermissions = false;
        int permissionsCount = 0;

        if(permissions != null) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults != null && grantResults.length > i
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionsCount++;
                }
            }
        }

        if (permissionsCount == permissions.length) {
            isPermissions = true;
        } else {
            isPermissions = false;
        }

        return isPermissions;
    }


    /**
     * 判断权限
     *
     * @param context
     * @return
     */
    public static boolean checkCallingOrSelfPermission(Context context, String permission) {
        return checkCallingOrSelfPermissionsUtil(context, new String[]{permission});
    }

    public static boolean checkCallingOrSelfPermission(Context context, String[] permissions) {
        return checkCallingOrSelfPermissionsUtil(context, permissions);
    }

    private static boolean checkCallingOrSelfPermissionsUtil(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    // 只要有一个权限没有被授予, 则直接返回 false
                    //Log.e("err","权限"+permission+"没有授权");
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 获取权限
     */
    public static void getPermission(Context mContext) {
        String[] permission = new String[]{

                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,

                Manifest.permission.WAKE_LOCK,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,

                Manifest.permission.KILL_BACKGROUND_PROCESSES,
                Manifest.permission.SET_WALLPAPER,
                // 打印日志用到
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

                Manifest.permission.CALL_PHONE,
                Manifest.permission.VIBRATE,
                Manifest.permission.FOREGROUND_SERVICE,
                //通话记录
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG
        };

        if (!PermissinsUtils.hasPermissions((Activity) mContext, permission)) {
            PermissinsUtils.requestPermissions((Activity) mContext, permission, 0xaa);
        }
    }
}
