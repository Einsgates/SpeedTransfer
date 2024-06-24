public class VarintEncoder {

    public static void main(String[] args) {
        int value = 150;
        byte[] encodedValue = encodeVarint(value);

        // Print the encoded value in hexadecimal format
        System.out.print("Encoded value: ");
        for (byte b : encodedValue) {
            System.out.printf("%02X ", b);
        }
    }

    /**
     * Encodes an integer as a varint.
     *
     * @param value The integer value to encode.
     * @return The varint-encoded byte array.
     */
    public static byte[] encodeVarint(int value) {
        // A buffer to hold the encoded bytes (max 5 bytes for a 32-bit integer)
        byte[] buffer = new byte[5];
        int position = 0;

        // Continue encoding until the value fits in 7 bits
        while (true) {
            // If the remaining value fits in 7 bits, write it and break
            if ((value & ~0x7F) == 0) {
                buffer[position++] = (byte) value;
                break;
            } else {
                // Write the lower 7 bits with the MSB set to 1
                buffer[position++] = (byte) ((value & 0x7F) | 0x80);
                // Right shift the value by 7 bits to process the next chunk
                value >>>= 7;
            }
        }

        // Copy the encoded bytes to a result array of the correct length
        byte[] result = new byte[position];
        System.arraycopy(buffer, 0, result, 0, position);
        return result;
    }
}