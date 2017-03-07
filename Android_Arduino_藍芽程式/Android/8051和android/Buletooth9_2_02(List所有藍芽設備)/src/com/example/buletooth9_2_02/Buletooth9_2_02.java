package com.example.buletooth9_2_02;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Buletooth9_2_02 extends Activity {

	private BluetoothAdapter bluetoothAdapter;
	private int ENABLE_BULETOOTH=2;
	private TextView tv1;
	private Button btn1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buletooth9_2_02);
		bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter==null){
			Toast.makeText(this, "不支援Bluetooth", Toast.LENGTH_LONG).show();
			finish();
		}else if(!bluetoothAdapter.isEnabled()){
			Intent intent=new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, ENABLE_BULETOOTH);
			Toast.makeText(this, "啟動Bluetooth中", Toast.LENGTH_LONG).show();
		}
		findId();
		btn1.setOnClickListener(new OnClickListener() {
			private Set<BluetoothDevice> bluetooth_count;

			@Override
			public void onClick(View v) {
				bluetooth_count=bluetoothAdapter.getBondedDevices();
				if(bluetooth_count.size()>0){
					for(BluetoothDevice bluetoothdevice:bluetooth_count){
						tv1.append("name:"+bluetoothdevice.getName()+
						"  Address:"+bluetoothdevice.getAddress()+"\n");
					}
				}
				
			}
		});
		
	}

	private void findId() {
		tv1=(TextView)findViewById(R.id.textView1);
		btn1=(Button)findViewById(R.id.button1);
	}

}
