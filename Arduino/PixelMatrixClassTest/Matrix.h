#define MATRIX_SIZE 16
#define END_OF_ARRAY '\n'
#define END_OF_PIXEL ','
#define END_OF_BYTE '.'

class Pixel {
public:
    // Constructor with default values
        // Default constructor
    Pixel() : row(0), column(0), red(0), green(0), blue(0) {}
    Pixel(uint8_t Row, uint8_t Column, uint8_t Red=0 , uint8_t Green=0, uint8_t Blue=0) {
			row = Row;
			column = Column;
			red = Red;
			green = Green;
			blue = Blue;
		}

    uint8_t getRow() {
        return row;
    }
    void setRow(uint8_t newValue) {
        row = newValue;
    }

    uint8_t getColumn() {
        return column;
    }
    void setColumn(uint8_t newValue) {
        column = newValue;
    }

    uint8_t getRed() {
        return red;
    }
    void setRed(uint8_t newValue) {
        red = newValue;
    }

    uint8_t getGreen() {
        return green;
    }
    void setGreen(uint8_t newValue) {
        green = newValue;
    }

    uint8_t getBlue() {
        return blue;
    }
    void setBlue(uint8_t newValue) {
        blue = newValue;
    }

    // char* toString() {
    //   char str[25] = "(";
    //   char buffer[25];
    //   itoa(row, buffer, sizeof(buffer));
    //   strncat(str, buffer, sizeof(buffer));
    //   strncat(str, ", ", 2);

    //   itoa(column, buffer, sizeof(buffer));
    //   strncat(str, buffer, sizeof(buffer));
    //   strncat(str, "): ", 3);

    //   itoa(red, buffer, sizeof(buffer));
    //   strncat(str, buffer, sizeof(buffer));

    //   itoa(green, buffer, sizeof(buffer));
    //   strncat(str, buffer, sizeof(buffer));

    //   itoa(blue, buffer, sizeof(buffer));
    //   strncat(str, buffer, sizeof(buffer));
    
    String toString() {
      return "(" + String(row) + ", " + String(column) + "): " + String(red) + " " + String(green) + " " + String(blue);
    }

private:
    uint8_t row;
    uint8_t column;
    uint8_t red;
    uint8_t green;
    uint8_t blue;
};

class PixelRow {
public:

    // Default constructor
    PixelRow() {
      for (uint8_t index = 0; index < MATRIX_SIZE; index++) {
            pixel[index] = Pixel(0, index, 0, 0, 0);
        }
    }

    // Constructor
    PixelRow(uint8_t Row) {
        for (uint8_t columnindex = 0; columnindex < MATRIX_SIZE; columnindex++) {
            pixel[columnindex] = Pixel(Row, columnindex, 0, 0, 0);
        }
    }

    void print() {
      for (uint8_t columnindex = 0; columnindex < MATRIX_SIZE; columnindex++) {
          Serial.println(pixel[columnindex].toString());
        }
    }

    // void print() const {
    //   char str[25*MATRIX_SIZE];
    //   char buffer[25];
    //     for (uint8_t columnindex = 0; columnindex < MATRIX_SIZE; columnindex++) {
    //       strcpy(buffer, pixel[columnindex].toString());
    //       strncat(str, buffer, sizeof(buffer));
    //     }
    //     snprintf("%s", str, sizeof(str));

    // }

    Pixel pixel[MATRIX_SIZE];
};

class Matrix {
public:
    // Constructor
    Matrix() {
        for (uint8_t index = 0; index < MATRIX_SIZE; index++) {
            rows[index] = PixelRow(index);  // Initialize PixelRow with an integer argument
        }
    }

    void print() {
			Serial.println("---------------------------");
			for (uint8_t index = 0; index < MATRIX_SIZE; index++) {
					rows[index].print();
			}
			Serial.println("---------------------------");

    }
  	PixelRow rows[MATRIX_SIZE];  
};


void parseByteArray(String str, Matrix* PixelMatrix) {
  String buffer = "";
  uint8_t row = 0;
  uint8_t column = 0;
  uint8_t red = 0;
  uint8_t green = 0;
  uint8_t blue = 0;

  for (int index = 0; index <= sizeof(str); index++) {
    buffer += str[index];
      if(str[index] == END_OF_PIXEL) {
        row = buffer.substring(0,2).toInt();
        column = buffer.substring(2,4).toInt();
        red = buffer.substring(4,6).toInt();
        green = buffer.substring(6,8).toInt();
        blue = buffer.substring(8,10).toInt();
        PixelMatrix.rows[row].pixel[column].setRed(red);
        PixelMatrix.rows[row].pixel[column].setGreen(green);
        PixelMatrix.rows[row].pixel[column].setBlue(blue);
        buffer = "";     
      }
    }
}