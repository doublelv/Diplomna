#define END_OF_ARRAY '/' // Define the delimiter
#define END_OF_ROW '\n'

#include <SoftwareSerial.h>

SoftwareSerial bt(8, 9);

void setup() {
  Serial.begin(9600);
  bt.begin(9600);
}

String rowString = "";

void loop() {
		if (bt.available()) {

    char incomingChar = bt.read();

    if(incomingChar == END_OF_ROW) {
        Serial.println(rowString);
        rowString = "";
    }
    
    else {
      rowString += incomingChar;
    }
	}
}