package arduino;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

public class ReadSerialport implements SerialPortEventListener {
	SerialPort serialPort;//定義serialport物件

	private BufferedReader input;//宣告input buffer
	private static final int TIME_OUT = 2000;//設定等待port開啟的時間，單位為毫秒
	private static final int DATA_RATE = 9600;//設定baud rate為9600

	public void initialize() {
		CommPortIdentifier portId = null;//定義CommPortIdentifier物件，控制接收接收port
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();//儲存所有有效的port

		while (portEnum.hasMoreElements()) {//掃過所有的port
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();//定義currPortId
				if (currPortId.getName().equals("COM12")) {//設定arduino serial port
					portId = currPortId;
					break;
				}	
		}
		
		if (portId == null) {//如果com port設定錯誤，會出現這一行
			System.out.println("Could not find COM port.");
			return;
		}
		
		try {//連接port
			//open serial port
			serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);

			//設定port parameters
			serialPort.setSerialPortParams(DATA_RATE,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

			//open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));


			// add event listeners
			serialPort.addEventListener(this);// Registers a SerialPortEventListener object to listen for SerialEvents.
			serialPort.notifyOnDataAvailable(true);//Expresses interest in receiving notification when input data is available.
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	//處理serial port事件,讀取資料並print出來
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {//if data available on serial port
			try {
				String inputLine=input.readLine();
				System.out.println(inputLine);
			} catch (Exception e) {
				//System.err.println(e.toString());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		ReadSerialport main = new ReadSerialport();//creates an object of the class
		main.initialize();
		System.out.println("Started");
	}
}

