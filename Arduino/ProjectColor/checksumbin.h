#define PIXEL_BINARY_CHAR_SIZE 36  //1_PIXEL * 4_bytes(position,R,G,B) * 8bits(per Byte) + 4bits(1Byte for checksum)
#define ROW_HEX_CHAR_SIZE 136 //16_PIXEL * 4_bytes(position,R,G,B) * 2chars(per Byte) + 2chars(1Byte for checksum)
#define ROW_BINARY_CHAR_SIZE 520 //16_PIXEL * 4_bytes(position,R,G,B) * 8chars(per Byte) + 8chars(1Byte for checksum)

#define HALF_ROW_HEX_CHAR_SIZE 66 //8_PIXEL * 4_bytes(position,R,G,B) * 2chars(per Byte) + 2chars(1Byte for checksum)
#define HALF_ROW_BINARY_CHAR_SIZE 264 //8_PIXEL * 4_bytes(position,R,G,B) * 8chars(per Byte) + 8chars(1Byte for checksum)

#define QUARTER_ROW_HEX_CHAR_SIZE 34 //4_PIXEL * 4_bytes(position,R,G,B) * 2chars(per Byte) + 2chars(1Byte for checksum)
#define QUARTER_ROW_BINARY_CHAR_SIZE 136 //4_PIXEL * 4_bytes(position,R,G,B) * 8chars(per Byte) + 8chars(1Byte for checksum)


/**
 * Ones_complement is a function that computes the one's complement of a given binary string. The function inverts each bit in the string, changing '0' to '1' and '1' to '0'.
 *
 * **Parameters:**
 *
 * - `data`: A `char*` representing the binary string for which the one's complement will be calculated. The string is modified in place.
 *
 * **Functionality:**
 *
 * - The function iterates over each character in the `data` string.
 * - For each character, it checks if the character is '0' or '1' and then inverts it.
 * - The modified string is the one's complement of the original input, and the inversion is done in place.
 *
 * - This function is typically used in checksum calculations to ensure data integrity by helping verify the transmitted data.
 */
void Ones_complement(char* data) {
    for (int i = 0; i < strlen(data); i++) {
        if (data[i] == '0')
            data[i] = '1';
        else
            data[i] = '0';
    }
}

/**
 * checkSum is a function that calculates the checksum of a binary string by dividing it into blocks of a specified size, performing binary addition on each block, and then applying one's complement.
 * The checksum helps ensure data integrity during transmission.
 *
 * **Parameters:**
 *
 * - `data`: A `char*` representing the binary string for which the checksum will be calculated. The string may be padded to fit the block size.
 * - `block_size`: An `int` specifying the size of each block (i.e., the number of bits) used in the checksum calculation.
 * - `result`: A `char*` where the calculated checksum will be stored. The checksum is the one's complement of the binary addition result.
 *
 * **Functionality:**
 *
 * - The function first checks if the length of `data` is divisible by `block_size`. If not, the string is padded with leading zeros to make it so.
 * - It then initializes the `result` with the first block of data.
 * - The function performs binary addition of the blocks, updating the `result` with each addition.
 * - If a carry is generated during the addition, it is handled and propagated accordingly.
 * - Finally, the function applies one's complement to the `result`, ensuring that it represents the checksum of the input `data`.
 */
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

/**
 * result_checker is a function that verifies whether a given checksum result is valid by checking if all bits in the result are zero.
 *
 * **Parameters:**
 *
 * - `result`: A `char*` representing the binary checksum result that needs to be verified.
 * - `block_size`: An `int` specifying the size of the block (i.e., the number of bits) to be checked.
 *
 * **Returns:**
 *
 * - `bool`: Returns `true` if all bits in the `result` are '0', indicating a valid checksum. Otherwise, returns `false`.
 *
 * **Functionality:**
 *
 * - The function iterates over each bit in the `result` string.
 * - If any bit is found to be non-zero, the function returns `false`.
 * - If all bits are '0', the function returns `true`, indicating that the checksum is valid and the data integrity is intact.
 */
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

/**
 * hexData_to_binaryData is a function that converts a hexadecimal string into its equivalent binary string representation. Each hex digit is translated into a 4-bit binary sequence.
 *
 * **Parameters:**
 *
 * - `hexData`: A `const char*` representing the hexadecimal data to be converted to binary.
 * - `binaryData`: A `char*` where the resulting binary string will be stored. The binary string is constructed by appending 4-bit binary equivalents of each hex digit.
 *
 * **Functionality:**
 *
 * - The function initializes `binaryData` as an empty string.
 * - It then iterates over each character in `hexData`, appending the corresponding 4-bit binary string to `binaryData`.
 * - The function handles both uppercase and lowercase hexadecimal digits.
 * - If an invalid hexadecimal character is encountered, the function appends "????" to `binaryData` to indicate an error.
 *
 * - This function is typically used when binary data is needed for further processing or transmission, but the data is initially provided in hexadecimal format.
 */
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