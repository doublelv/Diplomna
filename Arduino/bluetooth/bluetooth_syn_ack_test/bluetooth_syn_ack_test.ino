#include <SoftwareSerial.h>
SoftwareSerial BTserial(9, 10); // RX | TX

// Define protocol messages
const String SYN = "syn";
const String SYN_ACK = "syn-ack";
const String ACK = "ack";
const String DATA_ACK = "data-ack";
const String FIN = "fin";
const String FIN_ACK = "fin-ack";

// Buffer to hold incoming data
String incomingMessage = "";

void setup() {
    Serial.begin(9600);
    BTserial.begin(9600);
    Serial.println("Setup complete.");
}

void loop() {
    // Read from Bluetooth and process messages
    while (BTserial.available()) {
        char c = BTserial.read();
        if (c == '\n' || c == '\r') {
            if (incomingMessage.length() > 0) {
                processMessage(incomingMessage);
                incomingMessage = "";  // Clear buffer for next message
            }
        } else {
            incomingMessage += c;
        }
    }
}

void processMessage(const String& message) {
    Serial.print("Received message: ");
    Serial.println(message);

    if (message == SYN) {
        Serial.println("Sending SYN-ACK");
        BTserial.println(SYN_ACK);  // Send SYN-ACK with newline for better recognition
    } else if (message == SYN_ACK) {
        Serial.println("Sending ACK");
        BTserial.println(ACK);  // Send ACK with newline for better recognition
    } else if (message.startsWith("data:")) {
        Serial.println("Received data message");
        // Process data here if needed
        BTserial.println(DATA_ACK);  // Send data acknowledgment
    } else if (message == FIN) {
        Serial.println("Sending FIN-ACK");
        BTserial.println(FIN_ACK);  // Send FIN-ACK with newline
    } else {
        Serial.print("Unknown message: ");
        Serial.println(message);
    }
}