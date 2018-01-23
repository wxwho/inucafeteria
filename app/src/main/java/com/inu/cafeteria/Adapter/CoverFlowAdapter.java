package com.inu.cafeteria.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.img.coverflow.widget.ICoverFlowAdapter;
import com.inu.cafeteria.Model.CoverflowModel;
import com.inu.cafeteria.R;
import com.inu.cafeteria.Utility.MyBitmapImageViewTarget;

import java.util.List;

public class CoverFlowAdapter implements ICoverFlowAdapter {

    private List<CoverflowModel> mArray;
    private Context context;
    private TextView textCode;


    public CoverFlowAdapter(Context context, List<CoverflowModel> mArray, TextView textCode) {
        this.context = context;
        this.mArray = mArray;
        this.textCode = textCode;
    }

    @Override
    public int getCount() {
        return mArray == null ? 0 : mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(context, R.layout.activity_coverflow_item_view, null);
            holder.iv_code = (ImageView) convertView.findViewById(R.id.aciv_image_code);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        convertView.setTag(holder);

        return convertView;
    }


    @Override
    public void getData(View view, int position) {
        Holder holder = (Holder) view.getTag();

        CoverflowModel coverflowmodel = mArray.get(position);

        // Glide : 이미지 로딩 라이브러리
        // app gradle에 다음 소스 추가함
        // compile 'com.github.bumptech.glide:glide:3.7.0'
        Glide.with(context)
                .load(coverflowmodel.getImg())
                .asBitmap()
                .centerCrop()
                .fitCenter()
                .into(new MyBitmapImageViewTarget(holder.iv_code));

    }

    public static class Holder {
        ImageView iv_code;
    }

}