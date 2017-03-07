package com.test.BTClient;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.test.BTClient.DeviceListActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
//import android.view.Menu;            //�p�ϥΥ\���[�J���T�]
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BTClient extends Activity {
	
	private final static int REQUEST_CONNECT_DEVICE = 1;    //�����w�q�d�߳]�Ʊ���X
	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP�A��UUID��
	
	private InputStream is;    //��J�y�A�Ψӱ����Ť����
	//private TextView text0;    //������ѱ���X
    private EditText edit0;    //�o�e��Ƶn������X
    private TextView dis;       //���������ܱ���X
    private ScrollView sv;      //½������X
    private String smsg = "";    //��ܥθ�ƽw�s
    private String fmsg = "";    //�O�s�θ�ƽw�s
    
    
    

    public String filename=""; //�ΨӫO�s�s�x���ɮצW
    BluetoothDevice _device = null;     //�Ť��]��
    BluetoothSocket _socket = null;      //�Ť��q�Hsocket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //������a�Ť��A�t���A�Y�Ť��]��
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   //�]�m�e�����D�e�� main.xml
        
        //text0 = (TextView)findViewById(R.id.Text0);  //�o�촣���汱��X
        edit0 = (EditText)findViewById(R.id.Edit0);   //�o���J�ر���X
        sv = (ScrollView)findViewById(R.id.ScrollView01);  //�o��½������X
        dis = (TextView) findViewById(R.id.in);      //�o������ܱ���X

       //�p�G���}���a�Ť��]�Ƥ����\�A���ܸ�T�A�����{��
        if (_bluetooth == null){
        	Toast.makeText(this, "�L�k���}����Ť��A�нT�{����O�_���Ť��\��I", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // �]�m�]�ƥi�H�Q�j��  
       new Thread(){
    	   public void run(){
    		   if(_bluetooth.isEnabled()==false){
        		_bluetooth.enable();
    		   }
    	   }   	   
       }.start();      
    }

    //�o�e����^��
    public void onSendButtonClicked(View v){
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //�Ť��s����X�y
    		byte[] bos = edit0.getText().toString().getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //��������欰0a,�N��אּ0d 0a��A�o�e
    			if(bos[i]==0x0a){
    				bos_new[n]=0x0d;
    				n++;
    				bos_new[n]=0x0a;
    			}else{
    				bos_new[n]=bos[i];
    			}
    			n++;
    		}
    		
    		os.write(bos_new);	
    	}catch(IOException e){  		
    	}  	
    }
    
    //�������ʵ��G�A�T��startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     //�s�����G�A��DeviceListActivity�]�m��^
    		// �^����^���G
            if (resultCode == Activity.RESULT_OK) {   //�s�����\�A��DeviceListActivity�]�m��^
                // MAC�a�}�A��DeviceListActivity�]�m��^
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // �o���Ť��]�Ʊ���X      
                _device = _bluetooth.getRemoteDevice(address);
 
                // �ΪA�ȸ��o��socket
                try{
                	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "�s�����ѡI", Toast.LENGTH_SHORT).show();
                }
                //�s��socket
            	Button btn = (Button) findViewById(R.id.Button03);
                try{
                	_socket.connect();
                	Toast.makeText(this, "�s��"+_device.getName()+"���\�I", Toast.LENGTH_SHORT).show();
                	btn.setText("�_�}");
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "�s�����ѡI", Toast.LENGTH_SHORT).show();
                		_socket.close();
                		_socket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "�s�����ѡI", Toast.LENGTH_SHORT).show();
                	}
                	
                	return;
                }
                
                //���}�����u�{
                try{
            		is = _socket.getInputStream();   //�o���Ť���Ƶn���y
            		}catch(IOException e){
            			Toast.makeText(this, "������ƥ��ѡI", Toast.LENGTH_SHORT).show();
            			return;
            		}
            		if(bThread==false){
            			ReadThread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}
            }
    		break;
    	default:break;
    	}
    }
    
    //������ƽu�{
    Thread ReadThread=new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//�����u�{
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					num = is.read(buffer);         //Ū�J�ƾ�
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //�O�s������
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					smsg+=s;   //�g�J�����w�s
    					if(is.available()==0)break;  //�u�ɶ��S����Ƥ~���X�i�����
    				}
    				//�o�e��ܮ����A�i����ܨ�s
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
    //�����B�z��C
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		dis.setText(smsg);   //��ܸ�� 
    		sv.scrollTo(0,dis.getMeasuredHeight()); //���ܸ�Ƴ̫�@��
    	}
    };
    
    //�����{�����γB�z����
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)  //�����s��socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //�����Ť��A��
    }
    
    //�\���B�z����
  /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {//�إߥ\���
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) { //�\���^�����
        switch (item.getItemId()) {
        case R.id.scan:
        	if(_bluetooth.isEnabled()==false){
        		Toast.makeText(this, "Open BT......", Toast.LENGTH_LONG).show();
        		return true;
        	}
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.quit:
            finish();
            return true;
        case R.id.clear:
        	smsg="";
        	ls.setText(smsg);
        	return true;
        case R.id.save:
        	Save();
        	return true;
        }
        return false;
    }*/
    
    //�s������^�����
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //�p�G�Ť��A�Ȥ��i�Ϋh����
    		Toast.makeText(this, " ���}�Ť���...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //�p���s���]�ƫh���}DeviceListActivity�i��]�Ʒj��
    	Button btn = (Button) findViewById(R.id.Button03);
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //����{���]�m
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //�]�m��^�����w�q
    	}
    	else{
    		 //�����s��socket
    	    try{
    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("�s��");
    	    }catch(IOException e){}   
    	}
    	return;
    }
    
    //�O�s����^�����
    public void onSaveButtonClicked(View v){
    	Save();
    }
    
    //�M������^�����
    public void onClearButtonClicked(View v){
    	smsg="";
    	fmsg="";
    	dis.setText(smsg);
    	return;
    }
    
    //�h�X����^�����
    public void onQuitButtonClicked(View v){
    	finish();
    }
    
    //�O�s�\���{
	private void Save() {
		//��ܹ�ܤ����J�ɮצW
		LayoutInflater factory = LayoutInflater.from(BTClient.this);  //�ϼh�d���ͦ�������X
		final View DialogView =  factory.inflate(R.layout.sname, null);  //��sname.xml�d���ͦ����Ͻd��
		new AlertDialog.Builder(BTClient.this)
								.setTitle("�ɮצW")
								.setView(DialogView)   //�]�m���Ͻd��
								.setPositiveButton("�T�w",
								new DialogInterface.OnClickListener() //�T�w����^�����
								{
									public void onClick(DialogInterface dialog, int whichButton){
										EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //�o���ɮצW��J�ر���X
										filename = text1.getText().toString();  //�o���ɮצW
										
										try{
											if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //�p�GSD�d�w�ǳƦn
												
												filename =filename+".txt";   //�b�ɮצW�����[�W.txt										
												File sdCardDir = Environment.getExternalStorageDirectory();  //�o��SD�d�ڥؿ�
												File BuildDir = new File(sdCardDir, "/data");   //���}data�ؿ��A�p���s�b�h�ͦ�
												if(BuildDir.exists()==false)BuildDir.mkdirs();
												File saveFile =new File(BuildDir, filename);  //�s�ؤ�󱱨�X�A�p�w�s�b���s�ؤ���
												FileOutputStream stream = new FileOutputStream(saveFile);  //���}�ɿ�J�y
												stream.write(fmsg.getBytes());
												stream.close();
												Toast.makeText(BTClient.this, "�s�x���\�I", Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(BTClient.this, "�S���s�x�d�I", Toast.LENGTH_LONG).show();
											}
										
										}catch(IOException e){
											return;
										}
										
										
										
									}
								})
								.setNegativeButton("����",   //��������^�����,�����h�X��ܤ����������B�z 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) { 
									}
								}).show();  //��ܹ�ܤ��
	} 
}