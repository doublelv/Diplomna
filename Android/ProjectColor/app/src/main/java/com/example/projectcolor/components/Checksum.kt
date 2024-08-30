package com.example.projectcolor.components

/**
 * onesComplement is a function that computes the one's complement of a given binary string. The one's complement is obtained by inverting each bit in the string.
 *
 * **Parameters:**
 *
 * - `data`: A `String` representing the binary data for which the one's complement will be calculated.
 *
 * **Returns:**
 *
 * - `String`: Returns a new binary string where each bit is the inverse of the corresponding bit in the input string. Specifically, '0' becomes '1' and '1' becomes '0'.
 *
 * **Functionality:**
 *
 * - The function iterates over each character in the input string.
 * - For each character, it inverts the bit: '0' is changed to '1', and '1' is changed to '0'.
 * - The inverted bits are stored in a `CharArray`, which is then converted back into a string and returned.
 *
 * - This function is typically used as part of a checksum calculation to help verify data integrity by ensuring that the transmitted data can be checked for errors.
 */
fun onesComplement(data: String): String {
    val result = CharArray(data.length)
    for (index in data.indices) {
        result[index] = if (data[index] == '0') '1' else '0'
    }
    return result.concatToString()
}

/**
 * binaryAdd is a function that performs binary addition of two binary strings, each representing a block of bits, and returns the result.
 * The function also handles any carry-over that results from the addition, ensuring that the final binary string is accurate.
 *
 * **Parameters:**
 *
 * - `result`: A `String` representing the first binary block to be added. This is typically the cumulative result of previous additions.
 * - `nextBlock`: A `String` representing the next binary block to be added to the `result`.
 * - `blockSize`: An `Int` specifying the size of the blocks (i.e., the number of bits) involved in the binary addition.
 *
 * **Returns:**
 *
 * - `String`: Returns the binary string that results from the addition of `result` and `nextBlock`, taking into account any carry-over.
 *
 * **Functionality:**
 *
 * - The function initializes an array of characters (`resultArray`) from the `result` string, which will hold the result of the addition.
 * - Starting from the least significant bit (rightmost), it iterates through each bit in the `result` and `nextBlock`, performs binary addition, and accounts for carry.
 * - If there is any carry remaining after the initial pass, the function continues to propagate the carry through the more significant bits (moving left).
 * - The final result, which includes any carry-over, is returned as a new binary string.
 *
 * - This function is typically used in the context of checksum calculations, where multiple binary blocks are summed to create a final checksum value.
 */
fun binaryAdd(result: String, nextBlock: String, blockSize: Int): String {
    val resultArray = result.toCharArray()
    var carry = 0
    for (k in blockSize - 1 downTo 0) {
        val sum = (nextBlock[k] - '0') + (resultArray[k] - '0') + carry
        resultArray[k] = (sum % 2 + '0'.toInt()).toChar()
        carry = sum / 2
    }

    // Handle the carry
    if (carry != 0) {
        for (l in blockSize - 1 downTo 0) {
            val sum = (resultArray[l] - '0') + carry
            resultArray[l] = (sum % 2 + '0'.toInt()).toChar()
            carry = sum / 2
            if (carry == 0) break
        }
    }
    return resultArray.concatToString()
}

/**
 * checkSum is a function that calculates the checksum of a given binary string by breaking it into blocks, performing binary addition on each block,
 * and then applying a one's complement to the result. This checksum is typically used to verify the integrity of data during transmission.
 *
 * **Parameters:**
 *
 * - `data`: A `String` representing the binary data for which the checksum will be calculated.
 * - `blockSize`: An `Int` representing the size of the blocks in which the binary data will be divided for the checksum calculation.
 *
 * **Returns:**
 *
 * - `String`: Returns the one's complement of the binary addition result as a binary string, which serves as the checksum.
 *
 * **Functionality:**
 *
 * - The function pads the input binary string with leading zeros if its length is not a multiple of `blockSize`.
 * - It initializes the result with the first block of the padded data.
 * - The function then iterates over the remaining blocks, performing binary addition of each block to the cumulative result.
 * - After processing all blocks, the function calculates the one's complement of the result, which is returned as the checksum.
 *
 * - This function is typically used in data integrity checks, where the checksum is appended to data before transmission, allowing the receiver to verify that the data has not been altered.
 */
fun checkSum(data: String, blockSize: Int): String {
    var paddedData = data
    val n = data.length

    // Pad with leading zeros if necessary
    val padSize = blockSize - (n % blockSize)
    if (padSize != blockSize) {
        paddedData = "0".repeat(padSize) + data
    }

    // Initialize the result with the first block
    var result = paddedData.substring(0, blockSize)

    // Process each block
    for (i in blockSize until paddedData.length step blockSize) {
        result = binaryAdd(result, paddedData.substring(i, i + blockSize), blockSize)
    }

    // Compute one's complement
    return onesComplement(result)
}

/**
 * hexStringToBinaryString is a function that converts a hexadecimal string into its equivalent binary string representation.
 * Each hexadecimal digit is translated into a 4-bit binary sequence.
 *
 * **Parameters:**
 *
 * - `hexString`: A `String` representing the hexadecimal data to be converted to binary.
 *
 * **Returns:**
 *
 * - `String`: Returns a binary string equivalent to the input hexadecimal string, where each hex digit is converted into a 4-bit binary sequence.
 *
 * **Functionality:**
 *
 * - The function iterates over each character in the input `hexString`.
 * - For each character, it appends the corresponding 4-bit binary string to the `binaryString` variable.
 * - The conversion accounts for both uppercase and lowercase hexadecimal digits.
 * - If an invalid hexadecimal character is encountered, the function throws an `IllegalArgumentException`.
 *
 * - This function is typically used when binary data needs to be processed in its binary form but is originally provided in a hexadecimal format.
 */
fun hexStringToBinaryString(hexString: String): String {
    var binaryString = ""
    for (char in hexString) {
        binaryString += when (char) {
            '0' -> "0000"
            '1' -> "0001"
            '2' -> "0010"
            '3' -> "0011"
            '4' -> "0100"
            '5' -> "0101"
            '6' -> "0110"
            '7' -> "0111"
            '8' -> "1000"
            '9' -> "1001"
            'a', 'A' -> "1010"
            'b', 'B' -> "1011"
            'c', 'C' -> "1100"
            'd', 'D' -> "1101"
            'e', 'E' -> "1110"
            'f', 'F' -> "1111"
            else -> throw IllegalArgumentException("Invalid hex character")
        }
    }
    return binaryString
}