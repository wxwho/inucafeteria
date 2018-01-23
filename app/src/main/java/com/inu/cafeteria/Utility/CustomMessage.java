package com.inu.cafeteria.Utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.inu.cafeteria.R;


/**
 * Created by GaYoon on 2017-08-11.
 */

public class CustomMessage extends Dialog{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_custom_popup);

        setLayout();
        setTitle(mTitle);
        setContent(mContent);
        setClickListener(mRightClickListener);
    }

    public CustomMessage(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }

    public CustomMessage(Context context , String title ,
                         View.OnClickListener singleListener) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mRightClickListener = singleListener;
    }

    public CustomMessage(Context context, String title, String content,
                         View.OnClickListener rightListener) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.mRightClickListener = rightListener;
    }

    private void setTitle(String title){
        mTitleView.setText(title);
    }

    private void setContent(String content){
        mContentView.setText(content);
    }

    private void setClickListener(View.OnClickListener right){
        if(right!=null){
            mRightButton.setOnClickListener(right);
        }else if(right==null){
        }else {

        }
    }

    //    private TextView mTitleView;
    private TextView mContentView;
    private TextView mTitleView;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    //    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    /*
     * Layout
     */
    private void setLayout(){
        mTitleView = (TextView) findViewById(R.id.acp_tv_title);
        mContentView = (TextView) findViewById(R.id.acp_tv_content);
//        mLeftButton = (Button) findViewById(R.id.acd_btn_cancel);
        mRightButton = (Button) findViewById(R.id.acp_btn_confirm);
    }
}
