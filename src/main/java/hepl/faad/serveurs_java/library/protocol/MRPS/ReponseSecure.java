package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseSecure implements Reponse {
    private byte[] message;
    private byte[] signature;
    private byte[] hash;

    public ReponseSecure() { }

    public byte[] getMessage() { return message; }
    public byte[] getSignature() { return signature; }
    public byte[] getHash() { return hash; }
    public void setMessage(byte[] message) { this.message = message; }
    public void setSignature(byte[] signature) { this.signature = signature; }
    public void setHash(byte[] hash) { this.hash = hash; }
}
