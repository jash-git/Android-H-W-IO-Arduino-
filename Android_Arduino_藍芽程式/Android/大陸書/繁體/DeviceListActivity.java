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
    // �ոե�
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // ��^�ɸ�Ƽ���
    public static String EXTRA_DEVICE_ADDRESS = "�]�Ʀ�}";

    // ������
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // �Ыب���ܵ���
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  //�]�m������ܼҦ��������覡
        setContentView(R.layout.device_list);

        // �]�w�q�{��^�Ȭ�����
        setResult(Activity.RESULT_CANCELED);

        // �]�w���y����^��
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // ��ϤƳ]�Ʀs�x�}�C
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // �]�m�w�t���]�ƦC��
        
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // �]�m�s�d��]�ƦC��
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // ���U�����d���]��action������
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // ���U�d�䵲��action������
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // �o�쥻�a�Ť�����X
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // �o��w�t���Ť��]�ƦC��
        //Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // �K�[�w�t��]�ƨ�C������ 
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

        // �����A�Ȭd��
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // ���Paction������
        this.unregisterReceiver(mReceiver);
    }
    
    public void OnCancel(View v){
    	finish();
    }
    /**
     * �}�l�A�ȩM�]�Ƭd��
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // �b������ܬd�䤤��T
        setProgressBarIndeterminateVisibility(true);
        setTitle("�d��]�Ƥ�...");

        // ��ܨ�L�]�ơ]���t��]�ơ^�C��
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // �����A�i�檺�A�Ȭd��
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //�í��s�}�l
        mBtAdapter.startDiscovery();
    }

    // ��ܳ]�Ʀ^����� 
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // �ǳƳs���]�ơA�����A�Ȭd��
            mBtAdapter.cancelDiscovery();

            // �o��mac�a�}
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // �]�m��^�ƾ�
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // �]�m��^�Ȩõ����{��
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // �d���]�ƩM�j������action��ť��
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // �d���]��action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // �o���Ť��]��
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // �p�G�O�w�t�諸�h���L�A�w�o����ܡA��l���b�K�[��C���i�����
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }else{  //�K�[��w�t��]�ƦC��
                	mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // �j������action
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("��ܭn�s�����]��");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "�S�����s�]��";
                    mNewDevicesArrayAdapter.add(noDevices);
                }
             //   if(mPairedDevicesArrayAdapter.getCount() > 0)
              //  	findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            }
        }
    };


}
