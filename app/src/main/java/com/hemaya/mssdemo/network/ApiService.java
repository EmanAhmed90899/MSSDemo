package com.hemaya.mssdemo.network;

import com.hemaya.mssdemo.model.activationData.Request.ActivationDataRequest;
import com.hemaya.mssdemo.model.activationData.Response.ActivationDataResponse;
import com.hemaya.mssdemo.model.ephemeral.request.DSAPPSRPEphemeralRequest;
import com.hemaya.mssdemo.model.ephemeral.response.DSAPPSRPEphemeralResponse;
import com.hemaya.mssdemo.model.identification.UserInfoRequest.UserInfoRequest;
import com.hemaya.mssdemo.model.identification.otpRequest.OtpRequest;
import com.hemaya.mssdemo.model.identification.otpResponse.OtpResponse;
import com.hemaya.mssdemo.model.identification.otpVerifyRequest.OtpVerifyRequest;
import com.hemaya.mssdemo.model.identification.otpVerifyResponse.OtpVerifyResponse;
import com.hemaya.mssdemo.model.identification.resendOtpRequest.ResendOtpRequest;
import com.hemaya.mssdemo.model.identification.resendOtpResponse.ResendOtpResponse;
import com.hemaya.mssdemo.model.identification.storeUserRequest.StoreUserRequest;
import com.hemaya.mssdemo.model.identification.storeUserResponse.StoreUserResponse;
import com.hemaya.mssdemo.model.identification.userInfoResponse.UserInfoResponse;
import com.hemaya.mssdemo.model.mdlActivate.Request.MdlActivateRequest;
import com.hemaya.mssdemo.model.mdlActivate.Response.MdlActivateResponse;
import com.hemaya.mssdemo.model.mdlAddDevice.Request.MdlAddDeviceRequest;
import com.hemaya.mssdemo.model.mdlAddDevice.Response.MdlAddDeviceResponse;
import com.hemaya.mssdemo.model.synchronizeModel.SynchronizeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("provisioning/DSAPPSRPGenerateEphemeralKey")
    Call<DSAPPSRPEphemeralResponse> generateEphemeralKey(@Body DSAPPSRPEphemeralRequest dsappsrpEphemeralRequest);

    @POST("provisioning/DSAPPSRPGenerateActivationData")
    Call<ActivationDataResponse> generateActivationData(@Body ActivationDataRequest activationDataRequest);

    @POST("provisioning/MdlAddDevice")
    Call<MdlAddDeviceResponse> addDeviceMDL(@Body MdlAddDeviceRequest mdlAddDeviceRequest);

    @POST("provisioning/MdlActivate")
    Call<MdlActivateResponse> activateMDL(@Body MdlActivateRequest mdlActivateRequest);

    @POST("provisioning/getServerTime")
    Call<SynchronizeResponse> synchronize();

    @POST("customer/info")
    Call<UserInfoResponse> getInfo(@Body UserInfoRequest userInfoRequest);

    @POST("otp/send")
    Call<OtpResponse> sendOtp(@Body OtpRequest otpRequest);

    @POST("otp/verify")
    Call<OtpVerifyResponse> verifyOtp(@Body OtpVerifyRequest otpVerifyRequest);

    @POST("otp/re-send")
    Call<ResendOtpResponse> resendOtp(@Body ResendOtpRequest resendOtpRequest);

    @POST("customer/store")
    Call<StoreUserResponse> storeUser(@Body StoreUserRequest storeUserRequest);


}
