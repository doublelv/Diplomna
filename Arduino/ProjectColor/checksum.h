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

// Function to return the checksum value of
// the given string when divided in K size blocks
void checkSum(char* data, uint8_t block_size, char* result) {
    int n = strlen(data);

    // Pad with leading zeros if necessary
    uint8_t pad_size = block_size - (n % block_size);
    if (pad_size != block_size) {
        memmove(data + pad_size, data, n + 1);
        memset(data, '0', pad_size);
        n += pad_size;
    }

    // Initialize the result with the first block
    strncpy(result, data, block_size);

    // Process each block
    for (uint8_t i = block_size; i < n; i += block_size) {
        binary_add(result, &data[i], block_size);
    }

    // Compute one's complement
    Ones_complement(result, block_size);
}

// char* arguments variant
void hexstring_to_binaryCharArray(const char* hexstring, char* binaryString) {
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

// string arguments variant
void hexstring_to_binarystring(String hexstring, String &binaryString) {
    int length = hexstring.length();
    for(int index = 0; index < length; index++) {
        switch(hexstring[index]) {
            case '0': binaryString += "0000"; break;
            case '1': binaryString += "0001"; break;
            case '2': binaryString += "0010"; break;
            case '3': binaryString += "0011"; break;
            case '4': binaryString += "0100"; break;
            case '5': binaryString += "0101"; break;
            case '6': binaryString += "0110"; break;
            case '7': binaryString += "0111"; break;
            case '8': binaryString += "1000"; break;
            case '9': binaryString += "1001"; break;
            case 'a': case 'A': binaryString += "1010"; break;
            case 'b': case 'B': binaryString += "1011"; break;
            case 'c': case 'C': binaryString += "1100"; break;
            case 'd': case 'D': binaryString += "1101"; break;
            case 'e': case 'E': binaryString += "1110"; break;
            case 'f': case 'F': binaryString += "1111"; break;
        }
    }
}
