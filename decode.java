public class VarintEncoderDecoder {

    public static void main(String[] args) {
        int originalValue = 150;

        // Encode the integer value
        byte[] encodedValue = encodeVarint(originalValue);
        System.out.print("Encoded value: ");
        for (byte b : encodedValue) {
            System.out.printf("%02X ", b);
        }
        System.out.println();

        // Decode the encoded value
        int decodedValue = decodeVarint(encodedValue);
        System.out.println("Decoded value: " + decodedValue);
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
     * Decodes a varint-encoded byte array back to an integer.
     *
     * @param encoded The varint-encoded byte array.
     * @return The decoded integer value.
     */
    public static int decodeVarint(byte[] encoded) {
        int value = 0;
        int position = 0;
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
}