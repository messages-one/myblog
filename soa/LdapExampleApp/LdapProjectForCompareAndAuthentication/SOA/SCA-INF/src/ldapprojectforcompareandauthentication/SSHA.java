package ldapprojectforcompareandauthentication;

import java.io.IOException;
import java.security.MessageDigest;
import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class SSHA {
    private static BASE64Encoder encoder = new BASE64Encoder();
    private static BASE64Decoder decoder = new BASE64Decoder();
    private static SSHA INSTANCE = new SSHA("SHA");
    private static final String SALT = "0cca15668661c5dc";
    private MessageDigest md = null;
    private SSHA(String shaEnc) {
        super();
        try {
            md = MessageDigest.getInstance(shaEnc);
        } catch (NoSuchAlgorithmException nsae) {
            // TODO: Add catch code
            System.out.println("error creating md " + nsae);
        }
    }
    public static SSHA getInstance() {
        return INSTANCE;
    }
    public void setAlgorithm(String shaEnc) {
        INSTANCE = new SSHA(shaEnc);
    }
    public String createDigest(String salt, String password) {
        return createDigest(salt.getBytes(), password);
    }
    public String createDigest(byte[] salt, String password) {
        String label = "{SSHA}";
        md.reset();
        md.update(password.getBytes());
        md.update(salt);
        byte[] passwordHash = md.digest();
        return label + encoder.encode(concatBytes(passwordHash, salt));
    }
    public String createDigest(String password) {
        return INSTANCE.createDigest(getStringSaltFromHex(), password);
    }
    private static byte[] concatBytes(byte[] l, byte[] r) {
        byte[] b = new byte[l.length + r.length];
        System.arraycopy(l, 0, b, 0, l.length);
        System.arraycopy(r, 0, b, l.length, r.length);
        return b;
    }
    public String getStringSaltFromHex() {
        byte[] bytes = new BigInteger(SALT, 16).toByteArray();
        return new String(bytes);
    }
}
