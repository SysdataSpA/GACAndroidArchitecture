package sysdata.it.androidarchitecture.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public class BaseArchitectureActivity extends AppCompatActivity {

    //subscription container
    CompositeDisposable viewSubscriptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewSubscriptionList = new CompositeDisposable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //subscriptions Dispose
        if(viewSubscriptionList != null) {
            viewSubscriptionList.dispose();
        }
    }
}
