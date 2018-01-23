package com.inu.cafeteria.Application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.inu.cafeteria.Network.NetworkService;
import com.inu.cafeteria.Network.RegisterNetworkService;
import com.inu.cafeteria.R;
import com.tsengvn.typekit.Typekit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationController extends Application {

    // 먼저 어플리케이션 인스턴스 객체를 하나 선언
    private static ApplicationController instance;
    private static ApplicationController registerInstance;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static String baseUrl = "baseUrl";
    private static String registerUrl = "registerUrl";
    private static String nation = "korea";

    // 식당 이미지 변경이 필요할 경우 이부분만 수정하면 됨 (InputFoodNumerActivity 뷰 식당 이미지)
    private int[] coverflowImages = {
            R.drawable.img_cafedream_1,
            R.drawable.img_45_corner,
            R.drawable.img_sabumdae,
            R.drawable.img_kimbab,
            R.drawable.img_sodam,
            R.drawable.img_cafedream_2,
            R.drawable.img_miyu,
    };

    // WaitingFoodNumberActivity 뷰 식당 이미지
    private int[] waitingImages = {
            R.drawable.img_cafedream_12,
            R.drawable.img_45_corner_2,
            R.drawable.img_sabumdae_2,
            R.drawable.img_kimbab_2,
            R.drawable.img_sodam_2,
            R.drawable.img_cafedream_22,
            R.drawable.img_miyu_2,
    };

    // 식당 이름 및 변경이 필요한 경우 이부분을 수정
    // 수정 시 SplashActivity에 있는 배열도 같이 수정해줄것
    private String[] coverflowTitles = {
            "카페드림(학식실외)",
            "학생식당(4, 5코너)",
            "사범대식당",
            "김밥천국",
            "소담국밥",
            "카페드림(도서관)",
            "미유카페",
    };

    private String[] coverflowCodes = {
            "7",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
    };

    public static String getNation() {
        return nation;
    }

    public static void setNation(String nation) {
        ApplicationController.nation = nation;
    }

    public int[] getCoverflowImages() {
        return coverflowImages;
    }

    public String[] getCoverflowTitles() {
        return coverflowTitles;
    }

    public String[] getCoverflowCodes() {
        return coverflowCodes;
    }

    public int[] getWaitingImages() {return waitingImages;}


    // 네트워크 서비스 객체 선언
    private NetworkService networkService;
    private RegisterNetworkService registerNetworkService;


    // 인스턴스 객체 반환  왜? static 안드에서 static 으로 선언된 변수는 매번 객체를 새로 생성하지 않아도 다른 액티비티에서
    // 자유롭게 사용가능합니다.
    public static ApplicationController getInstance() {
        return instance;
    }
    public static ApplicationController getRegisterInstance() { return registerInstance; }

    // 네트워크서비스 객체 반환
    public NetworkService getNetworkService() {
        return networkService;
    }
    public RegisterNetworkService getRegisterNetworkService() { return registerNetworkService; }


    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    //인스턴스 객체 초기화
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationController.instance = this;
        ApplicationController.registerInstance = this;
        buildService();

        setFont();


    }

    private void setFont() {
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/KoPubDotumMedium.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/KoPubDotumBold.ttf"));
    }

    public void buildService() {
        // Maven에서 포함한 PersistentCookieJar를 통해 쿠키 세팅
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit.Builder builder1 = new Retrofit.Builder();

        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        Retrofit registerRetrofit = builder1
                .baseUrl(registerUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        networkService = retrofit.create(NetworkService.class);
        registerNetworkService = registerRetrofit.create(RegisterNetworkService.class);
    }
}
