package com.example.sysdata.gacandroidarchitecture.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sysdata.gacandroidarchitecture.viewmodel.LoginActivityViewModel;
import com.example.sysdata.gacandroidarchitecture.R;

import sysdata.it.androidarchitecture.repository.Resource;
import sysdata.it.androidarchitecture.ui.activity.BaseArchitectureActivity;

public class LoginActivity extends BaseArchitectureActivity implements View.OnClickListener{

    private EditText mUsernameValue;
    private EditText mPasswordValue;
    private Button mProceedBtn;
    private LoginActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel.class);
        mViewModel.getRetrieveLoginLiveData().observe(this, loginScreenUiModelResource -> {
            if(loginScreenUiModelResource.status == Resource.Status.SUCCESS) {
                LoginActivityViewModel.LoginScreenUiModel data = loginScreenUiModelResource.data;
                Intent intent = new Intent(this, UserLoggedActivity.class);
                intent.putExtra(UserLoggedActivity.USERNAME, data.getUsername());
                startActivity(intent);
                this.finish();
            }
        });
        mProceedBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mProceedBtn){
            Editable username = mUsernameValue.getText();
            Editable password = mPasswordValue.getText();
            mViewModel.execute(LoginActivityViewModel.Actions.LOGIN, username.toString(), password.toString());
        }
    }

    private void initView() {
        mUsernameValue = (EditText) findViewById(R.id.username_value);
        mPasswordValue = (EditText) findViewById(R.id.password_value);
        mProceedBtn = (Button) findViewById(R.id.proceed_btn);
    }


}
