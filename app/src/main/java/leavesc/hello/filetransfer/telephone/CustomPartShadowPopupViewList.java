package leavesc.hello.filetransfer.telephone;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lxj.xpopup.impl.PartShadowPopupView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import leavesc.hello.filetransfer.R;


/**
 * Description: 自定义局部阴影弹窗
 * Create by dance, at 2018/12/21
 */
public class CustomPartShadowPopupViewList extends PartShadowPopupView {

    @BindView(R.id.xrv_list)
    XRecyclerView xrvList;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.rl_bottom_btn_layout)
    RelativeLayout rlBottomBtnLayout;

    private List<CustomPSPViewBean> list = new ArrayList<>();
    private Context mContext;
    private CustomPSPViewAdapter customPSPViewAdapter;
    private List<Integer> positions = new ArrayList<>();

    public CustomPartShadowPopupViewList(@NonNull Context context, List<CustomPSPViewBean> list) {
        super(context);
        mContext = context;
        this.list = list;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_custom_part_shadow_popup_list;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        xrvList.setLayoutManager(layoutManager);
        customPSPViewAdapter = new CustomPSPViewAdapter(mContext, list);
        xrvList.setAdapter(customPSPViewAdapter);
        xrvList.setPullRefreshEnabled(false);
        xrvList.setLoadingMoreEnabled(false);

        // 单选去掉底部
        if (list != null && list.size() > 0 && list.get(0).isRadio()) {
            rlBottomBtnLayout.setVisibility(View.GONE);
        } else {
            for (CustomPSPViewBean bean : list) {
                if (bean.isChecked() && !positions.contains(bean.getPosition())) {
                    positions.add(bean.getPosition());
                }
            }
            rlBottomBtnLayout.setVisibility(View.VISIBLE);
        }

        // itemClick监听
        setMyOnItemClickListener();
    }

    @Override
    protected void onShow() {
        super.onShow();

    }

    @Override
    protected void onDismiss() {
        super.onDismiss();

    }


    @OnClick({R.id.btn_confirm, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (mListener != null) {
                    if (positions == null || positions.size() == 0) {
                        for (int i = 0; i < list.size(); i++) {
                            positions.add(i);
                        }
                    }
                    mListener.onItemClick(positions);
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    /**
     * itemClick监听
     */
    private void setMyOnItemClickListener() {
        customPSPViewAdapter.setOnItemClickListener(new CustomPSPViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(List<CustomPSPViewBean> clickList) {
                positions.clear();
                if (clickList != null && clickList.size() > 0) {
                    for (CustomPSPViewBean bean : clickList) {
                        if (!positions.contains(bean.getPosition())) {
                            positions.add(bean.getPosition());
                        }
                    }

                    if (clickList.get(0).isRadio()) {
                        if (mListener != null) {
                            mListener.onItemClick(positions);
                        }
                        dismiss();
                    }
                }
            }
        });
    }

    /**
     * item 点击处理事件
     */
    public OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(List<Integer> position);
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }


}
