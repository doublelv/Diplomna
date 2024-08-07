#include <SoftwareSerial.h>

SoftwareSerial BTSerial(8, 9);

void setup() 
{
  pinMode(9, OUTPUT);
  piMode(10, INPUT);
  Serial.begin(9600);
  Serial.println("Enter AT Commands");
  BTSerial.begin(9600);
}

void loop() 
{
  if (BTSerial.available()) {
    Serial.write(BTSerial.read());
  }
}