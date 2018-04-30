package com.example.sysdata.gacandroidarchitecture.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.util.Log;

import com.example.sysdata.gacandroidarchitecture.usecase.LoginUC;
import com.example.sysdata.gacandroidarchitecture.model.UserLogged;

import sysdata.it.androidarchitecture.repository.Resource;
import sysdata.it.androidarchitecture.usecase.BaseUsecaseObserver;
import sysdata.it.androidarchitecture.viewmodel.BaseArchitectureViewModel;
import sysdata.it.androidarchitecture.viewmodel.UCAction;
import sysdata.it.androidarchitecture.viewmodel.UCPrepareBundle;

public class LoginActivityViewModel extends BaseArchitectureViewModel {

    final private MutableLiveData<Resource<LoginActivityViewModel.LoginScreenUiModel>> lvRetrieveLoginLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }

    @Override
    protected void onReleaseObservers() {

    }

    @UCAction(actions = {Actions.LOGIN})
    private final BaseUsecaseObserver getLoginUCObserver = BaseUsecaseObserver.buildOn(LoginUC.class).map(lvRetrieveLoginLiveData, userLoggedResource -> {
        LoginScreenUiModel returnValue = new LoginScreenUiModel();
        if(userLoggedResource.status == Resource.Status.SUCCESS){
            UserLogged userLogged = userLoggedResource.data;
            returnValue.setUsername(userLogged.getmUsername());
        }
        return Resource.mappingData(userLoggedResource, returnValue);
    }).build();

    @UCPrepareBundle(actionName = Actions.LOGIN)
    public Bundle prepareLoginAction(String email, String password) {
        Bundle params = new Bundle();
        params.putString(LoginUC.BUNDLE_USERNAME, email);
        params.putString(LoginUC.BUNDLE_PASSWORD, password);
        return params;
    }

    public MutableLiveData<Resource<LoginScreenUiModel>> getRetrieveLoginLiveData() {
        return lvRetrieveLoginLiveData;
    }

    public interface Actions {
        String LOGIN = "onLogin";
    }

    public static class LoginScreenUiModel {
        private String username;

        public LoginScreenUiModel() {
        }

        public LoginScreenUiModel(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
