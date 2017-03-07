package com.test.BTClient;



import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BTClient extends Activity {
	
	private TextView text0;
    private EditText edit0;
    
  //��ť��������������˿ڣ���UUID����

    BluetoothDevice _device = null;
    BluetoothSocket _socket = null;
    boolean _discoveryFinished = false;
	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text0 = (TextView)findViewById(R.id.Text0);
        edit0 = (EditText)findViewById(R.id.Edit0);

        
      //�򿪱����豸
        if (_bluetooth == null){
        	Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
          //  finish();
            return;
        }
        
        // �����豸���Ա�����  
        _bluetooth.enable();
        if(_bluetooth.isEnabled()==false){
        	Toast.makeText(this, "Bluetooth can`t be discorvered", Toast.LENGTH_LONG).show();
        	//finish();
        	return;
        }
        
        
        text0.setText("���������������ɹ�!");
    }
    

    //����㲥������
    private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			/* ��intent��ȡ������������� */
			//BluetoothDevice _device0 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			//if (_device0.getName().equals("linvor") )
				_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);;
			text0.setText("��Ѱ���豸:"+_device.getName());
		}
	};
	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			/* ж��ע��Ľ����� */
			unregisterReceiver(_foundReceiver);
			unregisterReceiver(this);
			_discoveryFinished = true;
		}
	};
    
    //������Ѱ����
	public void onSearchButtonClicked(View v){
		
		IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(_discoveryReceiver, discoveryFilter);
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(_foundReceiver, foundFilter);
		new Thread(){
    		public void run() 
    		{
    			//��ʼ���� 
    			_bluetooth.startDiscovery();
    			for (;;) 
    			{
    				if (_discoveryFinished) 
    				{
    					break;
    				}
    				try 
    				{
    					Thread.sleep(100);
    				} 
    				catch (InterruptedException e){}
    			}
    		}
    	}.start();
    	text0.setText("������Ѱ�豸��");

	}
	
	//�����豸
    public void onConenectButtonClicked(View v){
    	/*_device = _bluetooth.getRemoteDevice("00:10:20:26:00:04");
    	
    	if(_device==null){
    		Toast.makeText(this, "Bluetooth is not found", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
    	
    
    	//�����豸
    	try{
    		_bluetooth.cancelDiscovery();
    		
    		_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
    		text0.setText("�򿪷���ɹ�");
    	}catch(IOException e){
    		text0.setText("�򿪷��񲻳ɹ�");
    		return;
    	}
    	
    	if(_socket==null){
    		text0.setText("������ʧ��");
    		return;
    	}*/
    	try{
    		_socket.connect();
    		text0.setText("�ɹ��������ӣ����Է������ݣ�"+_device.getName());
    	}catch(IOException e){
    		//text0.setText(e.toString());
    		text0.setText("����ʧ��");
    	}
    	
    	
    }
    
    public void onSendButtonClicked(View v){
    	try{
    		OutputStream os = _socket.getOutputStream();
    		os.write(edit0.getText().toString().getBytes());
    		os.close();
    		text0.setText("�������ݳɹ���");
    		
    	}catch(IOException e){
    		
    	}
    	
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)
    	try{
    		_socket.close();
    	}catch(IOException e){}
    }
}