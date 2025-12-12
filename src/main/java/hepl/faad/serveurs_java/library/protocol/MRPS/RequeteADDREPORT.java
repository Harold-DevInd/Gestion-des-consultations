package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteADDREPORT implements Requete {
    private byte[] message;
    private byte[] signature;

    public RequeteADDREPORT(byte[] message, byte[] signature) {
        this.message = message;
        this.signature = signature;
    }

    public byte[] getMessage() { return message; }
    public byte[] getSignature() { return signature; }
}
