#include<string.h>
#include "Matrix.h"
#define MATRIX_SIZE 16

void custom_main() {
  Matrix PixelMatrix;
  PixelMatrix.print();

  for (uint8_t row = 0; row < MATRIX_SIZE; row++) {
    for (uint8_t column = 0; column < MATRIX_SIZE; column++) {
      PixelMatrix.rows[row].pixel[column].setRed(255);
      PixelMatrix.rows[row].pixel[column].setGreen(255);
      PixelMatrix.rows[row].pixel[column].setBlue(255);
    }
  }
  PixelMatrix.print();
}

void setup() {
    Serial.begin(9600);
    custom_main();
}

void loop() {
}

