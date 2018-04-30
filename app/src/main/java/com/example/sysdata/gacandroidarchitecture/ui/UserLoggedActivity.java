package com.example.sysdata.gacandroidarchitecture.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.sysdata.gacandroidarchitecture.R;

public class UserLoggedActivity extends AppCompatActivity {

    public static final String USERNAME = "USERNAME";
    private String username;
    private TextView mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getIntent().getStringExtra(USERNAME);
        setContentView(R.layout.activity_user_logged);
        initView();
        mUsername.setText(username);
    }

    private void initView() {
        mUsername = (TextView) findViewById(R.id.username);
    }
}
