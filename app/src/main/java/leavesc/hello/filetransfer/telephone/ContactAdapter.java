package leavesc.hello.filetransfer.telephone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import leavesc.hello.filetransfer.R;
import me.yokeyword.indexablerv.IndexableAdapter;


/**
 * Created by YoKey on 16/10/8.
 */
public class ContactAdapter extends IndexableAdapter<ContactBean> {
    private LayoutInflater mInflater;

    public ContactAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public ContactAdapter() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_index_contact, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_contact, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, ContactBean entity) {
        ContentVH vh = (ContentVH) holder;
        if (!StringUtils.isNullOrBlank(entity.getName())) {
            vh.tvName.setText(entity.getName());
        }
        if (!StringUtils.isNullOrBlank(entity.getPhone())) {
            vh.tvMobile.setText(entity.getPhone());
        }
        if (entity.isAdd()) {
            vh.tvIsAdd.setVisibility(View.VISIBLE);
            vh.ivChecked.setVisibility(View.GONE);
        } else {
            if (!StringUtils.isNullOrBlank(entity.getPhone())) {
                if (!StringUtils.isPhoneType(entity.getPhone())) {
                    vh.tvIsAdd.setVisibility(View.GONE);
                    vh.ivChecked.setVisibility(View.GONE);
                } else {
                    vh.tvIsAdd.setVisibility(View.GONE);
                    vh.ivChecked.setVisibility(View.VISIBLE);
                    if (entity.isChecked()) {
                        vh.ivChecked.setImageResource(R.mipmap.ic_checked_in);
                    } else {
                        vh.ivChecked.setImageResource(R.mipmap.ic_checked_out);
                    }
                }
            } else {
                vh.tvIsAdd.setVisibility(View.GONE);
                vh.ivChecked.setVisibility(View.GONE);
            }
        }
    }

    private class IndexVH extends RecyclerView.ViewHolder {
        TextView tv;

        public IndexVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }

    private class ContentVH extends RecyclerView.ViewHolder {
        TextView tvName, tvMobile, tvIsAdd;
        ImageView ivChecked;

        public ContentVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvMobile = (TextView) itemView.findViewById(R.id.tv_mobile);
            tvIsAdd = (TextView) itemView.findViewById(R.id.tv_is_add);
            ivChecked = (ImageView) itemView.findViewById(R.id.iv_checked);
        }
    }
}
