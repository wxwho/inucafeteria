package com.inu.cafeteria.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.inu.cafeteria.Activity.WaitingFoodNumberActivity;
import com.inu.cafeteria.Fragment.CompleteFoodBoardFragment;
import com.inu.cafeteria.R;


/**
 * Created by GaYoon on 2017-08-15.
 */

public class CustomVibePopup extends Activity implements View.OnClickListener{

    private WindowManager.LayoutParams layoutParams;
    private TextView textTitle, textContent;
    private Button btnConfirm;

    private Vibrator vibe;

    private Intent intent;

    private WaitingFoodNumberActivity tmpWaitingFoodNumberActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        dialogBackground();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_popup);

        // 앱이 백그라운드에 있을 때 완료된 대기번호 색칠하기
        intent = new Intent(this.getIntent());
        WaitingFoodNumberActivity tmpWaitingFoodNumberActivity = ((WaitingFoodNumberActivity)WaitingFoodNumberActivity.mContext);
        CompleteFoodBoardFragment tmpCompleteFoodBoardFragment = ((CompleteFoodBoardFragment)CompleteFoodBoardFragment.mFragment);
        // 앱이 백그라운드에 있는 경우 색을 바꾸고 앱이 종료되어 있는 경우에는 바꾸지 않는다
        if(intent != null && tmpWaitingFoodNumberActivity != null) {
            tmpWaitingFoodNumberActivity.matchCompleteFoodNumber(intent.getStringExtra("foodnumber"));
        }
        // 전광판에 완료된 대기번호 추가하기
//        if(intent != null && tmpCompleteFoodBoardFragment != null) {
//            tmpCompleteFoodBoardFragment.addCompleteFood(intent.getStringExtra("foodnumber"));
//        }
            initView();
        dialogSize();
        dialogVibe();

        // 다이얼로그 텍스트
        textContent.setText("주문하신 메뉴가 완료되었습니다. 카운터에서 받아가세요.\n번호 : " + intent.getStringExtra("foodnumber"));
        // 커스텀한 다이얼로그의 타이틀 부분 GONE처리
        textTitle.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibe.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView(){
        textTitle = (TextView) findViewById(R.id.acp_tv_title);
        textContent = (TextView) findViewById(R.id.acp_tv_content);
        btnConfirm = (Button)findViewById(R.id.acp_btn_confirm);
        btnConfirm.setOnClickListener(this);
        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void dialogBackground() {
        // 다이얼로그 뒷 배경 투명하게 표현
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void dialogSize(){
        // 다이얼로그 크기
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width   = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().height  = WindowManager.LayoutParams.MATCH_PARENT;
    }

    private void dialogVibe() {
        long[] pattern = {0, 1000, 1000};
        vibe.vibrate(pattern, 0);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.acp_btn_confirm:
                vibe.cancel();
                finish();
                break;
            default:
                break;
        }
    }

    // 홈 버튼 눌렀을 때 기능 추가 - 진동알림끄기
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        vibe.cancel();
        finish();
    }
}


