//1_PIXEL * 4_bytes(position,R,G,B) * 8bits(per Byte) + 4bits(1Byte for checksum) + "\0"
#define PIXEL_BINARY_CHAR_SIZE 36  

void Ones_complement(char* data) {
    for (int i = 0; i < strlen(data); i++) {
        if (data[i] == '0')
            data[i] = '1';
        else
            data[i] = '0';
    }
}

// Function to return the checksum value of
// the given string when divided in K size blocks
void checkSum(char* data, int block_size, char* result) {
    // Check data size is divisible by block_size
    int n = strlen(data);
    if (n % block_size != 0) {
        int pad_size = block_size - (n % block_size);
        for (int i = n + pad_size - 1; i >= pad_size; i--) {
            data[i] = data[i - pad_size];
        }
        for (int i = 0; i < pad_size; i++) {
            data[i] = '0';
        }
        n += pad_size;
        data[n] = '\0'; // Add null-terminator at the end
    }

    // Initialize result to the first block of data
    strncpy(result, data, block_size);
    result[block_size] = '\0'; // Null-terminate the result

    // Loop to calculate block-wise addition of data
    for (int i = block_size; i < n; i += block_size) {
        char next_block[block_size + 1];
        strncpy(next_block, &data[i], block_size);
        next_block[block_size] = '\0'; // Null-terminate next_block

        char additions[block_size + 1];
        int sum = 0, carry = 0;

        for (int k = block_size - 1; k >= 0; k--) {
            sum = (next_block[k] - '0') + (result[k] - '0') + carry;
            carry = sum / 2;
            additions[k] = (sum % 2) + '0';
        }
        additions[block_size] = '\0';

        // If carry is 1, handle it
        if (carry == 1) {
            for (int l = block_size - 1; l >= 0; l--) {
                sum = (additions[l] - '0') + carry;
                carry = sum / 2;
                additions[l] = (sum % 2) + '0';
            }
        }

        strcpy(result, additions); // Copy additions to result
    }

    // Apply One's complement to the result
    Ones_complement(result);
}

bool result_checker(char* result, int block_size) {
	for (size_t i = 0; i < block_size; i++)
	{
		if (result[i] != '0')
		{
			return false;
		}
	}
	return true;
}


void hexData_to_binaryData(const char* hexData, char* binaryData) {
    // Initialize the binary string to an empty string
    binaryData[0] = '\0';

    // Convert each hex digit to its binary equivalent
    for (int index = 0; index < strlen(hexData); index++) {
        switch (hexData[index]) {
            case '0': 			strcat(binaryData, "0000"); break;
            case '1': 			strcat(binaryData, "0001"); break;
            case '2': 			strcat(binaryData, "0010"); break;
            case '3': 			strcat(binaryData, "0011"); break;
            case '4': 			strcat(binaryData, "0100"); break;
            case '5': 			strcat(binaryData, "0101"); break;
            case '6': 			strcat(binaryData, "0110"); break;
            case '7': 			strcat(binaryData, "0111"); break;
            case '8': 			strcat(binaryData, "1000"); break;
            case '9': 			strcat(binaryData, "1001"); break;
            case 'a': case 'A': strcat(binaryData, "1010"); break;
            case 'b': case 'B': strcat(binaryData, "1011"); break;
            case 'c': case 'C': strcat(binaryData, "1100"); break;
            case 'd': case 'D': strcat(binaryData, "1101"); break;
            case 'e': case 'E': strcat(binaryData, "1110"); break;
            case 'f': case 'F': strcat(binaryData, "1111"); break;
            default: // Handle invalid hex characters if needed
                strcat(binaryData, "????");
                break;
        }
    }
}