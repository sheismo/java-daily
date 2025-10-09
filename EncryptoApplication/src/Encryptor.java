import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    private static String ENCRYPTION_KEY;

    public Encryptor(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("encryption key must not be null or empty:::");
        }

        ENCRYPTION_KEY = key;
    }

    public String decrypt(String encryptedValueHex) throws Exception {
        // Ensure 32-byte key and 16-byte IV
        byte[] keyBytes = ENCRYPTION_KEY.substring(0, 32).getBytes(StandardCharsets.UTF_8); // 32 bytes
        byte[] ivBytes = ENCRYPTION_KEY.substring(0, 16).getBytes(StandardCharsets.UTF_8); // 16 bytes

        // Initialize key and IV
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // Create Cipher instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        System.out.println("about to convert hex::");
        byte[] encryptedValue = hexToBytes(encryptedValueHex);
        System.out.println("successfully converted to hex:::");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedValue = cipher.doFinal(encryptedValue);
        System.out.println("cipher.dofinal");

        return new String(decryptedValue, StandardCharsets.UTF_8);
    }

    public String encrypt(String value) throws Exception {
        // Ensure 32-byte key and 16-byte IV
        byte[] keyBytes = ENCRYPTION_KEY.substring(0, 32).getBytes(StandardCharsets.UTF_8); // 32 bytes
        byte[] ivBytes = ENCRYPTION_KEY.substring(0, 16).getBytes(StandardCharsets.UTF_8); // 16 bytes

        // Initialize key and IV
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encryptedValue);
    }

    public String decrypt2 (String encryptedValue) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Ensure 32-byte key and 16-byte IV
        byte[] keyBytes = ENCRYPTION_KEY.substring(0, 32).getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = ENCRYPTION_KEY.substring(0, 16).getBytes(StandardCharsets.UTF_8);

        // Initialize key and IV
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // Decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // Decrypt the data
        System.out.println(encryptedValue);
        byte[] decryptedValue = cipher.doFinal(encryptedValue.getBytes(StandardCharsets.UTF_8));

        // Remove zero padding
        int paddingEndIndex = decryptedValue.length;
        while (paddingEndIndex > 0 && decryptedValue[paddingEndIndex - 1] == 0) {
            paddingEndIndex--;
        }
        byte[] unpaddedValue = new byte[paddingEndIndex];
        System.arraycopy(decryptedValue, 0, unpaddedValue, 0, paddingEndIndex);

        // Convert to string
        String plainText = new String(unpaddedValue);
        System.out.println("Decrypted value for Tomisin and Ope: " + plainText);
        return plainText;
    }

    // Helper method to convert Hex string to byte array
    private static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
        bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
        + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    // Helper method to convert byte array to Hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
