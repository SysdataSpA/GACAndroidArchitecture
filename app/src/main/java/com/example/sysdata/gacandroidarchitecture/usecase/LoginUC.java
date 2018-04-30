package com.example.sysdata.gacandroidarchitecture.usecase;

import android.os.Bundle;

import com.example.sysdata.gacandroidarchitecture.repository.AuthRepo;
import com.example.sysdata.gacandroidarchitecture.model.UserLogged;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import sysdata.it.androidarchitecture.repository.Resource;
import sysdata.it.androidarchitecture.usecase.BaseUseCase;

public class LoginUC extends BaseUseCase<Resource<UserLogged>> {

    public static final String BUNDLE_USERNAME = "RetrieveLoginUC.Bundle.Username";
    public static final String BUNDLE_PASSWORD = "RetrieveLoginUC.Bundle.Password";

    /**
     * Will setup the 'execution' Scheduler and the 'emission' Scheduler to use.
     * Then it will perform two operations:
     *
     * @param threadExecutor the {@link Scheduler} on which to execute the work
     * @param postExecutionThread the {@link Scheduler} on which to emit notifications
     */
    public LoginUC(Scheduler threadExecutor, Scheduler postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    protected Observable<Resource<UserLogged>> buildUseCaseObservable(Bundle b) {
        // parameters retrieved from ui
        String username = "";
        String password = "";
        if (b != null) {
            username = b.getString(BUNDLE_USERNAME);
            password = b.getString(BUNDLE_PASSWORD);
        }
        return AuthRepo.getInstance().login(username, password);
    }
}
