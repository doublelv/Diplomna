#include <cstring>
#include <cstdint>

// Helper function to convert a hex character to its integer value
int hexCharToInt(char hexChar) {
    if (hexChar >= '0' && hexChar <= '9') {
        return hexChar - '0';
    } else if (hexChar >= 'A' && hexChar <= 'F') {
        return hexChar - 'A' + 10;
    } else if (hexChar >= 'a' && hexChar <= 'f') {
        return hexChar - 'a' + 10;
    }
    return 0; // Should not reach here if input is valid
}

// Helper function to convert an integer value to a hex character
char intToHexChar(int value) {
    if (value >= 0 && value <= 9) {
        return value + '0';
    } else if (value >= 10 && value <= 15) {
        return value - 10 + 'A';
    }
    return '0'; // Should not reach here if value is valid
}

// Function to flip hex digits (one's complement)
void Ones_complement(char* data, int length) {
    for (int index = 0; index < length; index++) {
        int value = 15 - hexCharToInt(data[index]); // Flip hex digit
        data[index] = intToHexChar(value);
    }
}

// Function to add two hex numbers
void hex_add(char* result, char* next_block, int block_size) {
    int carry = 0;
    for (int k = block_size - 1; k >= 0; k--) {
        int sum = hexCharToInt(next_block[k]) + hexCharToInt(result[k]) + carry;
        result[k] = intToHexChar(sum % 16); // Hex digit range is 0-15
        carry = sum / 16; // Hex addition carry
    }

    // Handle carry over
    if (carry) {
        for (int l = block_size - 1; l >= 0; l--) {
            int sum = hexCharToInt(result[l]) + carry;
            result[l] = intToHexChar(sum % 16);
            carry = sum / 16;
            if (!carry) break;
        }
    }
}

// Function to return the checksum value of the given string divided in K size blocks
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
        hex_add(result, &data[i], block_size);
    }

    // Compute one's complement
    Ones_complement(result, block_size);
}

// char* arguments variant
void hexstring_to_binaryCharArray(const char* hexstring, char* binaryString) {
    int length = strlen(hexstring);
    for(int index = 0; index < length; index++) {
        switch(hexstring[index]) {
            case '0':           memcpy(binaryString + index * 4, "0000", 4); break;
            case '1':           memcpy(binaryString + index * 4, "0001", 4); break;
            case '2':           memcpy(binaryString + index * 4, "0010", 4); break;
            case '3':           memcpy(binaryString + index * 4, "0011", 4); break;
            case '4':           memcpy(binaryString + index * 4, "0100", 4); break;
            case '5':           memcpy(binaryString + index * 4, "0101", 4); break;
            case '6':           memcpy(binaryString + index * 4, "0110", 4); break;
            case '7':           memcpy(binaryString + index * 4, "0111", 4); break;
            case '8':           memcpy(binaryString + index * 4, "1000", 4); break;
            case '9':           memcpy(binaryString + index * 4, "1001", 4); break;
            case 'a': case 'A': memcpy(binaryString + index * 4, "1010", 4); break;
            case 'b': case 'B': memcpy(binaryString + index * 4, "1011", 4); break;
            case 'c': case 'C': memcpy(binaryString + index * 4, "1100", 4); break;
            case 'd': case 'D': memcpy(binaryString + index * 4, "1101", 4); break;
            case 'e': case 'E': memcpy(binaryString + index * 4, "1110", 4); break;
            case 'f': case 'F': memcpy(binaryString + index * 4, "1111", 4); break;
            default:            Serial.print("Invalid hex character: "); Serial.println(hexstring[index]); break;

        }
    }
    binaryString[length * 4] = '\0';  // Null-terminate the string
        // Debug: Print the binary string at each step
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
