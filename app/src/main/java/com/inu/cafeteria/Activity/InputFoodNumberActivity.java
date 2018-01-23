package com.inu.cafeteria.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.img.coverflow.widget.CoverFlowView;
import com.inu.cafeteria.Adapter.CoverFlowAdapter;
import com.inu.cafeteria.Adapter.InputFoodNumberRecyclerAdapter;
import com.inu.cafeteria.Application.ApplicationController;
import com.inu.cafeteria.Model.ActiveBarcodeData;
import com.inu.cafeteria.Model.ActiveBarcodeResult;
import com.inu.cafeteria.Model.CoverflowModel;
import com.inu.cafeteria.Model.LoginResult;
import com.inu.cafeteria.Model.LogoutResult;
import com.inu.cafeteria.Model.RegisterData;
import com.inu.cafeteria.Model.RegisterResult;
import com.inu.cafeteria.Network.NetworkService;
import com.inu.cafeteria.Network.RegisterNetworkService;
import com.inu.cafeteria.R;
import com.inu.cafeteria.Utility.BackPressCloseHandler;
import com.inu.cafeteria.Utility.CustomDialog;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InputFoodNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private int editInputConut;

    private CoverFlowView coverFlowView;
    private TextView textCode;
    private Button btnPrevious;
    private Button btnForward;
    private Button btnInput;
    private Button btnPlus;
    private Button btnMinus;

    CoverFlowAdapter coverFlowAdapter;

    RecyclerView recyclerInputFoodNumber;
    RecyclerView.Adapter adapterInputFoodNumber;
    RecyclerView.LayoutManager layoutManager;

    // View in drawer
    private TextView textMajor;
    private TextView textSno;
    private TextView textName;
    private ImageView imgBarcode;
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
    private Button btnInquire;
    private Button btnAppInfo;

    // 다이얼로그
    private CustomDialog mCustomDialog;
    // 뒤로가기 버튼
    private BackPressCloseHandler backPressCloseHandler;

    //For intent data
    Intent intent;
    LoginResult intentData;

    RegisterData registerData;
    NetworkService service;
    RegisterNetworkService registerService;

    private List<CoverflowModel> coverflowList;

    private LinearLayout backgroundLayout;

    // 입력값 저장을 위한 배열
    private String[] loadData = { "", "", "" };

    // 글씨체 적용
    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_food_number);

        // get data about dtoken, barcode, cafeteria, student info
        intent = getIntent();
        intentData = (LoginResult)intent.getSerializableExtra("all");

        initView();
        initNetwork();
        initActionbar();
        initDrawer();
        initEvent();
        initListDataAndAction();

        // 회원 로그인인지 비회원 로그인인지 체크
        if(studentInfo.getString("inputsno", null) != null){
            // 회원 로그인인 경우
            initActionbarMenu();
        }
        else{
            // 비회원 로그인인 경우
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
        }
        else {
            backPressCloseHandler.onBackPressedActivity();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
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

        // 학생인증 바코드 비활성화
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

    private void initView() {
        editInputConut = 1;
        btnInput = (Button) findViewById(R.id.aifn_btn_submit);
        coverFlowView = (CoverFlowView) findViewById(R.id.aifn_coverflow);
        textCode = (TextView)findViewById(R.id.aifn_text_code);
        btnPrevious = (Button) findViewById(R.id.aifn_btn_previous);
        btnForward = (Button) findViewById(R.id.aifn_btn_forward);
        btnPlus = (Button) findViewById(R.id.aifn_btn_plus);
        btnMinus = (Button) findViewById(R.id.aifn_btn_minus);
        recyclerInputFoodNumber = (RecyclerView)findViewById(R.id.aifn_recycle_input_food_number);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.aifn_layout_drawer);
        backPressCloseHandler = new BackPressCloseHandler(this);
        backgroundLayout = (LinearLayout)findViewById(R.id.aifn_layout_main);
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
        drawer = (DrawerLayout)findViewById(R.id.aifn_layout_drawer);

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

                // 바코드 활성화 (서버통신)
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
        if(studentInfo != null && studentInfo.getString("inputstat", "").equals("재학")) {
            CreateBarcode();
        }
    }


    private void initEvent() {
        btnPrevious.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnInput.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = coverFlowView.getTopViewPosition();
        CoverflowModel model = coverflowList.get(position);
        CoverFlowAdapter.Holder holder = (CoverFlowAdapter.Holder) coverFlowView.getTopView().getTag();

        switch (view.getId()) {

            // 배경화면 터치시 키보드 내리기
            case R.id.aifn_layout_main:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;

            case R.id.aifn_btn_plus:
                //기존에 입력한 데이터 저장
                for(int i = 0; i < editInputConut; i++) {
                    EditText edit = (EditText)recyclerInputFoodNumber.getChildAt(i).findViewById(R.id.irifn_edit_input_food_number);
                    String tmp = edit.getText().toString();
                    loadData[i] = tmp;
                }

                //Log.i("loadData", loadData[0] + " " + loadData[1] + " " + loadData[2]);

                if (editInputConut < 3) {
                    adapterInputFoodNumber = new InputFoodNumberRecyclerAdapter(getApplicationContext(), ++editInputConut, loadData);
                    recyclerInputFoodNumber.setAdapter(adapterInputFoodNumber);
                }


                // input 칸이 3개일때 plus 버튼 숨김
                if(editInputConut == 3){
                    btnPlus.setVisibility(View.INVISIBLE);
                }
                else if(btnMinus.getVisibility() == View.INVISIBLE){
                    btnMinus.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.aifn_btn_minus:
                for(int i = 0; i < editInputConut - 1; i++) {
                    EditText edit = (EditText)recyclerInputFoodNumber.getChildAt(i).findViewById(R.id.irifn_edit_input_food_number);
                    String tmp = edit.getText().toString();
                    loadData[i] = tmp;
                }

                if (editInputConut > 1) {
                    adapterInputFoodNumber = new InputFoodNumberRecyclerAdapter(getApplicationContext(), --editInputConut, loadData);
                    recyclerInputFoodNumber.setAdapter(adapterInputFoodNumber);
                }

                // input 칸이 1개일때 minus 버튼 숨김
                if(editInputConut == 1){
                    btnMinus.setVisibility(View.INVISIBLE);
                }
                else if(btnPlus.getVisibility() == View.INVISIBLE){
                    btnPlus.setVisibility(View.VISIBLE);
                }

                break;

            // Logout
            case R.id.drawer_btn_logout:

                mCustomDialog = new CustomDialog(InputFoodNumberActivity.this,
                        "",
                        "로그아웃 하시겠습니까?",
                        logoutCancel,
                        logoutConfirm);
                mCustomDialog.show();

                break;

            case R.id.drawer_btn_app_info:
                intent = new Intent(InputFoodNumberActivity.this, InfomationActivity.class);
                startActivity(intent);
                break;

            case R.id.drawer_btn_inquire:
                intent = new Intent(InputFoodNumberActivity.this, InquireActivity.class);
                startActivity(intent);
                break;

            case R.id.aifn_btn_previous:
                coverFlowView.gotoPrevious();
                break;

            case R.id.aifn_btn_forward:
                coverFlowView.gotoForward();
                break;

            case R.id.aifn_btn_submit:
                // 비어있는 입력칸의 개수를 센다
                int editEmptyCount = 0;
                for(int i = 0; i < editInputConut; i++) {
                    EditText tmpEdit = (EditText) recyclerInputFoodNumber.getChildAt(i).findViewById(R.id.irifn_edit_input_food_number);
                    if (tmpEdit.getText().toString().equals("")) {
                        editEmptyCount++;
                    }
                }
                // 모든 입력칸이 비어있을 경우
                if(editEmptyCount == editInputConut) {
                    Toast.makeText(this, "대기번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }

                // 식당이름과 코드 정보 넘기기
                intent = new Intent(InputFoodNumberActivity.this, WaitingFoodNumberActivity.class);
                intent.putExtra("name", ApplicationController.getInstance().getCoverflowTitles()[coverFlowView.getTopViewPosition()]);
                intent.putExtra("code", ApplicationController.getInstance().getCoverflowCodes()[coverFlowView.getTopViewPosition()]);


                // 대기번호 입력 텍스트에 있는 값들을 인텐트로 넘김
                for(int i = 0; i < editInputConut; i++) {
                    EditText edit = (EditText)recyclerInputFoodNumber.getChildAt(i).findViewById(R.id.irifn_edit_input_food_number);
                    String tmp = "";
                    if(!edit.getText().toString().equals("")) {
                        tmp = Integer.parseInt(edit.getText().toString()) + "";
                    }
                    intent.putExtra("num" + (i + 1), tmp.equals("") ? "-1" : tmp);
                }
                for(int i = editInputConut; i < 3; i++) {
                    intent.putExtra("num" + (i + 1), "-1");
                }


                // 서버에 대기번호 등록하기
                registerData = new RegisterData(
                        intent.getStringExtra("code"),
                        intent.getStringExtra("num1"),
                        intent.getStringExtra("num2"),
                        intent.getStringExtra("num3"),
                        FirebaseInstanceId.getInstance().getToken(),
                        "android");

                Call<RegisterResult> registerResultCall = registerService.getRegisterResult(registerData);
                registerResultCall.enqueue(new Callback<RegisterResult>() {
                    @Override
                    public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onFailure(Call<RegisterResult> call, Throwable t) {
                        Toast.makeText(InputFoodNumberActivity.this, "서버접속이 끊겼습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            default:
                break;
        }
    }

    private void initListDataAndAction() {
        layoutManager = new LinearLayoutManager(this);
        recyclerInputFoodNumber.setLayoutManager(layoutManager);
        adapterInputFoodNumber = new InputFoodNumberRecyclerAdapter(getApplicationContext(), editInputConut, loadData);
        recyclerInputFoodNumber.setAdapter(adapterInputFoodNumber);


        coverflowList = new ArrayList<>();
        for (int i = 0; i < ApplicationController.getInstance().getCoverflowTitles().length; i++) {
            CoverflowModel channelBean = new CoverflowModel();
            channelBean.setImg(ApplicationController.getInstance().getCoverflowImages()[i]);
            coverflowList.add(channelBean);
        }

        coverFlowAdapter = new CoverFlowAdapter(this, coverflowList, textCode);
        // 스크롤할 때 식당이름을 나타내는 텍스트뷰를 넣는 부분
        coverFlowView.setTextView(textCode);
        // 스크롤할 때 변경되는 식당이름을 넣는 부분
        coverFlowView.setCafeList(ApplicationController.getInstance().getCoverflowTitles());
        coverFlowView.setAdapter(coverFlowAdapter);
    }

    private void initNetwork() {
        //FirebaseMessaging.getInstance().subscribeToTopic("news");
        //FirebaseInstanceId.getInstance().getToken();
        service = ApplicationController.getInstance().getNetworkService();
        registerService = ApplicationController.getRegisterInstance().getRegisterNetworkService();
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
                    if (response.isSuccessful()) {
                        if (response.body().getResult().equals("logout is successful")) {
                            // Delete auto login data
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.clear();
                            autoLogin.commit();

                            Intent intent = new Intent(InputFoodNumberActivity.this, LoginActivity.class);
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

}
