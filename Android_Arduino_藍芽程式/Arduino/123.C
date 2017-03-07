#include <SoftwareSerial.h>
#include <Wire.h>//引用二個函式庫SoftwareSerial及Wire
//http://blog.cavedu.com/programming-language/appinventor/%E9%9B%99a%E8%A8%88%E5%8A%83-part1%EF%BC%9Aapp-inventor-%E7%B6%93%E7%94%B1%E8%97%8D%E7%89%99%E6%8E%A7%E5%88%B6-arduino-led-%E4%BA%AE%E6%BB%85/
SoftwareSerial I2CBT(11,10);//定義PIN11及PIN10分別為RX及TX腳位

void setup()
{
	Serial.begin(9600); //Arduino起始鮑率：9600
	I2CBT.begin(57600);
	//藍牙鮑率：57600(注意！每個藍牙晶片的鮑率都不太一樣，請務必確認
	pinMode(13, OUTPUT); //設定 pin13 為輸出，LED就接在這
}

void loop() {
	byte cmmd[20];
	int insize;
	while(1)
	{
		if ((insize=(I2CBT.available()))>0)
		{ //讀取藍牙訊息
			Serial.print("input size = ");
			Serial.println(insize);
			for (int i=0; i<insize; i++)
			{
				Serial.print(cmmd[i]=char(I2CBT.read()));
				Serial.print(" ");
			}//此段請參考上一篇解釋
		}
		switch (cmmd[0])
		{ //讀取第一個字
			case 97: //97為"a"的ASCII CODE
			digitalWrite(13,HIGH); //點亮LED
			break;

			case 98://98為"b"的ASCII CODE
			Serial.println("Get b");
			digitalWrite(13,LOW); //熄滅LED
			break;
		} //Switch
	} //while
}//loop