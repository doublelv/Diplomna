#include <FastLED.h>
#define NUM_LEDS 256
#define DATA_PIN 12
#define MATRIX_SIZE 16

class Checksum {
public:

  Checksum() {
    value_red   = 0;
    value_green = 0;
    value_blue  = 0;
  }

  int get_value_red() {
    return value_red;
  }
  int get_value_green() {
    return value_green;
  }
  int get_value_blue() {
    return value_blue;
  }

  void set_value_red(int new_value) {
    value_red = new_value;
  }
  void set_value_green(int new_value) {
    value_green = new_value;
  }
  void set_value_blue(int new_value) {
    value_blue = new_value;
  }
  void set_value(int new_red, int new_green, int new_blue) {
    set_value_red(new_red);
    set_value_green(new_green);
    set_value_blue(new_blue);
  }

  private: 
  int value_red;
  int value_green;
  int value_blue;
};

class CombinedChecksums {
public:
  CombinedChecksums() {
    for (int index = 0; index < MATRIX_SIZE; index++) {
      rows[index] = Checksum();
      columns[index] = Checksum();
    }
  }

  Checksum rows[MATRIX_SIZE];
  Checksum columns[MATRIX_SIZE];
};

CRGB leds[NUM_LEDS]; 

void setup() {   
 FastLED.setBrightness(20);
 FastLED.addLeds<WS2812B, DATA_PIN, GRB > (leds, NUM_LEDS);
}

void loop() {
  show_image();
  delay(1000);
  sed_led_color(CRGB::Black);
  delay(1000);
}

void sed_led_color(CRGB color) {
  for (int counter = 0; counter < NUM_LEDS; counter++) {
    leds[counter] = color;
    FastLED.show();
    // delay(1);
  }
} 

//old
void show_image() {
  CRGB image[16][16] {
{0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x008000, 0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x008000, 0x008000, 0x008000, 0x008000, 0x008000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0xffffff, 0x000000, 0xffffff, 0x000000, 0x000000, 0x008000, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0x003300, 0x008000, 0x000000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0x003300, 0x008000, 0x008000, 0x008000, 0x008000, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0x003300, 0x003300, 0x003300, 0x003300, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
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
  int row = 0;
  int column = 0;
  int led_index = 0;

  for (column = 0; column < MATRIX_SIZE; column++) {
    for (row = 0; row < MATRIX_SIZE; row++) {
      if(column % 2 == 0 || column == 0) {
        leds[led_index] = image[row][column];
      }
      else
        leds[led_index] = image[MATRIX_SIZE-1-row][column];

      FastLED.show();
      led_index++;
      // delay(1);

    }
  }
}

CRGB receive_image() {
  //TODO: code for receiving image via bluetooth
  CRGB hardcoded_received_image[16][16] {
{0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x008000, 0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x008000, 0x008000, 0x008000, 0x008000, 0x008000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0xffffff, 0x000000, 0xffffff, 0x000000, 0x000000, 0x008000, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0x003300, 0x008000, 0x000000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0x003300, 0x008000, 0x008000, 0x008000, 0x008000, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
{0x000000, 0x003300, 0x003300, 0x003300, 0x003300, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000},
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
  return hardcoded_received_image;
} 

CombinedChecksums calculate_checksums(CRGB image[MATRIX_SIZE][MATRIX_SIZE]) {
  CombinedChecksums checksums;
  
  int checksums_red   = 0; 
  int checksums_green = 0; 
  int checksums_blue  = 0; 
  int row = 0;
  int column = 0;

   for (column = 0; column < MATRIX_SIZE; column++) {
    checksums_red   = 0; 
    checksums_green = 0; 
    checksums_blue  = 0; 
    for (row = 0; row < MATRIX_SIZE; row++) {
      checksums_red   += image[row][column].red;
      checksums_green += image[row][column].green;
      checksums_blue  += image[row][column].blue;
    }
    checksums.rows[column].set_value(checksums_red, checksums_green, checksums_blue);
  }

    for (row = 0; row < MATRIX_SIZE; row++) {
    checksums_red   = 0; 
    checksums_green = 0; 
    checksums_blue  = 0; 
     for (column = 0; column < MATRIX_SIZE; column++) {
      checksums_red   += image[row][column].red;
      checksums_green += image[row][column].green;
      checksums_blue  += image[row][column].blue;
    }
    checksums.columns[row].set_value(checksums_red, checksums_green, checksums_blue);
  }
  return checksums;
}

bool compare_checksums(CombinedChecksums checksums_local, CombinedChecksums checksums_remote) {
  for (int index = 0; index < MATRIX_SIZE; index++) {
    if( (checksums_local.rows[index].get_value_red()      != checksums_remote.rows[index].get_value_red())      or 
        (checksums_local.rows[index].get_value_green()    != checksums_remote.rows[index].get_value_green())    or 
        (checksums_local.rows[index].get_value_blue()     != checksums_remote.rows[index].get_value_blue())     or 
        (checksums_local.columns[index].get_value_red()   != checksums_remote.columns[index].get_value_red())   or 
        (checksums_local.columns[index].get_value_green() != checksums_remote.columns[index].get_value_green()) or 
        (checksums_local.columns[index].get_value_blue()  != checksums_remote.columns[index].get_value_blue())  ) 
    {
      return false;
    }
  return true;
  }
}

void display_image(CRGB image[MATRIX_SIZE][MATRIX_SIZE]) {
  int row = 0;
  int column = 0;
  int led_index = 0;

  for (column = 0; column < MATRIX_SIZE; column++) {
    for (row = 0; row < MATRIX_SIZE; row++) {
      if(column % 2 == 0 || column == 0) {
        leds[led_index] = image[row][column];
      }
      else
        leds[led_index] = image[MATRIX_SIZE-1-row][column];

      FastLED.show();
      led_index++;
      // delay(1);
    }
  }
}