package com.fruit.blclient;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fruit.library.bluetooth.BluetoothSPP;
import com.fruit.library.bluetooth.BluetoothState;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

  private BluetoothSPP mBluetooth;

  private static final String TAG = "MainActivity";
  private static final int RC_ACCESS_COARSE_LOCATION = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btn_start_first).setOnClickListener(this);
    findViewById(R.id.btn_stop_first).setOnClickListener(this);

    mBluetooth = new BluetoothSPP(this);

    if (!mBluetooth.isBluetoothAvailable()) {
      Toast.makeText(getApplicationContext()
          , "蓝牙不可用"
          , Toast.LENGTH_SHORT).show();
      finish();
    }

    String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
    if (!EasyPermissions.hasPermissions(this, perms)) {
      EasyPermissions.requestPermissions(this, "连接蓝牙设备需要扫描蓝牙设备权限", RC_ACCESS_COARSE_LOCATION, perms);
    }
  }

  public void onStart() {
    super.onStart();
    if (!mBluetooth.isBluetoothEnabled()) {
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
    }
  }


  @Override
  public void onClick(View v) {
    System.out.println("MainActivity.onClick");
    switch (v.getId()) {
      case R.id.btn_start_first:
        PollingUtils.startPollingService(this, 10, PollingService.class, PollingService.ACTION);
        break;
      case R.id.btn_stop_first:
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
        Intent intent = new Intent(this, PollingService.class);
        stopService(intent);
        break;
      default:
        break;
    }
  }


  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        Toast.makeText(getApplicationContext()
            , "蓝牙已开启"
            , Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getApplicationContext()
            , "设备蓝牙不可用"
            , Toast.LENGTH_SHORT).show();
        finish();
      }
    } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
      // Do something after user returned from app settings screen, like showing a Toast.
      Toast.makeText(this, "returned from app settings", Toast.LENGTH_SHORT)
          .show();
    }
  }


  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {
    Toast.makeText(this, "onPermissionsGranted", Toast.LENGTH_SHORT)
        .show();
  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    Toast.makeText(this, "onPermissionsDenied", Toast.LENGTH_SHORT).show();
    Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

    // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
    // This will display a dialog directing them to enable the permission in app settings.
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      new AppSettingsDialog.Builder(this).build().show();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // Forward results to EasyPermissions
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

}
