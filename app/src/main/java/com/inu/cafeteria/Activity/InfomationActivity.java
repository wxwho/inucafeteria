package com.inu.cafeteria.Activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.inu.cafeteria.R;
import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by GaYoon on 2017-08-16.
 */

public class InfomationActivity extends AppCompatActivity{

    Button btnBack;
    TextView textVersion;
    String versionName;
    PackageInfo pi;


    // 글씨체 적용
    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        btnBack = (Button) findViewById(R.id.ai_btn_back);
        textVersion = (TextView)findViewById(R.id.ai_text_version);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setVersion();

    }

    private void setVersion() {
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("PackageManager", "NameNotFoundException e Errer");
        }
        versionName = pi.versionName;
        textVersion.setText(versionName);

    }


}
