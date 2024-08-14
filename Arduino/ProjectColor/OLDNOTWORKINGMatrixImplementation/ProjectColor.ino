#include <SoftwareSerial.h>
#include <FastLED.h>
#include "checksum.h"

#define MATRIX_SIZE 16
#define LEDS_DATA_PIN 12
#define NUM_LEDS 256
#define ROW_CHAR_SIZE 520 //16_rows * 4_bytes(pos,R,G,B) * 8bits(per Byte) + 8bits(1Byte for checksum) 
#define BLOCK_SIZE 8


SoftwareSerial bluetoothManager(9, 10); // RX | TX
CRGB leds[NUM_LEDS];

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

CRGB receivedRow[16] = {0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000};

void loop() {
	CRGB receivedImage[16][16] {
	{0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x008000, 0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x008000, 0x008000, 0x008000, 0x008000, 0x008000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x008000, 0x008000, 0x008000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
	{0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000}
	};

	while (bluetoothManager.available()) {
        char c = bluetoothManager.read();
        if (c == '\n' || c == '\r') {
            if (incomingMessage.length() > 0) {
                processMessage(incomingMessage, receivedImage);
                incomingMessage = "";  // Clear buffer for next message
            }
        } else {
            incomingMessage += c;
        }
    }
}

void processMessage(const String& message, CRGB receivedImage[MATRIX_SIZE][MATRIX_SIZE]) {
    Serial.print("Received message: ");
    Serial.println(message);

    if (message == SYN) {
        Serial.println("Sending SYN-ACK");
        bluetoothManager.println(SYN_ACK);  // Send SYN-ACK with newline for better recognition
    } else if (message == SYN_ACK) {
        Serial.println("Sending ACK");
        bluetoothManager.println(ACK);  // Send ACK with newline for better recognition
    } else if (message.startsWith("data:")) {
        Serial.println("Received data message");
		if(checkCheckSum(message.substring(5).c_str(), receivedImage)) { // message.substring(5) returns message[5:]
			bluetoothManager.println(ROW_SUCCESS);  // Send data acknowledgment
		}
		else {
			bluetoothManager.println(ROW_FAIL);  // Send data acknowledgment
		}
    } else if (message == FIN) {
        Serial.println("Sending FIN-ACK");
        bluetoothManager.println(FIN_ACK);  // Send FIN-ACK with newline
        displayImage(receivedImage);
    } else {
        Serial.print("Unknown message: ");
        Serial.println(message);
    }
}

void processRow(const char* message, CRGB receivedImage[MATRIX_SIZE][MATRIX_SIZE]) {
	int index = 0;
    for (uint8_t i = 0; i < MATRIX_SIZE; i++) {
        // Extract 32 bits (4 bytes) for each pixel
        char posByte[9];
        char rByte[9];
        char gByte[9];
        char bByte[9];

        strncpy(posByte, message + index, 8);
        posByte[8] = '\0'; // Null-terminate the string

        strncpy(rByte, message + index + 8, 8);
        rByte[8] = '\0'; // Null-terminate the string

        strncpy(gByte, message + index + 16, 8);
        gByte[8] = '\0'; // Null-terminate the string

        strncpy(bByte, message + index + 24, 8);
        bByte[8] = '\0'; // Null-terminate the string

        // Convert binary strings to integers
        uint8_t pos = binaryToDecimal(posByte);
        uint8_t row = (pos & 0xF0) >> 4;  // Upper 4 bits for row
        uint8_t col = pos & 0x0F;         // Lower 4 bits for column
        uint8_t r = binaryToDecimal(rByte);
        uint8_t g = binaryToDecimal(gByte);
        uint8_t b = binaryToDecimal(bByte);

        // Update the receivedImage array with the new pixel data
        if (row < MATRIX_SIZE && col < MATRIX_SIZE) {
            receivedImage[row][col] = CRGB(r, g, b);
        }

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

bool checkCheckSum(char* message, CRGB receivedImage[MATRIX_SIZE][MATRIX_SIZE]) {
	char binaryString[49];
	hexstring_to_binaryCharArray(message, binaryString);
	const char result[9];
	checkSum(binaryString, BLOCK_SIZE, result);

	if(checker(result, BLOCK_SIZE)) {
		processRow(binaryString, receivedImage);
		return true;
	} else {
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

	if (count_zeros == block_size) {
		return true;
	} else {
		return false;
	}
}

void setLedsColor(CRGB color) {
  for ( uint8_t counter = 0; counter < NUM_LEDS; counter++) {
    leds[counter] = color;
    FastLED.show();
    // delay(1);
  }
} 

void displayImage(CRGB receivedImage[MATRIX_SIZE][MATRIX_SIZE]) {
	uint8_t led_index = 0;

	for (uint8_t column = 0; column < MATRIX_SIZE; column++) {
		for (uint8_t row = 0; row < MATRIX_SIZE; row++) {
			if(column % 2 == 0 || column == 0) {
				leds[led_index] = receivedImage[row][column];
			}
			else
				leds[led_index] = receivedImage[MATRIX_SIZE-1-row][column];

			led_index++;
		// delay(1);
		}
	}			
  FastLED.show();
}
