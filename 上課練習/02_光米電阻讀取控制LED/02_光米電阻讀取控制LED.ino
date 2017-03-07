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

int intValue;
// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin 13 as an output.
  pinMode(13, OUTPUT);
  pinMode(A2, INPUT);
  intValue=10;
  Serial.begin(9600);
}

// the loop function runs over and over again forever
void loop() {
  delay(500);              // wait for a second
  intValue=analogRead(A2);
  Serial.println(intValue);
  if(intValue<100)
  {
    digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
  }
  else
  {
    digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
  }
}
