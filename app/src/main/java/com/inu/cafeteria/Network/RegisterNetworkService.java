package com.inu.cafeteria.Network;

import com.inu.cafeteria.Model.PushModel;
import com.inu.cafeteria.Model.RegisterData;
import com.inu.cafeteria.Model.RegisterResult;
import com.inu.cafeteria.Model.ResetNumModel;
import com.inu.cafeteria.Model.WaitData;
import com.inu.cafeteria.Model.WaitResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by GaYoon on 2017-08-02.
 */

public interface RegisterNetworkService {

    // 음식번호 입력
    @POST("/registerNumber")
    Call<RegisterResult> getRegisterResult(@Body RegisterData data);


    // 푸시
    @POST("/pushNumber")
    Call<PushModel> getPushResult(@Body PushModel data);


    // 음식번호 지우기
    @POST("/resetNumber")
    Call<ResetNumModel> getResetNumResult(@Body ResetNumModel data);


    // 기다리고 있는 번호가 있는지 판별
    @POST("/isNumberWait")
    Call<WaitResult> getWaitResult(@Body WaitData data);



}
