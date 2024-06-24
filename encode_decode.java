public class VarintEncoderDecoder {

    public static void main(String[] args) {
        int fieldNumber = 1;
        int value = 150;

        // Encode the field number and value
        byte[] encodedData = encodeFieldAndValue(fieldNumber, value);
        System.out.print("Encoded value: ");
        for (byte b : encodedData) {
            System.out.printf("%02X ", b);
        }
        System.out.println();

        // Decode the encoded data
        DecodedResult decodedResult = decodeFieldAndValue(encodedData);
        System.out.println("Field number: " + decodedResult.fieldNumber);
        System.out.println("Decoded value: " + decodedResult.value);
    }

    /**
     * Encodes a field number and value as varint.
     *
     * @param fieldNumber The field number.
     * @param value The integer value to encode.
     * @return The varint-encoded byte array.
     */
    public static byte[] encodeFieldAndValue(int fieldNumber, int value) {
        int wireType = 0;  // Wire type for varint
        int key = (fieldNumber << 3) | wireType;  // Combine field number and wire type

        byte[] keyBytes = encodeVarint(key);  // Encode the key
        byte[] valueBytes = encodeVarint(value);  // Encode the value

        // Combine key bytes and value bytes
        byte[] result = new byte[keyBytes.length + valueBytes.length];
        System.arraycopy(keyBytes, 0, result, 0, keyBytes.length);
        System.arraycopy(valueBytes, 0, result, keyBytes.length, valueBytes.length);

        return result;
    }

    /**
     * Decodes a varint-encoded byte array to a field number and value.
     *
     * @param encoded The varint-encoded byte array.
     * @return A DecodedResult object containing the field number and value.
     */
    public static DecodedResult decodeFieldAndValue(byte[] encoded) {
        int position = 0;

        // Decode the key
        int key = decodeVarint(encoded, position);
        int fieldNumber = key >> 3;  // Extract field number
        int wireType = key & 0x07;  // Extract wire type (not used here)

        // Decode the value
        position += getVarintLength(key);  // Move position past the key
        int value = decodeVarint(encoded, position);

        return new DecodedResult(fieldNumber, value);
    }

    /**
     * Encodes an integer as a varint.
     *
     * @param value The integer value to encode.
     * @return The varint-encoded byte array.
     */
    public static byte[] encodeVarint(int value) {
        byte[] buffer = new byte[5];  // A buffer to hold the encoded bytes (max 5 bytes for a 32-bit integer)
        int position = 0;

        while (true) {
            if ((value & ~0x7F) == 0) {  // If the remaining value fits in 7 bits
                buffer[position++] = (byte) value;  // Write it and break
                break;
            } else {
                buffer[position++] = (byte) ((value & 0x7F) | 0x80);  // Write the lower 7 bits with the MSB set to 1
                value >>>= 7;  // Right shift the value by 7 bits to process the next chunk
            }
        }

        byte[] result = new byte[position];  // Copy the encoded bytes to a result array of the correct length
        System.arraycopy(buffer, 0, result, 0, position);
        return result;
    }

    /**
     * Decodes a varint-encoded byte array from a given position.
     *
     * @param encoded The varint-encoded byte array.
     * @param position The starting position to decode from.
     * @return The decoded integer value.
     */
    public static int decodeVarint(byte[] encoded, int position) {
        int value = 0;
        int shift = 0;

        while (true) {
            byte currentByte = encoded[position++];
            value |= (currentByte & 0x7F) << shift;

            if ((currentByte & 0x80) == 0) {  // If the MSB is not set, this is the last byte
                break;
            }
            shift += 7;
        }

        return value;
    }

    /**
     * Returns the length of a varint-encoded integer.
     *
     * @param value The varint-encoded integer.
     * @return The length in bytes.
     */
    public static int getVarintLength(int value) {
        int length = 0;
        while (true) {
            length++;
            if ((value & ~0x7F) == 0) {
                break;
            }
            value >>>= 7;
        }
        return length;
    }

    /**
     * Class to hold the result of decoding.
     */
    public static class DecodedResult {
        public final int fieldNumber;
        public final int value;

        public DecodedResult(int fieldNumber, int value) {
            this.fieldNumber = fieldNumber;
            this.value = value;
        }
    }
}