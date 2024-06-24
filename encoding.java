public class VarintEncoder {

    public static void main(String[] args) {
        int value = 150;
        byte[] encodedValue = encodeVarint(value);

        System.out.print("Encoded value: ");
        for (byte b : encodedValue) {
            System.out.printf("%02X ", b);
        }
    }

    public static byte[] encodeVarint(int value) {
        // A maximum of 5 bytes is enough to encode a 32-bit integer as a varint.
        byte[] buffer = new byte[5];
        int position = 0;

        while (true) {
            if ((value & ~0x7F) == 0) {
                buffer[position++] = (byte) value;
                break;
            } else {
                buffer[position++] = (byte) ((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }

        // Copy the exact encoded bytes to a new array of the correct length.
        byte[] result = new byte[position];
        System.arraycopy(buffer, 0, result, 0, position);
        return result;
    }
}