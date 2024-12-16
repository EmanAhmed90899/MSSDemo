package com.hemaya.mssdemo.network;

import com.hemaya.mssdemo.model.activationData.Request.ActivationDataRequest;
import com.hemaya.mssdemo.model.activationData.Response.ActivationDataResponse;
import com.hemaya.mssdemo.model.ephemeral.request.DSAPPSRPEphemeralRequest;
import com.hemaya.mssdemo.model.ephemeral.response.DSAPPSRPEphemeralResponse;
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




}
