package com.dftc.deviceonoff;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mShutdownButton , mCloseScreenBt,mRebootBt;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAdmin();
        setContentView(R.layout.activity_main);
        mShutdownButton = (Button)findViewById(R.id.avtivity_main_shutdown);
        mShutdownButton.setOnClickListener(this);
        mCloseScreenBt = (Button)findViewById(R.id.avtivity_main_closeScreen);
        mCloseScreenBt.setOnClickListener(this);
        mRebootBt = (Button)findViewById(R.id.avtivity_main_reboot);
        mRebootBt.setOnClickListener(this);
        findViewById(R.id.activity_main).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avtivity_main_shutdown:
                shutDown();
                break;
            case R.id.avtivity_main_closeScreen:
                closeScreen();
                break;
            case R.id.avtivity_main_reboot:
                rebootDevice();
                break;
            case R.id.activity_main:

                break;
        }
    }

    /**
     * 申请超级管理员权限，非常重要！！！！
     * 申请成功之后应用就无法卸载。。。。。
     *
     */

    private  DevicePolicyManager devicePolicyManager;

    private void getAdmin(){
        //获取系统管理权限
         devicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //申请权限
        ComponentName componentName = new ComponentName(this, MyAdmin.class);
        //判断该组件是否有系统管理员的权限
        boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
        Log.e(TAG, "getAdmin: "  + isAdminActive );
        if (isAdminActive == true){
//         devicePolicyManager.lockNow();
        }else{
            // 没有管理员权限---启动系统activity让用户激活管理员权限
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "只有激活了管理员权限才能息屏");
            startActivityForResult(intent, 0);
        }
    }

    private void closeScreen(){
        Toast.makeText(this,"3秒之后息屏",Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                devicePolicyManager.lockNow();
            }
        },3 * 1000);


        /**
         *  powerManager.gotoSleep可以强制息屏，但只有系统级应用才能调用pm.gotoSleep，即使申请了相关权限也没有
         */

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: 即将亮屏" );

                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                Log.e(TAG, "isScreenOn: " + pm.isScreenOn() );
                PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "TAG");
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                wakeLock.acquire();                     //测试小米电视无效果
//                wakeLock.release();
//                keyguardManager.newKeyguardLock("").disableKeyguard();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //这个方法也没有效果
                Log.e(TAG, "run: 亮屏成功" );

                String cmd = "adb shell input keyevent 26";//模拟手机按下开机键
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 10 * 1000);

    }

    private void openScreen(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10 *1000 );

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void openDevice(){
//        AlarmManager alarmAnager = (AlarmManager)getSystemService(ALARM_SERVICE);
//
//        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MyBroadcastReceiver.class), Intent.FLAG_ACTIVITY_NEW_TASK);
//        start_am.setInexactRepeating(AlarmManager.RTC_WAKEUP , Start_time, 1000 * 60 * 60 * 24, intent);
    }


    /**
     * 测试了多种关机方法均告失败，或者没反应，或者导致小米电视死机
     */
    private void shutDown(){
        Log.e(TAG, "shutDown=================");
        Toast.makeText(this,"即将关机",Toast.LENGTH_SHORT).show();

//        try {
//            Process process;
//            process = Runtime.getRuntime().exec("su");
//            DataOutputStream out = new DataOutputStream(
//                    process.getOutputStream());
//            out.writeBytes("reboot -p\n");
//            out.writeBytes("exit\n");
//            out.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//            Process process = Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p" });
//            process.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(
//                "com.android.settings.action.REQUEST_POWER_OFF");
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
//                intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        am.set(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), pendingIntent);

//        try {
//
//            //获得ServiceManager类
//            Class<?> ServiceManager = Class
//                    .forName("android.os.ServiceManager");
//            //获得ServiceManager的getService方法
//            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
//            //调用getService获取RemoteService
//            Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);
//            //获得IPowerManager.Stub类
//            Class<?> cStub = Class
//                    .forName("android.os.IPowerManager$Stub");
//            //获得asInterface方法
//            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
//            //调用asInterface方法获取IPowerManager对象
//            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
//            //获得shutdown()方法
//            Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
//            //调用shutdown()方法
//            shutdown.invoke(oIPowerManager,false,true);
//        } catch (Exception e) {
//            Log.e(TAG, e.toString(), e);
//        }


//        String cmd = "su -c reboot -p";//让手机关机
//        try {
//            Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p"});
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        Log.e(TAG, "已经关机" );
    }

    private void rebootDevice(){
        String cmd = "su -c reboot";//让手机从启
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
