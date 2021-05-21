package leavesc.hello.filetransfer.telephone;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import leavesc.hello.filetransfer.R;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;

public class PickContactPresenter {
    private String TAG = "PickContactPresenter";
    private Context mContext;
    private PickContactImpl pickContactImpl;
    private List<ContactBean> contactBeanList;
    private List<ContactBean> checkedContactList;
    private List<ContactBean> notAddContactList;
    private List<ContactBean> showContactList;
    private ContactAdapter mAdapter;
    private IPickContact iPickContact;
    private String[] contactSorting;
    private List<CustomPSPViewBean> customPSPViewBeanList;
    private String searchStr;
    private boolean isAll = true; // 是否为全部客户
    private boolean isCheckedAll;

    public PickContactPresenter(Context mContext, IPickContact iPickContact) {
        this.mContext = mContext;
        this.iPickContact = iPickContact;
        pickContactImpl = new PickContactImpl();
        contactBeanList = new ArrayList<>();
        checkedContactList = new ArrayList<>();
        showContactList = new ArrayList<>();
        notAddContactList = new ArrayList<>();
       // contactSorting = mContext.getResources().getStringArray(R.array.contact_screening); // 资源文件
        // 获取通讯录数据
        setContactsList();
    }

    // 获取本地的通讯录数据
    public void setContactsList() {
        contactBeanList = ContactUtils.getAllContacts(mContext);
        for (ContactBean contactBean : contactBeanList) {
            showContactList.add(contactBean);
        }
        // 批量查询号码是否可以导入
        //searchApi();
    }

    public void  initAdapter(IndexableLayout indexableLayout) {
        indexableLayout.setLayoutManager(new LinearLayoutManager(mContext));
//        indexableLayout.setLayoutManager(new GridLayoutManager(this, 3));
        // setAdapter
        mAdapter = new ContactAdapter(mContext);
        indexableLayout.setAdapter(mAdapter);
        // set Datas
        mAdapter.setDatas(showContactList);
        // set Material Design OverlayView
        indexableLayout.setOverlayStyle_MaterialDesign(Color.BLUE);
        // 全字母排序。  排序规则设置为：每个字母都会进行比较排序；速度较慢
        indexableLayout.setCompareMode(IndexableLayout.MODE_ALL_LETTERS);

        // set Listener
        mAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<ContactBean>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, ContactBean entity) {
                if (originalPosition >= 0) {
                    // 如果已经添加过的，选中无效
                    if (entity.isAdd() || !StringUtils.isPhoneType(entity.getPhone())) {
                        return;
                    }

//                    ToastUtil.showShort(PickContactActivity.this, "选中:" + entity.getName()
//                            + "  当前位置:" + currentPosition + "  原始所在数组位置:" + originalPosition);
                    entity.setChecked(!entity.isChecked());
                    mAdapter.notifyDataSetChanged();
                    // 选中的集合
                    if (entity.isChecked()) {
                        if (!checkedContactList.contains(entity)) {
                            checkedContactList.add(entity);
                        }
                    } else {
                        if (checkedContactList.contains(entity)) {
                            checkedContactList.remove(entity);
                        }
                    }
                    iPickContact.checkedResult();
                    // 单个选择达到全选状态
                    isCheckedAll();
                    //LogUtils.i(TAG, "checkedContactList size = " + checkedContactList.size());
                } else {
//                    ToastUtil.showShort(PickContactActivity.this, "选中Header/Footer:" + entity.getName() + "  当前位置:" + currentPosition);
                }
            }
        });

//        mAdapter.setOnItemTitleClickListener(new IndexableAdapter.OnItemTitleClickListener() {
//            @Override
//            public void onItemClick(View v, int currentPosition, String indexTitle) {
//                ToastUtil.showShort(PickContactActivity.this, "选中:" + indexTitle + "  当前位置:" + currentPosition);
//            }
//        });
    }

