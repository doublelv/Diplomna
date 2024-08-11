#include <SoftwareSerial.h>
SoftwareSerial BTserial(8, 9); // RX | TX 
 
void setup() 
{
    Serial.begin(9600);
    BTserial.begin(9600);  
}
 

bool data_received = ""; //not actual row



void loop()
{
 
    // Keep reading from HC-06 and send to Arduino Serial Monitor
    if (BTserial.available())
    {  
        Serial.write(BTserial.read());
    }
 
    // Keep reading from Arduino Serial Monitor and send to HC-06
    if (Serial.available())
    {
        BTserial.write(Serial.read());
    }
 
}