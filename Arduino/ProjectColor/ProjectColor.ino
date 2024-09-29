#include <SoftwareSerial.h>
#include <AltSoftSerial.h>
#include <FastLED.h>
#include "checksumbin.h"

#define MATRIX_SIZE 16
#define LEDS_DATA_PIN 11
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


/**
 * setup is a function that initializes the serial communication, Bluetooth module, and the LED strip. It configures the necessary settings
 * and prepares the system for operation.
 *
 * **Functionality:**
 *
 * - Initializes the serial communication at a baud rate of 9600 for debugging and monitoring.
 * - Initializes the Bluetooth communication using `SoftwareSerial` on pins 9 (RX) and 10 (TX) at a baud rate of 9600.
 * - Sets up the LED strip using the FastLED library, specifying the LED type, data pin, and color order.
 * - Initializes the `incomingMessage` buffer to an empty string.
 */
void setup() {
  Serial.begin(9600);
  bluetoothManager.begin(9600);
  incomingMessage[0] = '\0';
  FastLED.addLeds<WS2812B, LEDS_DATA_PIN, GRB >(leds, NUM_LEDS);
}

/**
 * loop is the main function that runs continuously, handling incoming messages from the Bluetooth module. It processes the data
 * and triggers appropriate actions based on the received messages.
 *
 * **Functionality:**
 *
 * - Continuously checks if data is available from the Bluetooth module.
 * - Reads each character from the Bluetooth input, building a message until a newline or carriage return is encountered.
 * - Once a complete message is received, it is passed to the `processMessage` function for further processing.
 * - Resets the `incomingMessage` buffer and index after each message is processed to prepare for the next incoming message.
 */
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

/**
 * processMessage is a function that handles and processes the incoming messages received via Bluetooth. It interprets different commands
 * and performs corresponding actions, such as sending acknowledgments or controlling the LEDs.
 *
 * **Parameters:**
 *
 * - `message`: A `char*` representing the message received via Bluetooth, which is processed to trigger various actions.
 *
 * **Functionality:**
 *
 * - Interprets and handles different predefined messages such as `SYN`, `SYN_ACK`, `ACK`, `FIN`, and various LED control commands.
 * - Sends appropriate responses back via Bluetooth, such as `SYN-ACK`, `ACK`, `ROW_SUCCESS`, `ROW_FAIL`, and `FIN_ACK`.
 * - Processes pixel data prefixed with "data:" and verifies it using a checksum. If valid, it updates the LED display.
 * - Controls the LED colors based on specific commands, setting the LEDs to black, white, red, green, or blue.
 * - Outputs unknown messages via Bluetooth for debugging purposes.
 */
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

/**
 * processRow is a function that processes an entire row of pixel data by iterating through each pixel in the row and passing
 * the pixel data to the `processPixel` function.
 *
 * **Parameters:**
 *
 * - `rowData`: A `const char*` representing the binary data for a single row of pixels.
 *
 * **Functionality:**
 *
 * - Iterates through each pixel in the row (assumed to be 16 pixels per row).
 * - For each pixel, the corresponding segment of the `rowData` is passed to the `processPixel` function for processing.
 * - This function is used to update the LED strip with the color data for a complete row.
 */
void processRow(const char* rowData) {
  for(uint8_t pixelIndex = 0; pixelIndex < 16; pixelIndex++) {
    processPixel(rowData + pixelIndex * 16);
  }
}

/**
 * processQuarterRow is a function that processes a quarter of a row of pixel data by iterating through each pixel in the quarter
 * and passing the pixel data to the `processPixel` function.
 *
 * **Parameters:**
 *
 * - `rowData`: A `const char*` representing the binary data for a quarter of a row of pixels.
 *
 * **Functionality:**
 *
 * - Iterates through each pixel in the quarter (assumed to be 4 pixels per quarter row).
 * - For each pixel, the corresponding segment of the `rowData` is passed to the `processPixel` function for processing.
 * - This function is used to update the LED strip with the color data for a partial row.
 */
void processQuarterRow(const char* rowData) {
  for(uint8_t pixelIndex = 0; pixelIndex < 4; pixelIndex++) {
    processPixel(rowData + pixelIndex * 32); // 4 pixels, each with 4 bytes of information = 32 bits of binary storage
  }
}

/**
 * processPixel is a function that processes individual pixel data, extracting the row, column, and RGB color information
 * from the binary data and setting the corresponding LED to the specified color.
 *
 * **Parameters:**
 *
 * - `pixelData`: A `const char*` representing the binary data for a single pixel, including its position and color information.
 *
 * **Functionality:**
 *
 * - Extracts the row and column numbers from the first 8 bits of `pixelData`.
 * - Extracts the red, green, and blue color components from the subsequent 24 bits of `pixelData`.
 * - Calculates the correct index for the LED strip based on the row and column numbers, accounting for the zigzag pattern.
 * - Sets the LED at the calculated index to the specified RGB color.
 */
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

/**
 * binaryToDecimal is a function that converts a binary string to its decimal equivalent. The conversion is limited to the size specified by the length parameter.
 *
 * **Parameters:**
 *
 * - `binaryString`: A `const char*` representing the binary string to be converted to a decimal value.
 * - `length`: A `size_t` specifying the length of the binary string to consider for the conversion.
 *
 * **Returns:**
 *
 * - `uint8_t`: Returns the decimal equivalent of the input binary string.
 *
 * **Functionality:**
 *
 * - Iterates over the binary string from left to right, shifting the result left and adding the corresponding bit value.
 * - The function processes only the specified number of bits (`length`) and converts them to a decimal integer.
 */
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

/**
 * checkCheckSum is a function that verifies the integrity of a message by converting it to binary, calculating its checksum,
 * and comparing the result to determine if the message is valid.
 *
 * **Parameters:**
 *
 * - `message`: A `char*` representing the hexadecimal string message to be checked.
 *
 * **Returns:**
 *
 * - `bool`: Returns `true` if the checksum is valid and the message is correctly formed; otherwise, returns `false`.
 *
 * **Functionality:**
 *
 * - Converts the hexadecimal `message` to a binary string.
 * - Calculates the checksum of the binary data using the `checkSum` function.
 * - Compares the checksum result against the expected value using `result_checker`.
 * - If the checksum is valid, processes the quarter row of data and returns `true`; otherwise, returns `false`.
 */
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

/**
 * setLedsColor is a function that sets the entire LED strip to a specified color and displays the result.
 *
 * **Parameters:**
 *
 * - `color`: A `CRGB` value representing the color to set on all LEDs in the strip.
 *
 * **Functionality:**
 *
 * - Sets all LEDs in the strip to the specified `color`.
 * - Displays the color immediately on the LED strip using `FastLED.show()`.
 * - Adds a brief delay to ensure the color is set correctly.
 */
void setLedsColor(CRGB color) {
  FastLED.showColor(color, 50);
  delay(10);
}