package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseADDREPORT implements Reponse {
    private byte[] message;

    public ReponseADDREPORT(byte[] message) { this.message = message; }

    public byte[] getMessage() { return message; }
    public void setMessage(byte[] message) { this.message = message; }
}
