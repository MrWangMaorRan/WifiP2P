package leavesc.hello.filetransfer.telephone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import leavesc.hello.filetransfer.R;

public class CustomPSPViewAdapter extends RecyclerView.Adapter<CustomPSPViewAdapter.ViewHolder> {
    private Context mContext;
    private List<CustomPSPViewBean> list = new ArrayList<>();

    public CustomPSPViewAdapter(Context mContext, List<CustomPSPViewBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_custom_psp_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       CustomPSPViewBean bean = list.get(position);
        holder.tvText.setText(bean.getText());
        if (bean.isChecked()) {
            holder.ivCheckedImg.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheckedImg.setVisibility(View.INVISIBLE);
        }
        // 点击事件
        holder.llListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CustomPSPViewBean> clickList = new ArrayList<>();
                if (bean.isRadio()) {
                    for (int i = 0; i < list.size(); i++){
                        list.get(i).setChecked(false);
                    }
                    // 单选点击必选中
                    bean.setChecked(true);
                } else {
                    // 全部 地区
                    bean.setChecked(!bean.isChecked());
                    // 多选处理
                    if (position == 0) {
                        if (bean.isChecked()) {
                            for (int i = 1; i < list.size(); i++) {
                                list.get(i).setChecked(true);
                            }
                        } else {
                            for (int i = 1; i < list.size(); i++) {
                                list.get(i).setChecked(false);
                            }
                        }
                    } else {
                        boolean hasNoChoose = false;
                        for (int i = 1; i < list.size(); i++) {
                            if (!list.get(i).isChecked()) {
                                hasNoChoose = true;
                            }
                        }
                        if (hasNoChoose) {
                            list.get(0).setChecked(false);
                        } else {
                            list.get(0).setChecked(true);
                        }
                    }
                }
                // 传值
                if (mListener != null) {
                    for (CustomPSPViewBean customPSPViewBean : list) {
                        if (customPSPViewBean.isChecked()) {
                            clickList.add(customPSPViewBean);
                        }
                    }
                    mListener.onItemClick(clickList);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text)
        TextView tvText;
        @BindView(R.id.iv_checked_img)
        ImageView ivCheckedImg;
        @BindView(R.id.ll_list_item)
        LinearLayout llListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


    /**
     * item 点击处理事件
     */
    public CustomPSPViewAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(List<CustomPSPViewBean> clickList);
    }

    public void setOnItemClickListener(CustomPSPViewAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
    }
}
