package com.example.projectcolor.components

fun onesComplement(data: String): String {
    val result = CharArray(data.length)
    for (index in data.indices) {
        result[index] = if (data[index] == '0') '1' else '0'
    }
    return result.concatToString()
}

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

// Function to return the checksum value of
// the given string when divided into K size blocks
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

//fun setup() {
//    val blockSize = 8
//    val sentPixelData = "01ff66ff"
//    val sentChecksum = "98"
//
//    val sentPixelDataBinary = hexStringToBinaryString(sentPixelData)
//    val sentChecksumBinary = hexStringToBinaryString(sentChecksum)
//
//    val localPixelDataBinary = sentPixelDataBinary
//
//    val localChecksum = checkSum(localPixelDataBinary, blockSize)
//
//    val combinedData = localPixelDataBinary + sentChecksumBinary
//
//    val output = checkSum(combinedData, blockSize)
//
//    println("sent_pixelData(hex):\t\t$sentPixelData\n")
//    println("sent_pixelData(binary):\t\t$sentPixelDataBinary\n")
//    println("sent_checksum:\t\t$sentChecksumBinary\n")
//
//    println("local_pixelData(hex):\t\t$sentPixelData\n")
//    println("local_pixelData(binary):\t$localPixelDataBinary\n")
//    println("local_checksum:\t\t$localChecksum\n")
//
//    println("output:\t\t\t$output\n")
//}