package hepl.faad.serveurs_java;

import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class test
{
    public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());

        String login = "wagner", password1 = "abc123", password2 = "abc123";

        System.out.println("Instanciation du message digest 1");
        MessageDigest md1 = MessageDigest.getInstance("SHA-1","BC");
        md1.update(login.getBytes());
        md1.update(password1.getBytes());
        byte[] digest1 = md1.digest();

        System.out.println("Instanciation du message digest 2");
        MessageDigest md2 = MessageDigest.getInstance("SHA-1","BC");
        md2.update(login.getBytes());
        md2.update(password2.getBytes());
        byte[] digest2 = md2.digest();

        System.out.println("Comparaison des digests");
        if (MessageDigest.isEqual(digest1,digest2)) System.out.println("OK !");
        else System.out.println("KO...");
    }
}
