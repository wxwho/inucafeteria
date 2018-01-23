package com.inu.cafeteria.Utility;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by GaYoon on 2017-08-14.
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }


    // 뒤로가기 눌렀을 경우, 액티비티 finish
    public void onBackPressedActivity() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();

            activity.finish();
            // custompopup액티비티가 겹치지 않도록, 아예 프로세스 죽임
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }

    // 뒤로가기 눌렀을 경우, 액티비티 finish + 프로세스 죽임 (WaitingFoodNumberActivity에서만 사용)
    public void onBackPressedWaitingActivity() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();

            activity.finish();
            // custompopup액티비티가 겹치지 않도록, 아예 프로세스 죽임
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }


    public void showGuide() {
        toast = Toast.makeText(activity, "'뒤로'버튼을 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

}
