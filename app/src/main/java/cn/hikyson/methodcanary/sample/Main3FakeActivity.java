package cn.hikyson.methodcanary.sample;


import android.os.Bundle;

public class Main3FakeActivity {

    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onStop() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
    }

    protected void onStart() {
    }

    protected void onResume() {
    }


}
