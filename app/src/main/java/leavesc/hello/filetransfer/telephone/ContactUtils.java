package leavesc.hello.filetransfer.telephone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/6/21.
 */

public class ContactUtils {

    //获取所有联系人
    public static List<ContactBean> getAllContacts(Context context) {
        List<ContactBean> phoneDtos = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
        while (cursor.moveToNext()) {
           ContactBean contactBean = new ContactBean();
            contactBean.setPhone(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", ""));
            contactBean.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            phoneDtos.add(contactBean);
        }
        return phoneDtos;
    }

    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    public static CallLogBean getCallLog(Context mContext) {
        // 1.获得ContentResolver
        ContentResolver resolver = mContext.getContentResolver();
        // 2.利用ContentResolver的query方法查询通话记录数据库
        /**
         * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
         * @param projection 需要查询的字段
         * @param selection sql语句where之后的语句
         * @param selectionArgs ?占位符代表的数据
         * @param sortOrder 排序方式
         *
         */
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                new String[]{CallLog.Calls.CACHED_NAME// 通话记录的联系人
                        , CallLog.Calls.NUMBER// 通话记录的电话号码
                        , CallLog.Calls.DATE// 通话记录的日期
                        , CallLog.Calls.DURATION// 通话时长
                        , CallLog.Calls.TYPE}// 通话类型
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        // 3.通过Cursor获得数据
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        // 此处只需要第一条数据，所以不执行循环
//        while (cursor.moveToNext()) {
        cursor.moveToNext();
        String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
        String date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(dateLong));
        int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
        int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
        String typeString = "";
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                typeString = "打入";
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                typeString = "打出";
                break;
            case CallLog.Calls.MISSED_TYPE:
                typeString = "未接";
                break;
            default:
                break;
        }
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", (name == null) ? "未备注联系人" : name);
//            map.put("number", number);
//            map.put("date", date);
//            map.put("duration", (duration / 60) + "分钟");
//            map.put("type", typeString);
//            list.add(map);
//        }

        CallLogBean callLogBean = new CallLogBean();
        callLogBean.setName(name);
        callLogBean.setNumber(number);
        callLogBean.setData(date);
        callLogBean.setDuration(duration);
        callLogBean.setTypeString(typeString);

        return callLogBean;
    }
}
