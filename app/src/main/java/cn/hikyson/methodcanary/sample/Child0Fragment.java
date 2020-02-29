package cn.hikyson.methodcanary.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class Child0Fragment extends BaseFragment {

    public Child0Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        Log.d("MethodCanary", "Child0Fragment onAttach0");
        super.onAttach(context);
        Log.d("MethodCanary", "Child0Fragment onAttach1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MethodCanary", "Child0Fragment onCreateView0");
        View view = new View(container.getContext());
        Log.d("MethodCanary", "Child0Fragment onCreateView1");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
