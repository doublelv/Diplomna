#include <SoftwareSerial.h>
#include <FastLED.h>
#include "checksumbin.h"

#define MATRIX_SIZE 16
#define LEDS_DATA_PIN 12
#define NUM_LEDS 256
#define BLOCK_SIZE 4

#define SYN "syn"
#define SYN_ACK "syn-ack"
#define ACK "ack"
#define PIXEL_SUCCESS "PIXEL-SUCCESS"
#define PIXEL_FAIL "PIXEL-FAIL"
// #define ROW_SUCCESS "row-success"
// #define ROW_FAIL "row-fail"
#define FIN "fin"
#define FIN_ACK "fin-ack"
#define LEDS_BLACK "set-leds-black"
#define LEDS_WHITE "set-leds-white"
#define LEDS_RED "set-leds-red"
#define LEDS_GREEN "set-leds-green"
#define LEDS_BLUE "set-leds-blue"

SoftwareSerial bluetoothManager(9, 10); // RX | TX
CRGB leds[NUM_LEDS];

char incomingMessage[50];
int messageIndex = 0;


void setup() {
  incomingMessage[0] = '\0';
	FastLED.setBrightness(10);
	FastLED.addLeds<WS2812B, LEDS_DATA_PIN, GRB > (leds, NUM_LEDS);
	Serial.begin(9600);
	bluetoothManager.begin(9600);
}

void loop() {

	while (bluetoothManager.available()) {
        char c = bluetoothManager.read();
        if (c == '\n' || c == '\r') {
            if (messageIndex > 0) {
                incomingMessage[messageIndex] = '\0';
                processMessage(incomingMessage);
                memset(incomingMessage, 0, sizeof(incomingMessage));  // Clear the buffer
                messageIndex = 0;  // Reset the index
            }
        } else {
          if (messageIndex < sizeof(incomingMessage) - 1) { // Ensure we don't overflow the buffer
                incomingMessage[messageIndex++] = c;
          }
        }
  }
}

void processMessage(char* message) {
  
  const char* dataPrefix = "data:";
    Serial.println("processMessage(): Received message: ");
    Serial.println(message);

    if (strcmp(message, SYN) == 0) {
        Serial.println("Sending SYN-ACK");
        bluetoothManager.write(SYN_ACK);  // Send SYN-ACK with newline for better recognition
    } else if (strcmp(message, SYN_ACK) == 0 ) {
        Serial.println("Sending ACK");
        bluetoothManager.write(ACK);  // Send ACK with newline for better recognition

    } else if (strncmp(message, dataPrefix, strlen(dataPrefix)) == 0) {

      char* dataPart = message + strlen(dataPrefix);

        Serial.println("dataPart:");
        Serial.println(dataPart);

      bool checksum_result = checkCheckSum(dataPart);
      if(checksum_result) { // message.substring(5) returns message from 5th char onwards
        bluetoothManager.write(PIXEL_SUCCESS);  // Send data acknowledgment
      }
      else {
        bluetoothManager.write(PIXEL_FAIL);  // Send data acknowledgment
      }

    } else if (strcmp(message, FIN) == 0) {
        Serial.println("Sending FIN-ACK");
        bluetoothManager.println(FIN_ACK);  // Send FIN-ACK with newline
        FastLED.show(5);
    }
    else if (strcmp(message, LEDS_BLACK) == 0) {
        Serial.println("set-leds-black");
        setLedsColor(CRGB::Black);
    }
    else if (strcmp(message, LEDS_WHITE) == 0) {
        Serial.println("set-leds-white");
        setLedsColor(CRGB::White);
    } 
    else if (strcmp(message, LEDS_RED) == 0) {
        Serial.println("set-leds-red");
        setLedsColor(CRGB::Red);
    }
    else if (strcmp(message, LEDS_GREEN) == 0) {
        Serial.println("set-leds-green");
        setLedsColor(CRGB::Green);
    }
    else if (strcmp(message, LEDS_BLUE) == 0) {
        Serial.println("set-leds-blue");
        setLedsColor(CRGB::Blue);
    }
    else {
        Serial.print("Unknown message: ");
        Serial.println(message);
    }
}

void processPixel(const char* pixelData) {
  Serial.println("entered processPixel()");

  char rowByte[5];
  char columnByte[5];

  strncpy(rowByte, pixelData, 4);
  strncpy(columnByte, pixelData + 4, 4);

  rowByte[4] = '\0';    // Null-terminate the string
  columnByte[4] = '\0'; // Null-terminate the string

  int rowNumber = binaryToDecimal(rowByte, 4);
  //receivedRow.rowNumber = rowNumber;

  char rByte[9];
  char gByte[9];
  char bByte[9];

  strncpy(rByte, pixelData + 8, 8);
  rByte[8] = '\0'; // Null-terminate the string

  strncpy(gByte, pixelData + 16, 8);
  gByte[8] = '\0'; // Null-terminate the string

  strncpy(bByte, pixelData + 24, 8);
  bByte[8] = '\0'; // Null-terminate the string

  int r = binaryToDecimal(rByte, 8);
  int g = binaryToDecimal(gByte, 8);
  int b = binaryToDecimal(bByte, 8);


  int index; //= rowNumber*MATRIX_SIZE + binaryToDecimal(columnByte, 4);
    if (rowNumber % 2 == 1) {
        // Even rows (0, 2, 4, ...) - Left to Right
        index = rowNumber * MATRIX_SIZE + binaryToDecimal(columnByte, 4);
    } else {
        // Odd rows (1, 3, 5, ...) - Right to Left
        index = rowNumber * MATRIX_SIZE + (MATRIX_SIZE - 1 - binaryToDecimal(columnByte, 4));
    }
    
    // Set the LED color
    leds[index].setRGB(r, g, b);

  // Serial.print("r: ");
  // Serial.println(r);
  // Serial.print("g: ");
  // Serial.println(g);
  // Serial.print("b: ");
  // Serial.println(b);

  // receivedRow.r[binaryToDecimal(columnByte, 4)] = r;
  // receivedRow.g[binaryToDecimal(columnByte, 4)] = g;
  // receivedRow.b[binaryToDecimal(columnByte, 4)] = b;
} 


int binaryToDecimal(const char* binaryString, size_t length) {
    int result = 0;
    for (size_t i = 0; i < length; i++) {
        if (binaryString[i] == '1') {
            result = (result << 1) | 1;
        } else {
            result = (result << 1) | 0;
        }
    }
    return result;
}
bool checkCheckSum(char* message) {
  Serial.println("Entered checkCheckSum()");

  char binaryData[PIXEL_BINARY_CHAR_SIZE];

  hexData_to_binaryData(message, binaryData);

  Serial.println("Binary String:");
  Serial.println(binaryData);

	char result[5];
	checkSum(binaryData, BLOCK_SIZE, result);

  Serial.print("Calculated Checksum: ");
  Serial.println(result);


	if(result_checker(result, BLOCK_SIZE)) {
    Serial.println("Pixel is checked: success");
		processPixel(binaryData);
		return true;
	}

  Serial.println("Pixel is checked: fail");
  return false;
}


void setLedsColor(CRGB color) {
  FastLED.showColor(color, 5);
  delay(10);

} 