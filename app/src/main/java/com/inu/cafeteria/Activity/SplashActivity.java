package com.inu.cafeteria.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.inu.cafeteria.Application.ApplicationController;
import com.inu.cafeteria.Application.MarketVersionChecker;
import com.inu.cafeteria.Model.AutoLoginData;
import com.inu.cafeteria.Model.AutoLoginResult;
import com.inu.cafeteria.Model.MessageResult;
import com.inu.cafeteria.Model.WaitData;
import com.inu.cafeteria.Model.WaitResult;
import com.inu.cafeteria.Network.NetworkService;
import com.inu.cafeteria.Network.RegisterNetworkService;
import com.inu.cafeteria.R;
import com.inu.cafeteria.Utility.CustomDialogNotice;
import com.inu.cafeteria.Utility.CustomMessage;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {

    AutoLoginData autoLoginData;
    WaitData waitData;

    SharedPreferences auto, studentInfo, checkNoReplayNoticeDate;
    String loginDtoekn, loginSno;
    String checkReplayNoticeDate;

    NetworkService service;
    RegisterNetworkService registerNetworkService;

    // 공지사항 다이얼로그
    private CustomMessage mCustomDialog;
    private CustomDialogNotice mCustomDialogNotice;


    // 버전 체크
    String storeVersion;
    String deviceVersion;
    private BackgroundThread mBackgroundThread;
    private DeviceVersionCheckHandler deviceVersionCheckHandler;

    // 글씨체 적용
    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        // 버전 체크
        mBackgroundThread.start();
    }


    private void initView() {

        service = ApplicationController.getInstance().getNetworkService();
        registerNetworkService = ApplicationController.getRegisterInstance().getRegisterNetworkService();

        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginDtoekn = auto.getString("inputDtoken", null);
        loginSno = auto.getString("inputSno", null);

        studentInfo = getSharedPreferences("studentInfo", Activity.MODE_PRIVATE);

        mBackgroundThread = new BackgroundThread();
        deviceVersionCheckHandler = new DeviceVersionCheckHandler(this);

        checkNoReplayNoticeDate = getSharedPreferences("checkNoReplayNoticeDate", Activity.MODE_PRIVATE);
    }

    // 버전 체크
    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            // 패키지 네임 전달
            storeVersion = MarketVersionChecker.getMarketVersion(getPackageName());

            // 디바이스 버전 가져옴
            try {
                deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            deviceVersionCheckHandler.sendMessage(deviceVersionCheckHandler.obtainMessage());
            // 핸들러로 메세지 전달
        }
    }

    // 핸들러 객체 만들기
    private static class DeviceVersionCheckHandler extends Handler{
        private final WeakReference<SplashActivity> mainActivityWeakReference;
        public DeviceVersionCheckHandler(SplashActivity mainActivity) {
            mainActivityWeakReference = new WeakReference<SplashActivity>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                activity.handleMessage(msg);
                // 핸들메세지로 결과값 전달

            }
        }
    }

    private void handleMessage(Message msg) {
        //핸들러에서 넘어온 값 체크

        // 서버 연결이 원활하지 않은 경우
        if(storeVersion == null) {
            mCustomDialog = new CustomMessage(SplashActivity.this,
                    "오류",
                    "버전 정보를 가져오지 못했습니다.\n데이터 연결을 확인해주세요.",
                    closeApp);
            mCustomDialog.show();
            return;
        }

        if (storeVersion.compareTo(deviceVersion) > 0) {
            // 업데이트 필요
            mCustomDialog = new CustomMessage(SplashActivity.this,
                    "업데이트",
                    "수정 사항이 있으니, \n업데이트 해주시기 바랍니다.",
                    updateConfirm);
            mCustomDialog.show();


        } else {
            // 업데이트 불필요
            // 공지사항 다이얼로그로 이동
            initMessage();

        }
    }

    private View.OnClickListener updateConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(ApplicationController.getInstance().getNation().equals("korea")) {
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            }
            else if(ApplicationController.getInstance().getNation().equals("china")) {
                intent.setData(Uri.parse("http://uicoop.ac.kr/mobile/notice_view.html?num=1279&page=1&seltb="));
            }
            startActivity(intent);
        }
    };


    // 공지사항
    private void initMessage() {

        Call<MessageResult> call = service.getMessageResult();
        call.enqueue(new Callback<MessageResult>() {

            @Override
            public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                if(response.isSuccessful()) {

                    // 오늘 날짜 구하기
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String getTime = sdf.format(date);

                    // 공지사항 오늘 하루 다시 보지 않기 선택한 경우
                    if(checkNoReplayNoticeDate.getString("date", "").equals(getTime)) {
                        initSplash();
                    }
                    else {
                        // 공통 공지사항이 있을 경우
                        if (!response.body().getAll().getMessage().equals("")) {
                            mCustomDialogNotice = new CustomDialogNotice(SplashActivity.this,
                                    response.body().getAll().getTitle(),
                                    response.body().getAll().getMessage(),
                                    noReplayNoticeToday,
                                    initConfirm);
                            mCustomDialogNotice.show();
                        }
                        // 안드로이드 공지사항이 있는 경우
                        else if (!response.body().getAndroid().getMessage().equals("")) {
                            mCustomDialogNotice = new CustomDialogNotice(SplashActivity.this,
                                    response.body().getAndroid().getTitle(),
                                    response.body().getAndroid().getMessage(),
                                    noReplayNoticeToday,
                                    initConfirm);
                            mCustomDialogNotice.show();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<MessageResult> call, Throwable t) {
                Log.d("debug", t.getMessage().toString());
            }
        });
    }


    private View.OnClickListener initConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomDialogNotice.cancel();
            initSplash();
        }
    };

    private View.OnClickListener noReplayNoticeToday = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 오늘 날짜 구하기
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String getTime = sdf.format(date);

            SharedPreferences.Editor editor = checkNoReplayNoticeDate.edit();
            editor.putString("date", getTime);
            editor.commit();
            mCustomDialogNotice.cancel();
            initSplash();
        }
    };

    private View.OnClickListener closeApp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomDialog.cancel();
            finish();
        }
    };


    private void initSplash(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkWait();
            }
        }, 500);
    }


    // 기다리는 푸시가 있는지 체크
    private void checkWait() {

        waitData = new WaitData(FirebaseInstanceId.getInstance().getToken());
        Call<WaitResult> waitcall = registerNetworkService.getWaitResult(waitData);
        waitcall.enqueue(new Callback<WaitResult>() {

            @Override
            public void onResponse(Call<WaitResult> call, Response<WaitResult> response) {

                // 현재 대기중인 음식이 없는 경우
                if (response.body() == null) {
                    // 자동로그인인지 체크
                    // 자동로그인인 경우
                    if (loginDtoekn != null) {
                        checkAuto();
                    }
                    // 수동로그인인 경우
                    else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }

                // 현재 대기중인 음식이 있는 경우
                else {
//                    Intent intent = new Intent(SplashActivity.this, InputFoodNumberActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(SplashActivity.this, WaitingFoodNumberActivity.class);
                    intent.putExtra("code", String.valueOf(response.body().getCode()));
                    intent.putExtra("name", ApplicationController.getInstance().getCoverflowTitles()[Integer.parseInt(response.body().getCode()) % 7]);
                    intent.putExtra("num1", String.valueOf(response.body().getNum1()));
                    intent.putExtra("num2", String.valueOf(response.body().getNum2()));
                    intent.putExtra("num3", String.valueOf(response.body().getNum3()));
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<WaitResult> call, Throwable t) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    // 자동로그인인 경우
    private void checkAuto() {
        autoLoginData = new AutoLoginData(loginDtoekn, loginSno);
        Log.i("dtoken", loginDtoekn);

        Call<AutoLoginResult> call = service.getAutoLoginResult(autoLoginData);
        call.enqueue(new Callback<AutoLoginResult>() {
            @Override
            public void onResponse(Call<AutoLoginResult> call, Response<AutoLoginResult> response) {

                SharedPreferences.Editor student = studentInfo.edit();

                // 서버에서 로그인관련 데이터를 지웠을 경우
                if (response.body() == null) {
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.clear();
                    autoLogin.commit();

                    student.clear();
                    student.commit();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {

                    student.putString("inputbarcode", response.body().getBarcode());
                    student.putString("inputstat", response.body().getStuInfo().getStat());
                    student.commit();

                    Intent intent = new Intent(SplashActivity.this, InputFoodNumberActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<AutoLoginResult> call, Throwable t) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}

