#include <SoftwareSerial.h>
#include <FastLED.h>
#include "checksumbin.h"
#include "checksumhex.h"

#define MATRIX_SIZE 16
#define LEDS_DATA_PIN 12
#define NUM_LEDS 256
#define ROW_CHAR_SIZE 520 //16_rows * 4_bytes(pos,R,G,B) * 8bits(per Byte) + 8bits(1Byte for checksum) 
#define BLOCK_SIZE 8


SoftwareSerial bluetoothManager(9, 10); // RX | TX
CRGB leds[NUM_LEDS];

struct RowData {
  uint8_t rowNumber;
  CRGB pixels[16] = {0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000};
};
RowData receivedRow;

const char SYN[] = "syn";
const char SYN_ACK[] = "syn-ack";
const char ACK[] = "ack";
const char ROW_SUCCESS[] = "row-success";
const char ROW_FAIL[] = "row-fail";
const char FIN[] = "fin";
const char FIN_ACK[] = "fin-ack";

char incomingMessage[200];
int messageIndex = 0;


void setup() {
  incomingMessage[0] = '\0';
	FastLED.setBrightness(20);
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
        bluetoothManager.println(ACK);  // Send ACK with newline for better recognition

    } else if (strncmp(message, dataPrefix, strlen(dataPrefix)) == 0) {

      char* dataPart = message + strlen(dataPrefix);

        Serial.println("Received data:");
        Serial.println(dataPart);

      bool checksum_result = false; //checkCheckSum(dataPart);
      if(checksum_result) { // message.substring(5) returns message from 5th char onwards
        bluetoothManager.println(ROW_SUCCESS);  // Send data acknowledgment
      }
      else {
        bluetoothManager.println(ROW_FAIL);  // Send data acknowledgment
      }

    } else if (strcmp(message, FIN) == 0) {
        Serial.println("Sending FIN-ACK");
        bluetoothManager.println(FIN_ACK);  // Send FIN-ACK with newline
        //displayRow();
    } else {
        Serial.print("Unknown message: ");
        Serial.println(message);
    }
}

void processRow(const char* message) {
  Serial.println("entered processRow()");
	int index = 0;
  
  // Extract 32 bits (4 bytes) for each pixel
  char rowByte[9];
  strncpy(rowByte, message, 8);
  rowByte[8] = '\0'; // Null-terminate the string
  receivedRow.rowNumber = binaryToDecimal(rowByte);

  for(uint8_t i = 0; i < MATRIX_SIZE; i++) {
    char rByte[9];
    char gByte[9];
    char bByte[9];

    strncpy(rByte, message + index + 8, 8);
    rByte[8] = '\0'; // Null-terminate the string

    strncpy(gByte, message + index + 16, 8);
    gByte[8] = '\0'; // Null-terminate the string

    strncpy(bByte, message + index + 24, 8);
    bByte[8] = '\0'; // Null-terminate the string

    uint8_t r = binaryToDecimal(rByte);
    uint8_t g = binaryToDecimal(gByte);
    uint8_t b = binaryToDecimal(bByte);

    receivedRow.pixels[i] = CRGB(r, g, b);

    // Move to the next set of 32 bits
    index += 32;
  }
}

uint8_t binaryToDecimal(const char* binaryString) {
    uint8_t result = 0;
    for (int i = 0; i < 8; i++) {
        result = (result << 1) | (binaryString[i] - '0');
    }
    return result;
}

bool checkCheckSum(char* message) {
  Serial.println("Entered checkCheckSum()");

  char binaryString[MAX_BINARY_STRING_LENGTH];

  hexData_to_binaryData(message, binaryString);

  Serial.println("Binary String:");
  Serial.println(binaryString);

	char result[9];
	checkSum(binaryString, BLOCK_SIZE, result);

  Serial.print("Calculated Checksum: ");
  Serial.println(result);


	if(result_checker(result, BLOCK_SIZE)) {
    Serial.println("Row is checked: success");
		// processRow(binaryString);
		return true;
	}

  Serial.println("Row is checked: fail");
  return false;
}


void setLedsColor(CRGB color) {
  for ( uint8_t counter = 0; counter < NUM_LEDS; counter++) {
    leds[counter] = color;
    FastLED.show();
    // delay(1);
  }
} 

void displayRow() {
  uint8_t led_index = receivedRow.rowNumber * MATRIX_SIZE;
  for (uint8_t col = 0; col < MATRIX_SIZE; col++) {
      leds[led_index + col] = receivedRow.pixels[col];
  }

  FastLED.show();
}
