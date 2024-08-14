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
String incomingMessage = "";

void setup() {
	FastLED.setBrightness(20);
	FastLED.addLeds<WS2812B, LEDS_DATA_PIN, GRB > (leds, NUM_LEDS);
	Serial.begin(9600);
	bluetoothManager.begin(9600);
}

void loop() {

	while (bluetoothManager.available()) {
        char c = bluetoothManager.read();
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

void processMessage(String& message) {
    Serial.println("processMessage(): Received message: ");
    Serial.println(message);

    if (message == SYN) {
        Serial.println("Sending SYN-ACK");
        bluetoothManager.println(SYN_ACK);  // Send SYN-ACK with newline for better recognition
    } else if (message == SYN_ACK) {
        Serial.println("Sending ACK");
        bluetoothManager.println(ACK);  // Send ACK with newline for better recognition
    } else if (message.startsWith("data:")) {

        Serial.println("Received data message");
        bluetoothManager.println(ROW_SUCCESS);  // remove after half-rows are good to send

      if(checkCheckSum(message.substring(5).c_str())) { // message.substring(5) returns message from 5th char onwards
        bluetoothManager.println(ROW_SUCCESS);  // Send data acknowledgment
      }
      else {
        bluetoothManager.println(ROW_FAIL);  // Send data acknowledgment
      }

    } else if (message == FIN) {
        Serial.println("Sending FIN-ACK");
        bluetoothManager.println(FIN_ACK);  // Send FIN-ACK with newline
        //displayRow();
    } else {
        Serial.print("Unknown message: ");
        Serial.println(message);
    }
}

void processRow(const char* message) {
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

  char binaryString[273]; // +1 for the null terminator //(8*4 + 2)*8 = 272 (half_row_size*bytes_per_pixel + bytes_checksum)*bits*per*byte

  hexstring_to_binaryCharArray(message, binaryString);

  // Serial.println("Binary String:");
  // Serial.println(binaryString);

	const char result[9];
	checkSum(binaryString, BLOCK_SIZE, result);

  Serial.print("Calculated Checksum: ");
  Serial.println(result);

	if(checker(result, BLOCK_SIZE)) {
    Serial.println("Row is checker success");
		// processRow(binaryString);
		return true;
	} else {
      Serial.println("Row is checker fail");
		return false;
	} 
}

bool checker(char* message, uint8_t block_size) {
	uint8_t count_zeros = 0;
	for (uint8_t i = 0; i < block_size; i++) {
		if (message[i] == '0') {
			count_zeros++;
		}
	}

  return (count_zeros == block_size);
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
