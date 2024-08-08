#include <SoftwareSerial.h>

#define END_OF_ARRAY '\n' // Define the delimiter

SoftwareSerial bluetooth(9, 8);

void setup() {
    Serial.begin(9600);
    bluetooth.begin(9600);

}

void loop() {
	
	static String byteString = "";
	if (bluetooth.available()) {
			char incomingChar = bluetooth.read();
			if (incomingChar == END_OF_ARRAY) {
					// Process the complete byte array here
					Serial.println(byteString);
					byteString = ""; // Clear the string for the next message
			} else {
					// Collect incoming bytes into a string
					byteString += incomingChar;
			}
	}
}
