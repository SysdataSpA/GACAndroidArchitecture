package sysdata.it.androidarchitecture.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Brando Baldassarre on 19/12/2017.
 */

public class BaseArchitectureFragment extends Fragment {

    //subscription container
    protected CompositeDisposable viewSubscriptionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewSubscriptionList = new CompositeDisposable();
    }

    @Override
    public void onPause() {
        super.onPause();
        //subscriptions Dispose
        if(viewSubscriptionList != null) {
            viewSubscriptionList.dispose();
        }
    }

    /**
     * Return true if the {@link Fragment} this fragment is currently associated with
     * is not <code>null</code> and the fragment is currently added to it.
     */
    public boolean isFragmentAttached() {
        return getActivity() != null && isAdded();
    }
}
