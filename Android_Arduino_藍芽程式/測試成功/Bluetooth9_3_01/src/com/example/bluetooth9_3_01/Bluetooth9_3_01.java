package com.example.bluetooth9_3_01;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Bluetooth9_3_01 extends Activity implements OnItemClickListener{
	private BluetoothAdapter bluetoothAdapter;
	private int ENABLE_BULETOOTH=2;
	private ArrayList<HashMap<String, String>> arrayList;
	private SimpleAdapter adapter;
	private ListView listmenu;
	private BlueToothReceiver receiver;
	private BufferedOutputStream out;
	private BufferedInputStream in;
	private BluetoothSocket socket;
    private final UUID MY_UUID=UUID.fromString
    		("00001101-0000-1000-8000-00805F9B34FB");
	private Button end;
	private Button send;
	private TextView tv1;
	private byte[] array_51={'b','a'};
	private boolean m_blnchange=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth9_3_01);
        findId();
		
		bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
        	Toast.makeText(this, "不支援Bluetooth", Toast.LENGTH_LONG).show();
        	finish();
        }else if(!bluetoothAdapter.isEnabled()){
        	Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(intent, ENABLE_BULETOOTH);
        }
        arrayList=new ArrayList<HashMap<String,String>>();
        if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();
		findBluetooth() ;
		listmenu.setOnItemClickListener( this);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try
				{
					if(!m_blnchange)
					{
						out.write(array_51[0]);
					}
					else
					{
						out.write(array_51[1]);
					}
					out.flush();
					m_blnchange=!m_blnchange;
				} catch (IOException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
				}					
			}
		});	
		end.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 try {
				    	out.close();
				    	in.close();
					    socket.close();
					    finish();
					   } catch (IOException e) {
					    e.printStackTrace();
					    android.os.Process.killProcess(android.os.Process.myPid());
				       }
				
			}
		});
		
	}

	private void findBluetooth() {
		
        receiver=new BlueToothReceiver();
        IntentFilter filter=new IntentFilter();
                     filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                     filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        adapter=new SimpleAdapter(this, arrayList, R.layout.bluetoothitem,
        		new String[]{"arrayName","arrayAddress"},
        		new int[]{R.id.Bluetooth_Name, R.id.Bluetooth_Address});
        listmenu.setAdapter(adapter);
	}

	public class BlueToothReceiver extends BroadcastReceiver{
        @Override
		public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		if(BluetoothDevice.ACTION_FOUND.equals(action)){
			BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("arrayName", device.getName());
			map.put("arrayAddress", device.getAddress());
			arrayList.add(map);
		}else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
			BluetoothDevice device1=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			switch(device1.getBondState()){
			case BluetoothDevice.BOND_BONDING:
				 tv1.setText("配對中");
				 break;
			case BluetoothDevice.BOND_BONDED:
				 tv1.setText("配對完成");
				 break;
			case BluetoothDevice.BOND_NONE:
				 tv1.setText("配對取消");
				 break;
			default:
				break;
				 
			}
		}
	  }
	}
	
public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
	bluetoothAdapter.cancelDiscovery();
            Set<BluetoothDevice> device1=bluetoothAdapter.getBondedDevices();
			BluetoothDevice device =bluetoothAdapter.getRemoteDevice
					              (arrayList.get(arg2).get("arrayAddress"));
			                
			
			
			try{
				socket=device.createRfcommSocketToServiceRecord(MY_UUID);
				socket.connect();
				out=new BufferedOutputStream(socket.getOutputStream());
				in=new BufferedInputStream(socket.getInputStream());
				 tv1.setText("連線成功");
			}catch(IOException e){
			}
		}
	
	private void findId() {
		tv1=(TextView)findViewById(R.id.textView1);
		end=(Button)findViewById(R.id.button2);
		send=(Button)findViewById(R.id.button1);
		listmenu=(ListView) findViewById(R.id.bluetooth_list);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(receiver!=null)
		unregisterReceiver(receiver);
		Toast.makeText(this, "結束", 2000).show();
	}

}
