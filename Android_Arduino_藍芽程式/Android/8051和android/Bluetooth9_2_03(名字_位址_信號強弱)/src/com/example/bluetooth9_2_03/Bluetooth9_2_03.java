package com.example.bluetooth9_2_03;


import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Bluetooth9_2_03 extends Activity {
	

	private BluetoothAdapter bluetoothAdapter;
	private Bluetoothreceiver btreceiver;
	private int ENABLE_BULETOOTH=2;
	private TextView tv1;
	private Button Discovery;
	private Button end;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth9_2_03);
		bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter==null){
			Toast.makeText(this, "不支援Bluetooth", Toast.LENGTH_LONG).show();
			finish();
		}else if(!bluetoothAdapter.isEnabled()){
			Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, ENABLE_BULETOOTH);
			Toast.makeText(this, "啟動Bluetooth中", Toast.LENGTH_LONG).show();
		}
		findId();
		Discovery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
				tv1.setText("搜尋中");
				bluetoothAdapter.startDiscovery();
				
				btreceiver=new Bluetoothreceiver();
				IntentFilter infilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
				registerReceiver(btreceiver, infilter);
			}
		});
		
		end.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	public class Bluetoothreceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			tv1.setText("");
			String action=intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice bluetoothdevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(bluetoothdevice.getBondState()!=BluetoothDevice.BOND_BONDED){
					 short rssi = intent.getExtras().getShort( 
                             BluetoothDevice.EXTRA_RSSI); 
				tv1.append("名字:"+bluetoothdevice.getName()+" 遠端藍牙信號強弱:"+rssi+
						   " 位址:"+bluetoothdevice.getAddress()+"\n");
				}
			}
			
		}
	}
	
	private void findId() {
		tv1=(TextView)findViewById(R.id.textView1);
		end=(Button)findViewById(R.id.button2);
		Discovery=(Button)findViewById(R.id.button1);
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		if(btreceiver!=null)
		unregisterReceiver(btreceiver);
		Toast.makeText(this, "結束", 2000).show();
	}
	

}
