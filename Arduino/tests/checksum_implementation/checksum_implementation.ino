void Ones_complement(char* data, int length) {
    for(int index = 0; index < length; index++) {
        data[index] = (data[index] == '0') ? '1' : '0';
    }
}

void binary_add(char* result, char* next_block, int block_size) {
    int carry = 0;
    for(int k = block_size - 1; k >= 0; k--) {
        int sum = (next_block[k] - '0') + (result[k] - '0') + carry;
        result[k] = (sum % 2) + '0';
        carry = sum / 2;
    }

    // Handle the carry
    if (carry) {
        for (int l = block_size - 1; l >= 0; l--) {
            int sum = (result[l] - '0') + carry;
            result[l] = (sum % 2) + '0';
            carry = sum / 2;
            if (!carry) break;
        }
    }
}

void checkSum(char* data, int block_size, char* result) {
    int n = strlen(data);

    // Pad with leading zeros if necessary
    int pad_size = block_size - (n % block_size);
    if (pad_size != block_size) {
        memmove(data + pad_size, data, n + 1);
        memset(data, '0', pad_size);
        n += pad_size;
    }

    // Initialize the result with the first block
    strncpy(result, data, block_size);

    // Process each block
    for (int i = block_size; i < n; i += block_size) {
        binary_add(result, &data[i], block_size);
    }

    // Compute one's complement
    Ones_complement(result, block_size);
}

void hexstring_to_binarystring(const char* hexstring, char* binaryString) {
    int length = strlen(hexstring);
    for(int index = 0; index < length; index++) {
        switch(hexstring[index]) {
            case '0': strcat(binaryString, "0000"); break;
            case '1': strcat(binaryString, "0001"); break;
            case '2': strcat(binaryString, "0010"); break;
            case '3': strcat(binaryString, "0011"); break;
            case '4': strcat(binaryString, "0100"); break;
            case '5': strcat(binaryString, "0101"); break;
            case '6': strcat(binaryString, "0110"); break;
            case '7': strcat(binaryString, "0111"); break;
            case '8': strcat(binaryString, "1000"); break;
            case '9': strcat(binaryString, "1001"); break;
            case 'a': case 'A': strcat(binaryString, "1010"); break;
            case 'b': case 'B': strcat(binaryString, "1011"); break;
            case 'c': case 'C': strcat(binaryString, "1100"); break;
            case 'd': case 'D': strcat(binaryString, "1101"); break;
            case 'e': case 'E': strcat(binaryString, "1110"); break;
            case 'f': case 'F': strcat(binaryString, "1111"); break;
        }
    }
}

void setup() {
  Serial.begin(9600);
  
  const int block_size = 8;
  char sent_pixelData[] = "01ff66ff";
  char sent_checksum[] = "98";
  
  char sent_pixelData_binary[65] = "";
  char sent_checksum_binary[17] = "";
  
  hexstring_to_binarystring(sent_pixelData, sent_pixelData_binary);
  hexstring_to_binarystring(sent_checksum, sent_checksum_binary);

  char local_pixelData_binary[65] = "";
  strcpy(local_pixelData_binary, sent_pixelData_binary);
  
  char local_checksum[block_size + 1] = "";
  checkSum(local_pixelData_binary, block_size, local_checksum);
  
  char combined_data[81] = "";
  strcpy(combined_data, local_pixelData_binary);
  strcat(combined_data, sent_checksum_binary);
  
  char output[block_size + 1] = "";
  checkSum(combined_data, block_size, output);
  
  Serial.println("sent_pixelData(hex):\t\t" + String(sent_pixelData) + "\n");
  Serial.println("sent_pixelData(binary):\t\t" + String(sent_pixelData_binary) + "\n");
  Serial.println("sent_checksum:\t\t" + String(sent_checksum_binary) +"\n");
  
  Serial.println("local_pixelData(hex):\t\t" + String(sent_pixelData) + "\n");
  Serial.println("local_pixelData(binary):\t" + String(local_pixelData_binary) + "\n");
  Serial.println("local_checksum:\t\t" + String(local_checksum) + "\n");
  
  Serial.println ("output:\t\t\t" + String(output) +"\n");
}

void loop() {}
