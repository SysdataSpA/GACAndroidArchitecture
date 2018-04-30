package com.example.sysdata.gacandroidarchitecture.repository;
import com.example.sysdata.gacandroidarchitecture.model.UserLogged;

import io.reactivex.Observable;
import sysdata.it.androidarchitecture.repository.Resource;

public class AuthRepo{

    private static AuthRepo INSTANCE;

    private AuthRepo() {
    }

    public static AuthRepo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthRepo();
        }

        return INSTANCE;
    }

    public Observable<Resource<UserLogged>> login(final String username, final String password) {
        return Observable.just(Resource.success(new UserLogged(username)));
    }
}
