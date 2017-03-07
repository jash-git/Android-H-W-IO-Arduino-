/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.

  Most Arduinos have an on-board LED you can control. On the Uno and
  Leonardo, it is attached to digital pin 13. If you're unsure what
  pin the on-board LED is connected to on your Arduino model, check
  the documentation at http://arduino.cc

  This example code is in the public domain.

  modified 8 May 2014
  by Scott Fitzgerald
 */

char cin;
// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin 13 as an output.
  pinMode(13, OUTPUT);
  pinMode(A2, INPUT);
  cin='X';
  Serial.begin(9600);
}

// the loop function runs over and over again forever
void loop() {

}

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  while (Serial.available()) {
    cin = Serial.read();
    switch(cin)
    {
      //fbrls
      case 'F':
      case 'f':
        Serial.print("F or f=");
        break;
      case 'B':
      case 'b':
        Serial.print("B or b=");
        break;
      case 'R':
      case 'r':
        Serial.print("R or r=");
        break;
      case 'L':
      case 'l':
        Serial.print("L or l=");
        break;
      case 'S':
      case 's':
        Serial.print("S or s=");
        break;            
    }
    Serial.println(cin);    
  }
}
