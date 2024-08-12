#include <stdio.h>
#include <bits/stdc++.h>
#include <iostream>
#include <string>
using namespace std;

// Function to find the One's complement
// of the given binary string
string Ones_complement(string data)
{
	for (int i = 0; i < data.length(); i++) {
		if (data[i] == '0')
			data[i] = '1';
		else
			data[i] = '0';
	}

	return data;
}

// Function to return the checksum value of
// the given string when divided in K size blocks
string checkSum(string data, int block_size)
{
	// Check data size is divisible by block_size
	// Otherwise add '0' front of the data
	int n = data.length();
	if (n % block_size != 0) {
		int pad_size = block_size - (n % block_size);
		for (int i = 0; i < pad_size; i++) {
			data = '0' + data;
		}
	}

	// Binary addition of all blocks with carry
	string result = "";

	// First block of data stored in result variable
	for (int i = 0; i < block_size; i++) {
		result += data[i];
	}

	// Loop to calculate the block
	// wise addition of data
	for (int i = block_size; i < n; i += block_size) {

		// Stores the data of the next block
		string next_block = "";

		for (int j = i; j < i + block_size; j++) {
			next_block += data[j];
		}

		// Stores the binary addition of two blocks
		string additions = "";
		int sum = 0, carry = 0;

		// Loop to calculate the binary addition of
		// the current two blocks of k size
		for (int k = block_size - 1; k >= 0; k--) {
			sum += (next_block[k] - '0')
			       + (result[k] - '0');
			carry = sum / 2;
			if (sum == 0) {
				additions = '0' + additions;
				sum = carry;
			}
			else if (sum == 1) {
				additions = '1' + additions;
				sum = carry;
			}
			else if (sum == 2) {
				additions = '0' + additions;
				sum = carry;
			}
			else {
				additions = '1' + additions;
				sum = carry;
			}
		}

		// After binary add of two blocks with carry,
		// if carry is 1 then apply binary addition
		string final = "";

		if (carry == 1) {
			for (int l = additions.length() - 1; l >= 0;
			        l--) {
				if (carry == 0) {
					final = additions[l] + final;
				}
				else if (((additions[l] - '0') + carry) % 2
				         == 0) {
					final = "0" + final;
					carry = 1;
				}
				else {
					final = "1" + final;
					carry = 0;
				}
			}

			result = final;
		}
		else {
			result = additions;
		}
	}

	// Return One's complements of result value
	// which represents the required checksum value
	return Ones_complement(result);
}

// Function to check if the received message
// is same as the senders message
bool checker(string sent_message,
             string rec_message,
             int block_size)
{

	// Checksum Value of the senders message
	string sender_checksum
	    = checkSum(sent_message, block_size);

	// Checksum value for the receivers message
	string receiver_checksum 
	    = checkSum(rec_message + sender_checksum, block_size);

	// If receivers checksum value is 0
	if (count(receiver_checksum.begin(),
	          receiver_checksum.end(), '0')
	        == block_size) {
		return true;
	}
	else {
		return false;
	}
}


string hexstring_to_binarystring(string hexstring) {
    string binaryString = "";
    for(int index = 0; index < hexstring.length(); index ++) {
        switch(hexstring[index]) {
            case '0':
                binaryString += "0000";
                break;
            case '1':
                binaryString += "0001";
                break;
            case '2':
                binaryString += "0010";
                break;
            case '3':
                binaryString += "0011";
                break;
            case '4':
                binaryString += "0100";
                break;
            case '5':
                binaryString += "0101";
                break;
            case '6':
                binaryString += "0110";
                break;
            case '7':
                binaryString += "0111";
                break;
            case '8':
                binaryString += "1000";
                break;
            case '9':
                binaryString += "1001";
                break;
            case 'a':
                binaryString += "1010";
                break;
            case 'b':
                binaryString += "1011";
                break;
            case 'c':
                binaryString += "1100";
                break;
            case 'd':
                binaryString += "1101";
                break;
            case 'e':
                binaryString += "1110";
                break;
            case 'f':
                binaryString += "1111";
                break;
        }
    }
    return binaryString;
}

///////////////////////////////////////////////////
//TESTS

//the source file
void TEST_checksum_demo() {
	string sent_message
	    = "10000101011000111001010011101101";
	string recv_message
	    = "10000101011000111001010011101101";
	int block_size = 8;

	if (checker(sent_message,
	            recv_message,
	            block_size)) {
		cout << "No Error";
	}
	else {
		cout << "Error";
	}
}

void checksum_sandbox() {
	string sent_message
	    = "10100101101001011010010110100101";
	int block_size = 32;
	string sent_message_checksum = checkSum(sent_message, block_size);
	cout << "sent_message: " << sent_message << "\n";
	cout << "block_size: " << block_size << "\n";
    cout << "sent_message_checksum: " << sent_message_checksum << "\n";
    
    string receiver_checksum = checkSum(sent_message + sent_message_checksum, block_size);
    cout << "receiver_checksum: " << receiver_checksum << "\n";
}

void test_hexstring_conversion() {
    string hex = "f0f06e6e";
    string binaryString = hexstring_to_binarystring(hex);
    cout << "hex: " << hex << "\n";
    cout << "converted to: " << hexstring_to_binarystring(hex) << "\n";
}

void test_hex_pixel_checksum() {
    int block_size = 8;
    string sent_pixelData = "01ff66ff";
    string sent_checksum = "98";
    string sent_pixelData_binary = hexstring_to_binarystring(sent_pixelData);
    string sent_checksum_binary = hexstring_to_binarystring(sent_checksum);

    string local_pixelData = "01ff66ff";
    string local_pixelData_binary = hexstring_to_binarystring(local_pixelData);
    string local_checksum = checkSum(local_pixelData_binary, block_size);
    
    string output = checkSum(local_pixelData_binary + sent_checksum_binary, block_size);
    
    cout << "sent_pixelData(hex):\t\t" << sent_pixelData << "\n";
    cout << "sent_pixelData(binary):\t\t" << sent_pixelData_binary << "\n";
    cout << "sent_checksum:\t\t" << sent_checksum_binary << "\n";
    
    cout << "local_pixelData(hex):\t\t" << local_pixelData << "\n";
    cout << "local_pixelData(binary):\t" << local_pixelData_binary << "\n";
    cout << "local_checksum:\t\t" << local_checksum << "\n";
    
    cout << "output:\t\t\t" << output << "\n";
}

///////////////////////////////////////////////////
int main()
{
	test_hex_pixel_checksum();
	return 0;
}
