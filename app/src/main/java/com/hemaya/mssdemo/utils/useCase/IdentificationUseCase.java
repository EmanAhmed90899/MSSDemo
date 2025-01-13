package com.hemaya.mssdemo.utils.useCase;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.ActivationModel;
import com.hemaya.mssdemo.model.identification.UserInfoRequest.UserInfoRequest;
import com.hemaya.mssdemo.model.identification.otpRequest.OtpRequest;
import com.hemaya.mssdemo.model.identification.otpResponse.OtpResponse;
import com.hemaya.mssdemo.model.identification.otpVerifyRequest.OtpVerifyRequest;
import com.hemaya.mssdemo.model.identification.otpVerifyResponse.OtpVerifyResponse;
import com.hemaya.mssdemo.model.identification.resendOtpRequest.ResendOtpRequest;
import com.hemaya.mssdemo.model.identification.resendOtpResponse.ResendOtpResponse;
import com.hemaya.mssdemo.model.identification.userInfoResponse.UserInfoResponse;
import com.hemaya.mssdemo.network.ApiClient;
import com.hemaya.mssdemo.network.ApiService;
import com.hemaya.mssdemo.network.domain.AuthData;
import com.hemaya.mssdemo.network.interceptor.DynamicHeaderInterceptor;
import com.hemaya.mssdemo.utils.validator.PhoneNumberValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IdentificationUseCase {
    Context context;
    String mobileNumber, userName, nationalId, passportNumber, uuid;
    IdentificationUseCaseListener identificationUseCaseListener;
    Gson gson;
    ActivationModel activationModel;

    public void setIdentificationUseCaseListener(IdentificationUseCaseListener identificationUseCaseListener) {
        this.identificationUseCaseListener = identificationUseCaseListener;
    }

    public IdentificationUseCase(Context context) {
        this.context = context;
        gson = new Gson();
        activationModel = new ActivationModel();
        activationModel.clear();
    }

    public void validateUser(String national) {
        UserInfoRequest userInfoRequest = new UserInfoRequest(national.trim());
        String jsonString = gson.toJson(userInfoRequest);

        String signature = AuthData.generateHmac(jsonString);
        Call<UserInfoResponse> validateUserResponseCall = getRetrofit(signature).getInfo(userInfoRequest);
        validateUserResponseCall.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                identificationUseCaseListener.hideProgress();
//                if (UserInfoResponse != null) {
                if (response.isSuccessful()) {
                    UserInfoResponse UserInfoResponse = response.body();
                    if (UserInfoResponse.getData() != null) {
                        mobileNumber = UserInfoResponse.getData().getPhone();
                        if (!mobileNumber.contains("+")) {
                            mobileNumber = "+" + mobileNumber;
                        }
                        userName = UserInfoResponse.getData().getVascoId();
                        nationalId = UserInfoResponse.getData().getNationalId();
                        identificationUseCaseListener.onValidateUserSuccess(mobileNumber, userName, nationalId, passportNumber);
                    }
                }

            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                identificationUseCaseListener.hideProgress();
                if (t.getMessage().contains("Failed to connect")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.something_went_wrong));
                } else if (t.getMessage().toLowerCase().contains("unable to resolve host")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.no_internet_connection));
                } else {
                    identificationUseCaseListener.onFailure(t.getMessage());
                }
            }
        });

    }

    public void checkMobile(String phone, String region) {
        PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
        phone = phoneNumberValidator.getValidPhone(phone, region);
        if (phone.equals(mobileNumber)) {
            sendOtp();
        } else {
            identificationUseCaseListener.hideProgress();
            identificationUseCaseListener.onFailure(context.getResources().getString(R.string.mobile_number_error));
        }

    }

    public void sendOtp() {
        OtpRequest otpRequest = new OtpRequest(mobileNumber, 6);
        String jsonString = gson.toJson(otpRequest);
        String signature = AuthData.generateHmac(jsonString);

        Call<OtpResponse> otpResponseCall = getRetrofit(signature).sendOtp(otpRequest);
        otpResponseCall.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                identificationUseCaseListener.hideProgress();
                if (response.isSuccessful()) {
                    OtpResponse otpResponse = response.body();

                    if (otpResponse.getData() != null) {
                        identificationUseCaseListener.onMobileSuccess();
                        uuid = otpResponse.getData().getUuid();
                        identificationUseCaseListener.onOtpSentSuccess(uuid);
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                identificationUseCaseListener.hideProgress();
                if (t.getMessage().contains("Failed to connect")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.something_went_wrong));
                } else if (t.getMessage().toLowerCase().contains("unable to resolve host")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.no_internet_connection));
                } else {
                    identificationUseCaseListener.onFailure(t.getMessage());
                }
            }
        });
    }

    public void validateOtp(String otp) {
        OtpVerifyRequest otpVerifyRequest = new OtpVerifyRequest(otp, uuid);
        String jsonString = gson.toJson(otpVerifyRequest);
        String signature = AuthData.generateHmac(jsonString);
        Call<OtpVerifyResponse> otpResponseCall = getRetrofit(signature).verifyOtp(otpVerifyRequest);

        otpResponseCall.enqueue(new Callback<OtpVerifyResponse>() {
            @Override
            public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                identificationUseCaseListener.hideProgress();
                if (response.isSuccessful()) {
                    OtpVerifyResponse otpVerifyResponse = response.body();
                    if (otpVerifyResponse.getData() != null) {
                        if(!otpVerifyResponse.getData().isVerified()) {
                            identificationUseCaseListener.onOtpVerifiedSuccess();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                identificationUseCaseListener.hideProgress();
                if (t.getMessage().contains("Failed to connect")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.something_went_wrong));
                } else if (t.getMessage().toLowerCase().contains("unable to resolve host")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.no_internet_connection));
                } else {
                    identificationUseCaseListener.onFailure(t.getMessage());
                }
            }
        });

    }


    public void resendOtp() {
        ResendOtpRequest resendOtpRequest = new ResendOtpRequest(uuid);
        String jsonString = gson.toJson(resendOtpRequest);
        String signature = AuthData.generateHmac(jsonString);
        Call<ResendOtpResponse> resendOtpResponseCall = getRetrofit(signature).resendOtp(resendOtpRequest);
        resendOtpResponseCall.enqueue(new Callback<ResendOtpResponse>() {
            @Override
            public void onResponse(Call<ResendOtpResponse> call, Response<ResendOtpResponse> response) {
                if (response.isSuccessful()) {
                    ResendOtpResponse resendOtpResponse = response.body();
                    if (resendOtpResponse.getData() != null) {
                        uuid = resendOtpResponse.getData().getUuid();
                        identificationUseCaseListener.onResendOtpSuccess(resendOtpResponse.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResendOtpResponse> call, Throwable t) {
                if (t.getMessage().contains("Failed to connect")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.something_went_wrong));
                } else if (t.getMessage().toLowerCase().contains("unable to resolve host")) {
                    identificationUseCaseListener.onFailure(context.getResources().getString(R.string.no_internet_connection));
                } else {
                    identificationUseCaseListener.onFailure(t.getMessage());
                }
            }
        });

    }


    ApiService getRetrofit(String header) {
        DynamicHeaderInterceptor headerInterceptor = new DynamicHeaderInterceptor(header, context);
        headerInterceptor.updateHeaderValue(header);
        Retrofit retrofit = ApiClient.getClient(header, context);
        ApiService apiService = retrofit.create(ApiService.class);
        return apiService;
    }

    public interface IdentificationUseCaseListener {
        void onValidateUserSuccess(String mobileNumber, String userName, String nationalId, String passportNumber);

        void onFailure(String message);

        void onErrorMessage(String message);

        void onOtpSentSuccess(String uuid);

        void onMobileSuccess();

        void onOtpVerifiedSuccess();

        void onResendOtpSuccess(String message);

        void hideProgress();
    }
}
