package com.fruit.blclient;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.fruit.library.bluetooth.BluetoothSPP;
import com.fruit.library.bluetooth.BluetoothState;

import java.util.Timer;
import java.util.TimerTask;


public class PollingService extends Service {

  public static final String ACTION = "com.ai.service.PollingService";

  private BluetoothSPP mBluetooth;

  @Override
  public void onCreate() {
    System.out.println("PollingService.onCreate");

    mBluetooth = new BluetoothSPP(this);

    mBluetooth.setDeviceTarget(BluetoothState.DEVICE_ANDROID);

    mBluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
      @Override
      public void onDataReceived(byte[] bytes, String s) {
        Toast.makeText(getApplicationContext()
            , s
            , Toast.LENGTH_SHORT).show();
      }
    });

    mBluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
      @Override
      public void onDeviceConnected(String name, String address) {
        Toast.makeText(getApplicationContext()
            , "已经连接到设备： " + name + "\n" + "设备地址： " + address + "\n"
            , Toast.LENGTH_SHORT).show();
        send();
      }

      @Override
      public void onDeviceDisconnected() {
        Toast.makeText(getApplicationContext()
            , "设备未连接"
            , Toast.LENGTH_SHORT).show();
        connect();
      }

      @Override
      public void onDeviceConnectionFailed() {
        Toast.makeText(getApplicationContext()
            , "连接失败！"
            , Toast.LENGTH_SHORT).show();
        connect();
      }
    });

    if (!mBluetooth.isBluetoothEnabled()) {
      Toast.makeText(getApplicationContext()
          , "蓝牙没有开启，请启动蓝牙"
          , Toast.LENGTH_SHORT).show();
    } else {
      if (!mBluetooth.isServiceAvailable()) {
        mBluetooth.setupService();
        mBluetooth.startService(BluetoothState.DEVICE_ANDROID);
      }

      connect();
    }
  }

  private void send() {
    if (mBluetooth != null) {
      mBluetooth.send("我是app", true);
    }
  }

  @Override
  public void onStart(Intent intent, int startId) {
    System.out.println("PollingService.onStart");
//        new PollingThread().start();
  }


  //    http://dsmzg.com/app/?i=S800_0|88888888|DD:BB:C6:51:86:EA|lock|2
  private void connect() {
    System.out.println("start connecting............................");
    new Handler().postDelayed(new Runnable() {
      public void run() {
        if (mBluetooth != null) {

                    mBluetooth.connect("DC:A3:AC:0A:0D:3D");//智能锁 HC LOCK F 0A0D3D
//          mBluetooth.connect("D4:50:3F:98:BA:B6");// OPPO A57
//          mBluetooth.connect("78:02:F8:D7:67:95");//小米5s
        }
      }
    }, 800); //设置至少大于500的延迟。
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    System.out.println("PollingService.onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    System.out.println("Service:onDestroy");
    mBluetooth.stopService();
    mBluetooth = null;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    System.out.println("PollingService.onBind");
    return null;
  }

//    class PollingThread extends Thread {
//        @Override
//        public void run() {
//
//            if (mBluetooth != null && !sIsConnected) {
//                System.out.println("start connecting............................");
//                mBluetooth.connect("B4:0B:44:35:EB:0A");
//            }
//            System.out.println("++++++++++++++++++++++");
//        }
//    }

}
