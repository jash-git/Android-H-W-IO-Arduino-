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
//import android.view.Menu;            //如使用功能表加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BTClient extends Activity {
	
	private final static int REQUEST_CONNECT_DEVICE = 1;    //巨集定義查詢設備控制碼
	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服務UUID號
	
	private InputStream is;    //輸入流，用來接收藍牙資料
	//private TextView text0;    //提示欄解控制碼
    private EditText edit0;    //發送資料登錄控制碼
    private TextView dis;       //接收資料顯示控制碼
    private ScrollView sv;      //翻頁控制碼
    private String smsg = "";    //顯示用資料緩存
    private String fmsg = "";    //保存用資料緩存
    
    
    

    public String filename=""; //用來保存存儲的檔案名
    BluetoothDevice _device = null;     //藍牙設備
    BluetoothSocket _socket = null;      //藍牙通信socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //獲取本地藍牙適配器，即藍牙設備
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   //設置畫面為主畫面 main.xml
        
        //text0 = (TextView)findViewById(R.id.Text0);  //得到提示欄控制碼
        edit0 = (EditText)findViewById(R.id.Edit0);   //得到輸入框控制碼
        sv = (ScrollView)findViewById(R.id.ScrollView01);  //得到翻頁控制碼
        dis = (TextView) findViewById(R.id.in);      //得到資料顯示控制碼

       //如果打開本地藍牙設備不成功，提示資訊，結束程式
        if (_bluetooth == null){
        	Toast.makeText(this, "無法打開手機藍牙，請確認手機是否有藍牙功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // 設置設備可以被搜索  
       new Thread(){
    	   public void run(){
    		   if(_bluetooth.isEnabled()==false){
        		_bluetooth.enable();
    		   }
    	   }   	   
       }.start();      
    }

    //發送按鍵回應
    public void onSendButtonClicked(View v){
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //藍牙連接輸出流
    		byte[] bos = edit0.getText().toString().getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //手機中換行為0a,將其改為0d 0a後再發送
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
    
    //接收活動結果，響應startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     //連接結果，由DeviceListActivity設置返回
    		// 回應返回結果
            if (resultCode == Activity.RESULT_OK) {   //連接成功，由DeviceListActivity設置返回
                // MAC地址，由DeviceListActivity設置返回
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // 得到藍牙設備控制碼      
                _device = _bluetooth.getRemoteDevice(address);
 
                // 用服務號得到socket
                try{
                	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "連接失敗！", Toast.LENGTH_SHORT).show();
                }
                //連接socket
            	Button btn = (Button) findViewById(R.id.Button03);
                try{
                	_socket.connect();
                	Toast.makeText(this, "連接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
                	btn.setText("斷開");
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "連接失敗！", Toast.LENGTH_SHORT).show();
                		_socket.close();
                		_socket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "連接失敗！", Toast.LENGTH_SHORT).show();
                	}
                	
                	return;
                }
                
                //打開接收線程
                try{
            		is = _socket.getInputStream();   //得到藍牙資料登錄流
            		}catch(IOException e){
            			Toast.makeText(this, "接收資料失敗！", Toast.LENGTH_SHORT).show();
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
    
    //接收資料線程
    Thread ReadThread=new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//接收線程
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					num = is.read(buffer);         //讀入數據
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //保存收到資料
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
    					smsg+=s;   //寫入接收緩存
    					if(is.available()==0)break;  //短時間沒有資料才跳出進行顯示
    				}
    				//發送顯示消息，進行顯示刷新
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
    //消息處理佇列
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		dis.setText(smsg);   //顯示資料 
    		sv.scrollTo(0,dis.getMeasuredHeight()); //跳至資料最後一頁
    	}
    };
    
    //關閉程式掉用處理部分
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)  //關閉連接socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //關閉藍牙服務
    }
    
    //功能表處理部分
  /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {//建立功能表
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) { //功能表回應函數
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
    
    //連接按鍵回應函數
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //如果藍牙服務不可用則提示
    		Toast.makeText(this, " 打開藍牙中...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //如未連接設備則打開DeviceListActivity進行設備搜索
    	Button btn = (Button) findViewById(R.id.Button03);
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳轉程式設置
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //設置返回巨集定義
    	}
    	else{
    		 //關閉連接socket
    	    try{
    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("連接");
    	    }catch(IOException e){}   
    	}
    	return;
    }
    
    //保存按鍵回應函數
    public void onSaveButtonClicked(View v){
    	Save();
    }
    
    //清除按鍵回應函數
    public void onClearButtonClicked(View v){
    	smsg="";
    	fmsg="";
    	dis.setText(smsg);
    	return;
    }
    
    //退出按鍵回應函數
    public void onQuitButtonClicked(View v){
    	finish();
    }
    
    //保存功能實現
	private void Save() {
		//顯示對話方塊輸入檔案名
		LayoutInflater factory = LayoutInflater.from(BTClient.this);  //圖層範本生成器控制碼
		final View DialogView =  factory.inflate(R.layout.sname, null);  //用sname.xml範本生成視圖範本
		new AlertDialog.Builder(BTClient.this)
								.setTitle("檔案名")
								.setView(DialogView)   //設置視圖範本
								.setPositiveButton("確定",
								new DialogInterface.OnClickListener() //確定按鍵回應函數
								{
									public void onClick(DialogInterface dialog, int whichButton){
										EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //得到檔案名輸入框控制碼
										filename = text1.getText().toString();  //得到檔案名
										
										try{
											if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //如果SD卡已準備好
												
												filename =filename+".txt";   //在檔案名末尾加上.txt										
												File sdCardDir = Environment.getExternalStorageDirectory();  //得到SD卡根目錄
												File BuildDir = new File(sdCardDir, "/data");   //打開data目錄，如不存在則生成
												if(BuildDir.exists()==false)BuildDir.mkdirs();
												File saveFile =new File(BuildDir, filename);  //新建文件控制碼，如已存在仍新建文檔
												FileOutputStream stream = new FileOutputStream(saveFile);  //打開檔輸入流
												stream.write(fmsg.getBytes());
												stream.close();
												Toast.makeText(BTClient.this, "存儲成功！", Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(BTClient.this, "沒有存儲卡！", Toast.LENGTH_LONG).show();
											}
										
										}catch(IOException e){
											return;
										}
										
										
										
									}
								})
								.setNegativeButton("取消",   //取消按鍵回應函數,直接退出對話方塊不做任何處理 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) { 
									}
								}).show();  //顯示對話方塊
	} 
}