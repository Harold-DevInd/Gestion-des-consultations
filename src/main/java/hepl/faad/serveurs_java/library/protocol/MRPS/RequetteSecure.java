package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Requete;


public class RequetteSecure implements Requete {
    private byte[] message;
    private byte[] signature;
    private byte[] hash;

    public RequetteSecure() { }

    public byte[] getMessage() { return message; }
    public byte[] getSignature() { return signature; }
    public byte[] getHash() { return hash; }
    public void setMessage(byte[] message) { this.message = message; }
    public void setSignature(byte[] signature) { this.signature = signature; }
    public void setHash(byte[] hash) { this.hash = hash; }
}
