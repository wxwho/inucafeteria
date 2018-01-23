package com.inu.cafeteria.Network;

import com.inu.cafeteria.Model.ActiveBarcodeData;
import com.inu.cafeteria.Model.ActiveBarcodeResult;
import com.inu.cafeteria.Model.AutoLoginData;
import com.inu.cafeteria.Model.AutoLoginResult;
import com.inu.cafeteria.Model.ErrorMsgData;
import com.inu.cafeteria.Model.ErrorMsgResult;
import com.inu.cafeteria.Model.LoginData;
import com.inu.cafeteria.Model.LoginResult;
import com.inu.cafeteria.Model.LogoutResult;
import com.inu.cafeteria.Model.MessageResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NetworkService {

    // 로그인
    @POST("/postlogin")
    Call<LoginResult> getLoginResult(@Body LoginData data);

    // 자동로그인
    @POST("/autologin")
    Call<AutoLoginResult> getAutoLoginResult(@Body AutoLoginData data);

    // 로그아웃
    @POST("/logout")
    Call<LogoutResult> getLogoutResult();

    // 바코드 활성화 설정
    @POST("/activeBarcode")
    Call<ActiveBarcodeResult> getActiveBarcodeResult(@Body ActiveBarcodeData data);

    // 공지사항
    @GET("/server_message.json")
    Call<MessageResult> getMessageResult();


    // 에러메세지
    @POST("/errormsg")
    Call<ErrorMsgResult> getErrorMsgResult(@Body ErrorMsgData data);
}

