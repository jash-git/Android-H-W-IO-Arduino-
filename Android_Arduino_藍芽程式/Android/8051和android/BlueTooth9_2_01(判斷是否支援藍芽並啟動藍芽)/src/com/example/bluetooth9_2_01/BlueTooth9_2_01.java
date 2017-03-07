package com.example.bluetooth9_2_01;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.Toast;

public class BlueTooth9_2_01 extends Activity {

	private static final int ENABLE_BULETOOTH = 2;
	private BluetoothAdapter bluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue_tooth9_2_01);
		bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter==null){
			Toast.makeText(this, "不支援Bluetooth", Toast.LENGTH_LONG).show();
    	finish();
		
      }else if(!bluetoothAdapter.isEnabled()){
    	Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	startActivityForResult(intent,ENABLE_BULETOOTH);
    	Toast.makeText(this, "啟動Bluetooth中", Toast.LENGTH_LONG).show();
      }
	}

	
}
