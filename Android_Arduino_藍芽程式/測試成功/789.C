#include <SoftwareSerial.h>
#include <Wire.h>//引用二個函式庫SoftwareSerial及Wire
//http://blog.cavedu.com/programming-language/appinventor/appinventorandarduinowithbluetooth2/
SoftwareSerial I2CBT(10,11);//定義PIN10及PIN11分別為RX及TX腳位

byte cmmd[20];
int insize, a;

void setup()
{

   Serial.begin(9600); //Arduino起始鮑率：9600
   I2CBT.begin(9600); //藍牙鮑率：57600(注意！鮑率每個藍牙晶片不一定相同，請務必確認
   pinMode(9, OUTPUT);  //請注意您使用的Arduino 該腳位是否支援 PWM (會有~符號），否則會看不到效果
   digitalWrite(9,HIGH); //點亮LED
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
			digitalWrite(9,HIGH); //點亮LED
			break;

			case 98://98為"b"的ASCII CODE
			Serial.println("Get b");
			digitalWrite(13,LOW); //熄滅LED
			digitalWrite(9,LOW); //熄滅LED
			break;
		} //Switch
	} //while
}