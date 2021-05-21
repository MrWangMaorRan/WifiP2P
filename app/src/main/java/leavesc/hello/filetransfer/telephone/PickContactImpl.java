package leavesc.hello.filetransfer.telephone;


public class PickContactImpl {

//    /**
//     * 批量查询号码是否可以导入
//     *
//     * @param mContext
//     * @param object
//     * @param onCallBackListener
//     */
//    public void search(Context mContext, Object object, OnCallBackListener onCallBackListener) {
//        HttpUtil.okHttpPostObjJson_2(mContext, ApiConstants.search, object, new CallBackUtil.CallBackString(mContext) {
//            @Override
//            public void onFailure(Call call, Exception e) {
//                LogUtils.i("PickContactImpl", "PickContactImpl search onFailure --> " + e.getMessage());
//                onCallBackListener.onSearchFailure("");
//            }
//
//            @Override
//            public void onResponse(String response) {
//                try {
//                    LogUtils.i("PickContactImpl", "PickContactImpl search onResponse ---> " + response.toString());
//                    if (!StringUtils.isNullOrBlank(response)) {
//                        ResponseCallAllBean responseCallAllBean = new Gson().fromJson(
//                                response, new TypeToken<ResponseCallAllBean>() {
//                                }.getType());
//                        if (responseCallAllBean.getCode() == 0) {
//                            String jsonStr = new Gson().toJson(responseCallAllBean.getData());
//                            if (!StringUtils.isNullOrBlank(jsonStr)) {
//                                // 可以导入的电话号码
//                                List<String> phoneList = new Gson().fromJson(
//                                        jsonStr, new TypeToken<List<String>>() {
//                                        }.getType());
//                                onCallBackListener.onSearchSuccess(phoneList);
//                            } else {
//                                onCallBackListener.onSearchSuccess(null);
//                            }
//                        } else {
//                            onCallBackListener.onSearchFailure(responseCallAllBean.getMessage());
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    /**
//     * 批量查询号码是否可以导入
//     *
//     * @param mContext
//     * @param object
//     * @param onCallBackListener
//     */
//    public void importContacts(Context mContext, Object object, OnCallBackListener onCallBackListener) {
//        HttpUtil.okHttpPostObjJson_2(mContext, ApiConstants.importContacts, object, new CallBackUtil.CallBackString(mContext) {
//            @Override
//            public void onFailure(Call call, Exception e) {
//                LogUtils.i("PickContactImpl", "PickContactImpl importContacts onFailure --> " + e.getMessage());
//                onCallBackListener.onImportContactsFailure("");
//            }
//
//            @Override
//            public void onResponse(String response) {
//                try {
//                    LogUtils.i("PickContactImp", "PickContactImpl importContacts onResponse ---> " + response.toString());
//                    if (!StringUtils.isNullOrBlank(response)) {
//                        ResponseCallAllBean responseCallAllBean = new Gson().fromJson(
//                                response, new TypeToken<ResponseCallAllBean>() {
//                                }.getType());
//                        if (responseCallAllBean.getCode() == 0) {
//                            String jsonStr = new Gson().toJson(responseCallAllBean.getData());
//                            onCallBackListener.onImportContactsSuccess();
//                        } else {
//                            onCallBackListener.onImportContactsFailure(responseCallAllBean.getMessage());
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }


//    public interface OnCallBackListener {
//        // 成功
//        void onSearchSuccess(List<String> phoneList);
//
//        // 失败
//        void onSearchFailure(String msg);
//
//        // 成功
//        void onImportContactsSuccess();
//
//        // 失败
//        void onImportContactsFailure(String msg);
//
//
//}
