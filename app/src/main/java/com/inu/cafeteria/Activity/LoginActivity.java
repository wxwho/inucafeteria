package com.inu.cafeteria.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inu.cafeteria.Application.ApplicationController;
import com.inu.cafeteria.Model.LoginData;
import com.inu.cafeteria.Model.LoginResult;
import com.inu.cafeteria.Network.NetworkService;
import com.inu.cafeteria.Network.RegisterNetworkService;
import com.inu.cafeteria.R;
import com.inu.cafeteria.Utility.BackPressCloseHandler;
import com.tsengvn.typekit.TypekitContextWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inu.cafeteria.R.id.view;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText ed_id;
    EditText ed_pw;
    CheckBox cb_login;
    TextView btnLoginNoMember;

    LoginData loginData;

    SharedPreferences auto, studentInfo, checkFirst;
    String loginDtoekn, loginSno, isAppFirst;

    NetworkService service;
    RegisterNetworkService registerNetworkService;

    FrameLayout imageview;
    AnimationDrawable animationDrawable;

    // 뒤로가기 버튼
    private BackPressCloseHandler backPressCloseHandler;

    private RelativeLayout relativeLayoutStart;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;

    // 글씨체 적용
    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        animationView();
        checkIsAppFirstRun();

        relativeLayoutStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeOutFirstSplash();
                fadeInLogin();
            }

            private void fadeOutFirstSplash() {
                Animation animationFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.first_splash_fadeout);
                relativeLayout.setAnimation(animationFadeOut);
                relativeLayout.setVisibility(View.GONE);

            }

            private void fadeInLogin() {
                Animation animationFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.first_splash_fadein);
                linearLayout.setAnimation(animationFadeIn);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });


    }

    private void checkIsAppFirstRun() {
        isAppFirst = checkFirst.getString("isFirst", null);
        if(isAppFirst != null) {
            relativeLayout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        else {
            SharedPreferences.Editor isFirstEditor = checkFirst.edit();
            isFirstEditor.putString("isFirst", "yes");
            isFirstEditor.commit();
        }
    }


    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressedActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }


    private void initView() {

        btnLogin = (Button) findViewById(R.id.al_btn_login);
        ed_id = (EditText) findViewById(R.id.al_edit_id);
        ed_pw = (EditText) findViewById(R.id.al_edit_pw);
        cb_login = (CheckBox) findViewById(R.id.al_chk_autoLogin);
        btnLoginNoMember = (TextView) findViewById(R.id.al_text_nonmember);
        btnLogin.setOnClickListener(this);
        btnLoginNoMember.setOnClickListener(this);

        service = ApplicationController.getInstance().getNetworkService();
        registerNetworkService = ApplicationController.getRegisterInstance().getRegisterNetworkService();

        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginDtoekn = auto.getString("inputDtoken", null);
        loginSno = auto.getString("inputSno", null);

        imageview = (FrameLayout) findViewById(view);

        studentInfo = getSharedPreferences("studentInfo", Activity.MODE_PRIVATE);
        checkFirst = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);

        backPressCloseHandler = new BackPressCloseHandler(this);

        relativeLayoutStart = (RelativeLayout)findViewById(R.id.al_layout_start);
        relativeLayout = (RelativeLayout)findViewById(R.id.al_layout_first_splash);
        linearLayout = (LinearLayout)findViewById(R.id.al_layout_login);
        linearLayout.setOnClickListener(this);
    }

    private void animationView() {

        animationDrawable = (AnimationDrawable) (imageview.getBackground() != null && imageview.getBackground() instanceof AnimationDrawable ?
                imageview.getBackground() : null);
        if (animationDrawable != null) {
            //animationDrawable.setEnterFadeDuration(10);
            animationDrawable.setExitFadeDuration(2000);
            animationDrawable.setAlpha(240);

        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            // 배경화면 터치시 키보드 내리기
            case R.id.al_layout_login:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;

            // 로그인버튼 누르는 경우
            case R.id.al_btn_login:
                loginData = new LoginData(
                        ed_id.getText().toString(),
                        ed_pw.getText().toString(),
                        "android",
                        String.valueOf(cb_login.isChecked()));

                Call<LoginResult> call = service.getLoginResult(loginData);
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                        if (response.isSuccessful()) {
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();

                            // loginModel.getBarcode() 대신 아래 소스 참고
                            // LoginModel temp = response.body();
                            // temp.getBarcode();
                            // 자동로그인이 체크 되어 있다면, dtoken값과 sno값을 저장
                            if (cb_login.isChecked()) {
                                autoLogin.putString("inputDtoken", response.body().getLogin().getDtoken());
                                autoLogin.putString("inputSno", loginData.getSno());
                                autoLogin.commit();
                            }


                            SharedPreferences studentInfo = getSharedPreferences("studentInfo", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor student = studentInfo.edit();

                            student.putString("inputsno", response.body().getStu_info().getStu_num());
                            student.putString("inputmajor", response.body().getStu_info().getDep());
                            student.putString("inputsname", response.body().getStu_info().getName());
                            student.putString("inputbarcode", response.body().getLogin().getBarcode());
                            student.putString("inputstat", response.body().getStu_info().getStat());
                            student.commit();

                            Intent intent = new Intent(LoginActivity.this, InputFoodNumberActivity.class);
                            LoginResult intentData = response.body();
                            Bundle bundle =  new Bundle();
                            bundle.putSerializable("all", intentData);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            Toast.makeText(LoginActivity.this, "로그인 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("Login, ", t.getMessage());
                    }
                });

                break;

            // 비회원 로그인버튼 누르는 경우
            case R.id.al_text_nonmember:
                SharedPreferences.Editor student = studentInfo.edit();
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.clear();
                autoLogin.commit();

                student.clear();
                student.commit();

                Intent intent = new Intent(LoginActivity.this, InputFoodNumberActivity.class);
                startActivity(intent);
                finish();

                break;

            default:
                break;
        }

    }
}
