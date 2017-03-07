/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.BTClient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class DeviceListActivity extends Activity {
    // 調試用
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // 返回時資料標籤
    public static String EXTRA_DEVICE_ADDRESS = "設備位址";

    // 成員域
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 創建並顯示視窗
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  //設置視窗顯示模式為視窗方式
        setContentView(R.layout.device_list);

        // 設定默認返回值為取消
        setResult(Activity.RESULT_CANCELED);

        // 設定掃描按鍵回應
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // 初使化設備存儲陣列
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // 設置已配隊設備列表
        
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 設置新查找設備列表
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // 註冊接收查找到設備action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 註冊查找結束action接收器
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // 得到本地藍牙控制碼
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // 得到已配對藍牙設備列表
        //Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // 添加已配對設備到列表並顯示 
       // if (pairedDevices.size() > 0) {
           // findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
        //    for (BluetoothDevice device : pairedDevices) {
       //         mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
       //     }
       // } else {
       //     String noDevices = "No devices have been paired";
       //     mPairedDevicesArrayAdapter.add(noDevices);
       // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 關閉服務查找
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // 註銷action接收器
        this.unregisterReceiver(mReceiver);
    }
    
    public void OnCancel(View v){
    	finish();
    }
    /**
     * 開始服務和設備查找
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // 在視窗顯示查找中資訊
        setProgressBarIndeterminateVisibility(true);
        setTitle("查找設備中...");

        // 顯示其他設備（未配對設備）列表
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 關閉再進行的服務查找
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //並重新開始
        mBtAdapter.startDiscovery();
    }

    // 選擇設備回應函數 
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 準備連接設備，關閉服務查找
            mBtAdapter.cancelDiscovery();

            // 得到mac地址
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // 設置返回數據
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // 設置返回值並結束程式
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // 查找到設備和搜索完成action監聽器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 查找到設備action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到藍牙設備
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果是已配對的則略過，已得到顯示，其餘的在添加到列表中進行顯示
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }else{  //添加到已配對設備列表
                	mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // 搜索完成action
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("選擇要連接的設備");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "沒有找到新設備";
                    mNewDevicesArrayAdapter.add(noDevices);
                }
             //   if(mPairedDevicesArrayAdapter.getCount() > 0)
              //  	findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            }
        }
    };


}
