package com.inu.cafeteria.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.inu.cafeteria.Application.ApplicationController;
import com.inu.cafeteria.Fragment.CompleteFoodBoardFragment;
import com.inu.cafeteria.Model.ActiveBarcodeData;
import com.inu.cafeteria.Model.ActiveBarcodeResult;
import com.inu.cafeteria.Model.LogoutResult;
import com.inu.cafeteria.Model.ResetNumModel;
import com.inu.cafeteria.Network.NetworkService;
import com.inu.cafeteria.Network.RegisterNetworkService;
import com.inu.cafeteria.R;
import com.inu.cafeteria.Utility.BackPressCloseHandler;
import com.inu.cafeteria.Utility.CustomDialog;
import com.tsengvn.typekit.TypekitContextWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingFoodNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private String cafeteriaName, num1, num2, num3, code;
    private LinearLayout llBackImage, llFoodNum;
    private TextView txFoodNum1, txFoodNum2, txFoodNum3, textCafeteriaName;
    private Button tvInit;

    private NetworkService service;
    private RegisterNetworkService registerService;
    private ResetNumModel resetNumModel;

    // View in drawer
    private TextView textMajor;
    private TextView textSno;
    private TextView textName;
    private ImageView imgBarcode;
    private Button btnInquire;
    private Button btnLogout;

    DrawerLayout mDrawerLayout;

    SharedPreferences studentInfo;
    String sno, major, sname, barcode;

    final static private int DRAWER_CLOSED = 0;
    final static private int DRAWER_OPENED = 1;

    // 핸드폰 기존 밝기 값 저장
    private float originBright;

    // For actionbar and drawer
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private Button btnAppInfo;

    // 다이얼로그
    private CustomDialog mCustomDialog;
    // 뒤로가기 버튼
    private BackPressCloseHandler backPressCloseHandler;

    Intent intent;

    // 클래스 외부에서 함수를 사용하기 위함
    public static Context mContext;

    // 글씨체 적용
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_food_number);

        mContext = this;

        intent = new Intent(this.getIntent());
        cafeteriaName = intent.getStringExtra("name");
        num1 = intent.getStringExtra("num1");
        num2 = intent.getStringExtra("num2");
        num3 = intent.getStringExtra("num3");
        code = intent.getStringExtra("code");

        initView();
        initEvent();
        initNetwork();
        initActionbar();
        initDrawer();
        setWaitNumber();
        setCompleteNumber();

        // 회원 로그인인지 비회원 로그인인지 체크
        if(studentInfo.getString("inputsno", null) != null){
            initActionbarMenu();
        }
        else{
            // 비회원 로그인인 경우
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

    }



    @Override
    protected void onStart() {
        super.onStart();

        // 학생인증 바코드 통신체크 및 활성화
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            Call<ActiveBarcodeResult> activeBarcodeResultCall = service.getActiveBarcodeResult(new ActiveBarcodeData(DRAWER_OPENED, "android", barcode));
            activeBarcodeResultCall.enqueue(new Callback<ActiveBarcodeResult>() {
                @Override
                public void onResponse(Call<ActiveBarcodeResult> call, Response<ActiveBarcodeResult> response) {
                    // 서버 통신이 정삭적으로 이루어질 경우
                    if(response.isSuccessful()) {
                        // 바코드 활성화
                        imgBarcode.setVisibility(View.VISIBLE);
                    }
                    else {
                        // 바코드 비활성화
                        imgBarcode.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ActiveBarcodeResult> call, Throwable t) {
                    imgBarcode.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 홈버튼 눌렀을 때
        // 생명주기 : OnUserLeaveHint() -> onStop()

        // 학생인증 바코드 통신체크 및 비활성화
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            Call<ActiveBarcodeResult> activeBarcodeResultCall = service.getActiveBarcodeResult(new ActiveBarcodeData(DRAWER_CLOSED, "android", barcode));
            activeBarcodeResultCall.enqueue(new Callback<ActiveBarcodeResult>() {
                @Override
                public void onResponse(Call<ActiveBarcodeResult> call, Response<ActiveBarcodeResult> response) {

                }

                @Override
                public void onFailure(Call<ActiveBarcodeResult> call, Throwable t) {

                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
        }
        else {
            backPressCloseHandler.onBackPressedWaitingActivity();
        }
    }

    private void setCompleteNumber() {
        Fragment fragment = new CompleteFoodBoardFragment();
        Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
        bundle.putString("code", code); // key , value
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.awfn_complete_food_board, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }


    private void initView() {

        // 뷰 초기화
        textCafeteriaName = (TextView)findViewById(R.id.awfn_text_cafeteria_name);
        tvInit = (Button)findViewById(R.id.awfn_V_init);

        // 뷰 설정
        textCafeteriaName.setText(cafeteriaName);

        // 식당이미지 뷰
        llBackImage = (LinearLayout)findViewById(R.id.awfn_LL_image);
        llBackImage.setBackgroundResource(ApplicationController.getInstance().getWaitingImages()[Integer.parseInt(code) % 7]);

        // 음식번호(num) 뷰
        llFoodNum = (LinearLayout)findViewById(R.id.awfn_V_foodnum);

        // drawer 뷰
        mDrawerLayout = (DrawerLayout)findViewById(R.id.awfn_layout_drawer);

        // 뒤로가기 버튼 핸들러
        backPressCloseHandler = new BackPressCloseHandler(this);


    }

    private void initEvent() {

        // 음식 번호 초기화 버튼
        tvInit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                mCustomDialog = new CustomDialog(WaitingFoodNumberActivity.this,
                        "",
                        "대기번호를 초기화하면 알림이 오지\n않습니다. 초기화 하시겠습니까?",
                        initCancel,
                        initConfirm);
                mCustomDialog.show();

            }
        });

    }

    private void setWaitNumber() {
        int margin = 10;
        int textSize = 42;
        String textColor = "#FFFFFF";
        // 음식번호(num) 텍스트 : '(실제 입력한 번호)'
        if(num1 != null && !num1.equals("-1")) {
            txFoodNum1 = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(IntToDp(margin), 0, IntToDp(margin), 0); //left top right bottom
            txFoodNum1.setLayoutParams(params);
            txFoodNum1.setText(num1);
            txFoodNum1.setTextSize(textSize);
            txFoodNum1.setTextColor(Color.parseColor(textColor));
            txFoodNum1.setTag("txFoodNum1");
            llFoodNum.addView(txFoodNum1);
        }
        if(num2 != null && !num2.equals("-1")) {
            txFoodNum2 = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(IntToDp(margin), 0, IntToDp(margin), 0); //left top right bottom
            txFoodNum2.setLayoutParams(params);
            txFoodNum2.setText(num2);
            txFoodNum2.setTextSize(textSize);
            txFoodNum2.setTextColor(Color.parseColor(textColor));
            txFoodNum2.setTag("txFoodNum2");
            llFoodNum.addView(txFoodNum2);
        }
        if(num3 != null && !num3.equals("-1")) {
            txFoodNum3 = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(IntToDp(margin), 0, IntToDp(margin), 0); //left top right bottom
            txFoodNum3.setLayoutParams(params);
            txFoodNum3.setText(num3);
            txFoodNum3.setTextSize(textSize);
            txFoodNum3.setTextColor(Color.parseColor(textColor));
            txFoodNum3.setTag("txFoodNum3");
            llFoodNum.addView(txFoodNum3);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Logout

            case R.id.drawer_btn_logout:

                mCustomDialog = new CustomDialog(WaitingFoodNumberActivity.this,
                        "",
                        "로그아웃 하시겠습니까?",
                        logoutCancel,
                        logoutConfirm);
                mCustomDialog.show();

                break;

            case R.id.drawer_btn_app_info:
                intent = new Intent(WaitingFoodNumberActivity.this, InfomationActivity.class);
                startActivity(intent);
                break;

            case R.id.drawer_btn_inquire:
                intent = new Intent(WaitingFoodNumberActivity.this, InquireActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initNetwork() {
        //FirebaseMessaging.getInstance().subscribeToTopic("news");
        //FirebaseInstanceId.getInstance().getToken();
        service = ApplicationController.getInstance().getNetworkService();
        registerService = ApplicationController.getRegisterInstance().getRegisterNetworkService();
    }

    private void initActionbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void initActionbarMenu(){
        // 드로어 메뉴 이미지 설정 및 활성화
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.btn_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void initDrawer() {

        // 뷰 초기화
        textMajor = (TextView)findViewById(R.id.drawer_text_major);
        textSno = (TextView)findViewById(R.id.drawer_text_sno);
        textName = (TextView)findViewById(R.id.drawer_text_name);
        imgBarcode = (ImageView)findViewById(R.id.drawer_img_barcode);
        btnLogout = (Button)findViewById(R.id.drawer_btn_logout);
        btnInquire = (Button) findViewById(R.id.drawer_btn_inquire);
        btnAppInfo = (Button)findViewById(R.id.drawer_btn_app_info);
        drawer = (DrawerLayout)findViewById(R.id.awfn_layout_drawer);

        studentInfo = getSharedPreferences("studentInfo", Activity.MODE_PRIVATE);
        sno = studentInfo.getString("inputsno", null);
        major= studentInfo.getString("inputmajor", null);
        sname= studentInfo.getString("inputsname", null);
        barcode= studentInfo.getString("inputbarcode", null);

        // 뷰 데이터 설정
        textMajor.setText(major);
        textSno.setText(sno);
        textName.setText(sname);

        // 클릭이벤트 초기화
        btnInquire.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnAppInfo.setOnClickListener(this);

        // 드로어 토글 설정
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close){
            // 드로어 메뉴 닫힐 때
            public void onDrawerClosed(View view) {

                // 화면 밝기 조절 - 원래대로
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = originBright;
                getWindow().setAttributes(params);

                Call<ActiveBarcodeResult> activeBarcodeResultCall = service.getActiveBarcodeResult(new ActiveBarcodeData(DRAWER_CLOSED, "android", barcode));
                activeBarcodeResultCall.enqueue(new Callback<ActiveBarcodeResult>() {
                    @Override
                    public void onResponse(Call<ActiveBarcodeResult> call, Response<ActiveBarcodeResult> response) {

                    }

                    @Override
                    public void onFailure(Call<ActiveBarcodeResult> call, Throwable t) {

                    }
                });
                invalidateOptionsMenu();
            }

            // 드로어 메뉴 열릴 때
            public void onDrawerOpened(View drawerView) {

                // 화면 밝기 조절 - 밝게
                WindowManager.LayoutParams params = getWindow().getAttributes();
                originBright = params.screenBrightness;
                params.screenBrightness = 1.0f;
                getWindow().setAttributes(params);

                Call<ActiveBarcodeResult> activeBarcodeResultCall = service.getActiveBarcodeResult(new ActiveBarcodeData(DRAWER_OPENED, "android", barcode));
                activeBarcodeResultCall.enqueue(new Callback<ActiveBarcodeResult>() {
                    @Override
                    public void onResponse(Call<ActiveBarcodeResult> call, Response<ActiveBarcodeResult> response) {
                        // 서버 통신이 정삭적으로 이루어질 경우
                        if(response.isSuccessful()) {
                            if(response.body().getActive().equals("1")) {
                                // 바코드 활성화
                                imgBarcode.setVisibility(View.VISIBLE);
                            }
                            else {
                                // 바코드 비활성화
                                imgBarcode.setVisibility(View.INVISIBLE);
                            }
                        }
                        else {
                            // 바코드 비활성화
                            imgBarcode.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ActiveBarcodeResult> call, Throwable t) {
                        // 바코드 비활성화
                        imgBarcode.setVisibility(View.INVISIBLE);
                    }
                });
                invalidateOptionsMenu();
            }
        };
        drawer.setDrawerListener(drawerToggle);

        // Create barcode
        if(studentInfo.getString("inputstat", "").equals("재학")) {
            CreateBarcode();
        }

    }

    private void CreateBarcode() {
        MultiFormatWriter gen = new MultiFormatWriter();
        String dataBarcode = barcode;
        try {
            final int WIDTH = 480;
            final int HEIGHT = 360;
            BitMatrix bytemap = gen.encode(dataBarcode, BarcodeFormat.CODE_128, WIDTH, HEIGHT);
            Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
            for (int i = 0 ; i < WIDTH ; ++i)
                for (int j = 0 ; j < HEIGHT ; ++j) {
                    bitmap.setPixel(i, j, bytemap.get(i,j) ? Color.BLACK : Color.WHITE);
                }

            imgBarcode.setImageBitmap(bitmap);
            imgBarcode.invalidate();
            Log.i("debug", "barcode create success");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int IntToDp(int size) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int)(10 * dm.density);
    }

    private View.OnClickListener initCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomDialog.cancel();
        }
    };

    private View.OnClickListener initConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetNumModel = new ResetNumModel(FirebaseInstanceId.getInstance().getToken());

            Call<ResetNumModel> call = registerService.getResetNumResult(resetNumModel);
            call.enqueue(new Callback<ResetNumModel>() {
                @Override
                public void onResponse(Call<ResetNumModel> call, Response<ResetNumModel> response) {
                    if(response.body().getResult().equals("success")) {
                        Log.d("Token", resetNumModel.getFcmtoken());
                        mCustomDialog.cancel();
                        intent = new Intent(WaitingFoodNumberActivity.this, InputFoodNumberActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResetNumModel> call, Throwable t) {

                }
            });


        }
    };

    private View.OnClickListener logoutCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomDialog.cancel();

        }
    };

    private View.OnClickListener logoutConfirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Call<LogoutResult> logoutResultCall = service.getLogoutResult();
            logoutResultCall.enqueue(new Callback<LogoutResult>() {
                @Override
                public void onResponse(Call<LogoutResult> call, Response<LogoutResult> response) {
                    if(response.isSuccessful()) {
                        if(response.body().getResult().equals("logout is successful")) {
                            // Delete auto login data
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.clear();
                            autoLogin.commit();

                            intent = new Intent(WaitingFoodNumberActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LogoutResult> call, Throwable t) {

                }
            });
        }
    };

    public boolean matchCompleteFoodNumber(String number) {
        String completeColor = "#F26C4F";
        TextView textView1, textView2, textView3;
        textView1 = (TextView)llFoodNum.findViewWithTag("txFoodNum1");
        textView2 = (TextView)llFoodNum.findViewWithTag("txFoodNum2");
        textView3 = (TextView)llFoodNum.findViewWithTag("txFoodNum3");

        if(textView1 != null && textView1.getText().toString().equals(number)) {
            textView1.setTextColor(Color.parseColor(completeColor));
            return true;
        }
        if(textView2 != null && textView2.getText().toString().equals(number)) {
            textView2.setTextColor(Color.parseColor(completeColor));
            return true;
        }
        if(textView3 != null && textView3.getText().toString().equals(number)) {
            textView3.setTextColor(Color.parseColor(completeColor));
            return true;
        }
        return false;
    }

}
