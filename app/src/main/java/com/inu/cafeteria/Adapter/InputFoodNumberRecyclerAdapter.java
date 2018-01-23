package com.inu.cafeteria.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.inu.cafeteria.R;

/**
 * Created by ksj on 2017. 8. 8..
 * Now not using header, footer by ksj on 2017. 8. 8..
 */

public class InputFoodNumberRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_FOOTER = 3;

    Context context;
    private int count;
    String[] loadData;

    public InputFoodNumberRecyclerAdapter(Context context, int count, String[] loadData) {
        this.context = context;
        this.count = count;
        this.loadData = loadData;
        Log.i("loadData", this.loadData[0] + " " + this.loadData[1] + " " + this.loadData[2]);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        EditText editFoodNumber;

        public ViewHolder(View view) {
            super(view);
            editFoodNumber = (EditText)view.findViewById(R.id.irifn_edit_input_food_number);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_input_food_number, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        } else if (holder instanceof FooterViewHolder) {
            //뷰타입이 푸터 생성하기 버튼일 경우
            FooterViewHolder footerHolder = (FooterViewHolder) holder;

        }  else if (holder instanceof ViewHolder) {
            //뷰 타입이 아이템일 경우
            ViewHolder itemHolder = (ViewHolder) holder;
            if(position != 0) {
                ((ViewHolder) holder).editFoodNumber.setHint("");
            }
            ((ViewHolder) holder).editFoodNumber.setText(loadData[position]);
            ((ViewHolder) holder).editFoodNumber.requestFocus();
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == count) {
            return TYPE_FOOTER;
        } else if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void addList() {
        count++;
    }
}
