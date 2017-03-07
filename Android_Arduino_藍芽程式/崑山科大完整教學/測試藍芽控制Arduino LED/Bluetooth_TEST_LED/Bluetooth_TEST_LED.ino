//Bluetooth TEST LED

const int ledPin =8;
int incomingByte;

void setup(){
  Serial.begin(57600);
  pinMode(ledPin,OUTPUT);
}

void loop(){
  
  if(Serial.available()>0){
  incomingByte = Serial.read();
  if(incomingByte == 'H'){
    digitalWrite(ledPin,HIGH);
  }
  if(incomingByte == 'L'){
    digitalWrite(ledPin,LOW);
  }
 }
}
  
