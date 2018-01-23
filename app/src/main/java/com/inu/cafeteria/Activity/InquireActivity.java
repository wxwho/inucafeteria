package com.inu.cafeteria.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inu.cafeteria.Application.ApplicationController;
import com.inu.cafeteria.Model.ErrorMsgData;
import com.inu.cafeteria.Model.ErrorMsgResult;
import com.inu.cafeteria.Network.NetworkService;
import com.inu.cafeteria.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by GaYoon on 2017-08-18.
 */

public class InquireActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editErrormmsg;
    Button btnSend;

    // For actionbar and drawer
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    ErrorMsgData errorMsgData;
    NetworkService service;

    SharedPreferences studentInfo;
    String sno, sname;

    // 글씨체 적용
    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_msg);

        initView();
        initActionbar();
        initActionbarMenu();
    }


    private void initView() {
        editErrormmsg = (EditText) findViewById(R.id.aim_edit_errormsg);
        btnSend = (Button) findViewById(R.id.aim_btn_send);
        btnSend.setOnClickListener(this);

        service = ApplicationController.getInstance().getNetworkService();

        studentInfo = getSharedPreferences("studentInfo", Activity.MODE_PRIVATE);
        sno = studentInfo.getString("inputsno", null);
        sname= studentInfo.getString("inputsname", null);
    }


    private void initActionbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void initActionbarMenu(){
        // 툴바 메뉴 이미지 설정 및 활성화
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.btn_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    // 툴바에 있는 뒤로가기 버튼 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.aim_btn_send:

                if(editErrormmsg.getText().toString().equals("")){
                    Toast.makeText(this, "문의내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }

                else {
                    errorMsgData = new ErrorMsgData(sno, editErrormmsg.getText().toString());

                    Call<ErrorMsgResult> call = service.getErrorMsgResult(errorMsgData);
                    call.enqueue(new Callback<ErrorMsgResult>() {
                        @Override
                        public void onResponse(Call<ErrorMsgResult> call, Response<ErrorMsgResult> response) {
                        }

                        @Override
                        public void onFailure(Call<ErrorMsgResult> call, Throwable t) {
                        }
                    });


                    Toast.makeText(this, "문의사항이 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
