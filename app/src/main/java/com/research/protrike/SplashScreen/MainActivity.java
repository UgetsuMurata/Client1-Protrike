package com.research.protrike.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.research.protrike.CustomViews.ProtrikeLoadingBar;
import com.research.protrike.MainFeats.Dashboard;
import com.research.protrike.R;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    ProtrikeLoadingBar display;
    Integer maxProcesses = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.loading_display);

        display.setMax(maxProcesses);
        new Thread(() -> {
            AtomicInteger process_number = new AtomicInteger(0);
            for (int i = 0; i < maxProcesses; i++) {
                process_number.getAndIncrement();
                runOnUiThread(() -> {
                    display.setProgress(process_number.get());
                });
                switch (i){
                    case 1:
                        Process1();
                        break;
                    case 2:
                        Process2();
                        break;
                    case 3:
                        Process3();
                        break;
                    default:
                        break;
                }
            }
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }).start();
    }

    private void Process1(){
        long timeStarted = System.currentTimeMillis();
        while (System.currentTimeMillis()-timeStarted<200);
    }
    private void Process2(){
        long timeStarted = System.currentTimeMillis();
        while (System.currentTimeMillis()-timeStarted<1000);
    }
    private void Process3(){
        long timeStarted = System.currentTimeMillis();
        while (System.currentTimeMillis()-timeStarted<500);
    }
}