//    // 查询是否可用
//    public void searchApi() {
//        List<String> list = new ArrayList<>();
//        for (ContactBean contactBean : contactBeanList) {
//            list.add(contactBean.getPhone());
//        }
//        SearchPhoneDto searchPhoneDto = new SearchPhoneDto();
//        searchPhoneDto.setPhones(StringUtils.listToString(list));
//        pickContactImpl.search(mContext, searchPhoneDto, this);
//    }
//    // SD卡文件查询是否可用
//    public void searchApi_two(List<XlseBean> xlseBeanList) {
//        List<String> list = new ArrayList<>();
//        for (XlseBean contactBean : xlseBeanList) {
//            list.add(contactBean.getPhonenum());
//        }
//        SearchPhoneDto searchPhoneDto = new SearchPhoneDto();
//        searchPhoneDto.setPhones(StringUtils.listToString(list));
//        pickContactImpl.search(mContext, searchPhoneDto, this);
//    }

    // 查询是否可用
    public List<ContactBean> importContacts() {
        ImportAddressBookDto importAddressBookDto = new ImportAddressBookDto();
        List<ContactBean> canImportList = new ArrayList<>();
        for (ContactBean contactBean : checkedContactList) {
            if (!contactBean.isAdd()) {
                canImportList.add(contactBean);
            }
        }
        importAddressBookDto.setPhoneList(canImportList);
        List<ContactBean> phonename = importAddressBookDto.getPhoneList();
        Log.i("importAddressBookDto111",importAddressBookDto.getPhoneList().toString());
        Log.i("importAddressBookDto",importAddressBookDto+"");
       // pickContactImpl.importContacts(mContext, importAddressBookDto, this);
        return phonename;
    }

    /**
     * 添加的背景
     *
     * @param llAdd
     */
    public void setAddBtnBg(LinearLayout llAdd) {
        if (checkedContactList.size() > 0) {
            llAdd.setEnabled(true);
            llAdd.setBackgroundResource(R.drawable.btn_corner_38dp_blue);
        } else {
            llAdd.setEnabled(false);
            llAdd.setBackgroundResource(R.drawable.bg_corner_38dp_gray);
        }
    }

    /**
     * 确定添加
     */
    public void setCheckedContactList() {
        checkedContactList.clear();
        for (ContactBean contactBean : showContactList) {
            if (contactBean.isChecked()) {
                checkedContactList.add(contactBean);
            }
        }
        //LogUtils.i(TAG, "checkedContactList = " + checkedContactList.toString());
    }

    /**
     * 筛选弹框
     *
     * @param view 弹框弹出的参照view
     */
    public void showPartShadow(final View view) {
        CustomPartShadowPopupViewList popupView = (CustomPartShadowPopupViewList) new XPopup.Builder(mContext)
                .atView(view)
//                    .isClickThrough(true)
//                    .dismissOnTouchOutside(false)
//                    .isCenterHorizontal(true)
                .autoOpenSoftInput(true)
//                    .offsetX(200)
//                .dismissOnTouchOutside(false)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onDismiss() {

                    }
                })
                .asCustom(new CustomPartShadowPopupViewList(mContext, getPSPDialogData()))
                .show();
        setOnClickResultListener(popupView);
    }

    // 数据
    private List<CustomPSPViewBean> getPSPDialogData() {
        if (customPSPViewBeanList == null) {
            customPSPViewBeanList = new ArrayList<>();
            for (int i = 0; i < contactSorting.length; i++) {
                CustomPSPViewBean bean = new CustomPSPViewBean();
                bean.setChecked(false);
                bean.setText(contactSorting[i]);
                bean.setRadio(true);
                bean.setPosition(i);
                customPSPViewBeanList.add(bean);
            }
        }
        return customPSPViewBeanList;
    }

    // 把所有选择都设置为 false
    private void dataSetFalse() {
        for (CustomPSPViewBean bean : customPSPViewBeanList) {
            bean.setChecked(false);
        }
    }

    // 点击事件
    private void setOnClickResultListener(CustomPartShadowPopupViewList popupView) {
        popupView.setOnItemClickListener(new CustomPartShadowPopupViewList.OnItemClickListener() {
            @Override
            public void onItemClick(List<Integer> position) {
                if (position != null && position.size() > 0) {
                    dataSetFalse();
                    customPSPViewBeanList.get(position.get(0)).setChecked(true);
                    iPickContact.screeningResult(customPSPViewBeanList.get(position.get(0)).getText());
                    switch (position.get(0)) {
                        case 0: // 全部客户
                            isAll = true;
                            break;
                        case 1: // 未添加客户
                            isAll = false;
                            break;
                    }
                    setShowData();
                    mAdapter.setDatas(showContactList);
                }
            }
        });
    }

    // 全选/全消
    public void checkedAll() {
        isCheckedAll = !isCheckedAll;
        if (isCheckedAll) {
            for (ContactBean contactBean : showContactList) {
                contactBean.setChecked(true);
            }
            setCheckedContactList();
        } else {
            for (ContactBean contactBean : showContactList) {
                contactBean.setChecked(false);
            }
            checkedContactList.clear();
        }
        mAdapter.notifyDataSetChanged();
        iPickContact.checkedResult();
        iPickContact.checkedAllResult(isCheckedAll);
    }

    /**
     * 输入监听
     *
     * @param etSearch
     */
    public void textChangeListener(EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               // LogUtils.i(TAG, "input = " + editable.toString());
                searchStr = editable.toString();
                setShowData();
                mAdapter.setDatas(showContactList);
            }
        });
    }

    /***
     * 搜索得到的数据
     * @return
     */
    private void setShowData() {
        if (showContactList != null) {
            showContactList.clear();
        } else {
            showContactList = new ArrayList<>();
        }
        if (!StringUtils.isNullOrBlank(searchStr)) {
            if (isAll) {
                for (ContactBean item : contactBeanList) {
                    if (item.getPinyin().startsWith(searchStr) || item.getName().contains(searchStr)
                            || (!StringUtils.isNullOrBlank(item.getPhone()) && item.getPhone().contains(searchStr))) {
                        showContactList.add(item);
                    }
                }
            } else {
                for (ContactBean item : notAddContactList) {
                    if (item.getPinyin().startsWith(searchStr) || item.getName().contains(searchStr)
                            || (!StringUtils.isNullOrBlank(item.getPhone()) && item.getPhone().contains(searchStr))) {
                        showContactList.add(item);
                    }
                }
            }
        } else {
            if (isAll) {
                for (ContactBean contactBean : contactBeanList) {
                    showContactList.add(contactBean);
                }
            } else {
                for (ContactBean contactBean : notAddContactList) {
                    showContactList.add(contactBean);
                }
            }
        }
        // 设置筛选/搜索后的选中/非选中状态
        for (ContactBean contactBean : showContactList) {
            if (checkedContactList != null && checkedContactList.size() > 0 && checkedContactList.contains(contactBean)) {
                contactBean.setChecked(true);
            } else {
                contactBean.setChecked(false);
            }
        }
        // 重新设置选中的值
        if(checkedContactList != null) {
            checkedContactList.clear();
        }else{
            checkedContactList = new ArrayList<>();
        }
        setCheckedContactList();
        isCheckedAll();
        iPickContact.checkedResult();
      //  LogUtils.i(TAG, "checkedContactList size = " + checkedContactList.size());
    }

    /***
     * 搜索得到的数据
     * @return
     */
    private void setNotAddData() {
        for (ContactBean item : contactBeanList) {
            if (!item.isAdd()) {
                notAddContactList.add(item);
            }
        }
    }

    /**
     * 判断是否全选
     */
    private void isCheckedAll() {
        if (checkedContactList.size() > 0 && checkedContactList.size() == showContactList.size()) {
            isCheckedAll = true;
        } else {
            isCheckedAll = false;
        }
        iPickContact.checkedAllResult(isCheckedAll);
    }

//    @Override
//    public void onSearchSuccess(List<String> phoneList) {
//        // phoneList 可以导入的电话号码，即为未导入的号码
//        if (phoneList != null) {
//            for (ContactBean contactBean : contactBeanList) {
//                if (phoneList.contains(contactBean.getPhone())) {
//                    contactBean.setAdd(false);
//                } else {
//                    contactBean.setAdd(true);
//                }
//            }
//            mAdapter.setDatas(contactBeanList);
//            setNotAddData();
//        }
//    }
//
//    @Override
//    public void onSearchFailure(String msg) {
//
//    }
//
//    @Override
//    public void onImportContactsSuccess() {
//        ((Activity) mContext).setResult(Activity.RESULT_OK);
//       // ToastUtils.showToast(mContext, "添加成功");
//        ((Activity) mContext).finish();
//    }
//
//    @Override
//    public void onImportContactsFailure(String msg) {
//       // ToastUtils.showToast(mContext, "添加失败");
//    }
}
