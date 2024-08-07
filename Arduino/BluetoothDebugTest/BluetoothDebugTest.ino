#include <SoftwareSerial.h>

SoftwareSerial bluetooth(9,8);

void setup() 
{
  Serial.begin(9600);
  bluetooth.begin(9600);
}

void loop() 
{
  if (bluetooth.available()) {
    char incomingChar = bluetooth.read();
    Serial.write(incomingChar);
  }
  
  // Read data from Serial Monitor and send it to Bluetooth
  if (Serial.available()) {
    char outgoingChar = Serial.read();
    bluetooth.write(outgoingChar);
  }
}