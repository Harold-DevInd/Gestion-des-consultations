package hepl.faad.serveurs_java.library;

import java.security.*;
import javax.crypto.*;

public class CryptoManagement{
    public static byte[] CryptSymDES(SecretKey cle,byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher chiffrementE = null;
        try {
            chiffrementE = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        chiffrementE.init(Cipher.ENCRYPT_MODE, cle);
        return chiffrementE.doFinal(data);
    }

    public static byte[] DecryptSymDES(SecretKey cle,byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher chiffrementD = null;
        try {
            chiffrementD = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        chiffrementD.init(Cipher.DECRYPT_MODE, cle);
        return chiffrementD.doFinal(data);
    }

    public static byte[] CryptAsymRSA(PublicKey cle,byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher chiffrementE = null;
        try {
            chiffrementE = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        chiffrementE.init(Cipher.ENCRYPT_MODE, cle);
        return chiffrementE.doFinal(data);
    }

    public static byte[] DecryptAsymRSA(PrivateKey cle,byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher chiffrementD = null;
        try {
            chiffrementD = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        chiffrementD.init(Cipher.DECRYPT_MODE, cle);
        return chiffrementD.doFinal(data);
    }
    }