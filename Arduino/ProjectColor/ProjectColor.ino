#include <SoftwareSerial.h>
#include <AltSoftSerial.h>
#include <FastLED.h>
#include "checksumbin.h"

#define MATRIX_SIZE 16
#define LEDS_DATA_PIN 12
#define NUM_LEDS 256
#define BLOCK_SIZE 8

#define SYN "syn"
#define SYN_ACK "syn-ack"
#define ACK "ack"
#define ROW_SUCCESS "ROW-SUCCESS"
#define ROW_FAIL "ROW-FAIL"
#define FIN "fin"
#define FIN_ACK "fin-ack"
#define LEDS_BLACK "set-leds-black"
#define LEDS_WHITE "set-leds-white"
#define LEDS_RED "set-leds-red"
#define LEDS_GREEN "set-leds-green"
#define LEDS_BLUE "set-leds-blue"

SoftwareSerial bluetoothManager(9, 10);  // RX | TX
CRGB leds[NUM_LEDS];

char incomingMessage[QUARTER_ROW_HEX_CHAR_SIZE + 8];
uint8_t messageIndex = 0;



void setup() {
  Serial.begin(9600);
  bluetoothManager.begin(9600);
  incomingMessage[0] = '\0';
  FastLED.addLeds<WS2812B, LEDS_DATA_PIN, GRB >(leds, NUM_LEDS);
}

void loop() {

  while (bluetoothManager.available()) {
    char c = bluetoothManager.read();
    if (c == '\n' || c == '\r') {
      if (messageIndex > 0) {
        incomingMessage[messageIndex] = '\0';
        processMessage(incomingMessage);
        memset(incomingMessage, 0, sizeof(incomingMessage));  // Clear the buffer
        messageIndex = 0;                                     // Reset the index
      }
    } else {
      if (messageIndex < sizeof(incomingMessage) - 1) {  // Ensure we don't overflow the buffer
        incomingMessage[messageIndex++] = c;
      }
    }
  }
}

void processMessage(char* message) { 
  Serial.println("entered processMessage()");
  Serial.println("message:");
  Serial.println(message);

  const char* dataPrefix = "data:";

  if (strcmp(message, SYN) == 0) {
    bluetoothManager.write(SYN_ACK);  // Send SYN-ACK with newline for better recognition
  }

  else if (strcmp(message, SYN_ACK) == 0) {
    bluetoothManager.write(ACK);  // Send ACK with newline for better recognition
  }

  else if (strncmp(message, dataPrefix, strlen(dataPrefix)) == 0) {

    char* dataPart = message + strlen(dataPrefix);

    bool checksum_result = checkCheckSum(dataPart);
    if (checksum_result) {
      bluetoothManager.write(ROW_SUCCESS);
    } else {
      bluetoothManager.write(ROW_FAIL);
    }
  }

  else if (strcmp(message, FIN) == 0) {
    bluetoothManager.write(FIN_ACK);
    FastLED.show(50);
  }

  else if (strcmp(message, LEDS_BLACK) == 0) {
    setLedsColor(CRGB::Black);
  }

  else if (strcmp(message, LEDS_WHITE) == 0) {
    setLedsColor(CRGB::White);
  }

  else if (strcmp(message, LEDS_RED) == 0) {
    setLedsColor(CRGB::Red);
  }

  else if (strcmp(message, LEDS_GREEN) == 0) {
    setLedsColor(CRGB::Green);
  }

  else if (strcmp(message, LEDS_BLUE) == 0) {
    setLedsColor(CRGB::Blue);
  }

  else {
    bluetoothManager.print("Unknown message: ");
    bluetoothManager.println(message);
  }
}

void processRow(const char* rowData) {
  for(uint8_t pixelIndex = 0; pixelIndex < 16; pixelIndex++) {
    processPixel(rowData + pixelIndex * 16);
  }
}

void processQuarterRow(const char* rowData) {
  for(uint8_t pixelIndex = 0; pixelIndex < 4; pixelIndex++) {
    processPixel(rowData + pixelIndex * 32); // 4 pixels, each with 4 bytes of information = 32 bits of binary storage
  }
}

void processPixel(const char* pixelData) {
  char rowByte[5];
  char columnByte[5];

  strncpy(rowByte, pixelData, 4);
  strncpy(columnByte, pixelData + 4, 4);

  rowByte[4] = '\0';     // Null-terminate the string
  columnByte[4] = '\0';  // Null-terminate the string

  uint8_t rowNumber = binaryToDecimal(rowByte, 4);
  //receivedRow.rowNumber = rowNumber;

  char rByte[9];
  char gByte[9];
  char bByte[9];

  strncpy(rByte, pixelData + 8, 8);
  rByte[8] = '\0';  // Null-terminate the string

  strncpy(gByte, pixelData + 16, 8);
  gByte[8] = '\0';  // Null-terminate the string

  strncpy(bByte, pixelData + 24, 8);
  bByte[8] = '\0';  // Null-terminate the string

  uint8_t r = binaryToDecimal(rByte, 8);
  uint8_t g = binaryToDecimal(gByte, 8);
  uint8_t b = binaryToDecimal(bByte, 8);


  uint8_t index;  //= rowNumber*MATRIX_SIZE + binaryToDecimal(columnByte, 4);
  if (rowNumber % 2 == 1) {
    // Even rows (0, 2, 4, ...) - Left to Right
    index = rowNumber * MATRIX_SIZE + binaryToDecimal(columnByte, 4);
  } else {
    // Odd rows (1, 3, 5, ...) - Right to Left
    index = rowNumber * MATRIX_SIZE + (MATRIX_SIZE - 1 - binaryToDecimal(columnByte, 4));
  }

  // Set the LED color
  leds[index].setRGB(r, g, b);
}


uint8_t binaryToDecimal(const char* binaryString, size_t length) {
  uint8_t result = 0;
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
  char binaryData[QUARTER_ROW_BINARY_CHAR_SIZE + 1];

  hexData_to_binaryData(message, binaryData);

  char result[9];
  checkSum(binaryData, BLOCK_SIZE, result);

  if (result_checker(result, BLOCK_SIZE)) {
    processQuarterRow(binaryData);
    return true;
  }
  return false;
}

void setLedsColor(CRGB color) {
  FastLED.showColor(color, 50);
  delay(10);
}