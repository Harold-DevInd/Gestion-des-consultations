package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteLISTREPORTS implements Requete {
    private byte[] message;

    public RequeteLISTREPORTS(byte[] message) {
        this.message = message;
    }

    public byte[] getMessage() { return message; }
}
