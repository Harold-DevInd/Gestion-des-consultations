package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseLOGIN implements Reponse {
    private boolean success;
    private byte[] sessionKey;

    public ReponseLOGIN(boolean success, byte[] sessionkey) {
        this.success = success;
        this.sessionKey = sessionkey;
    }

    public boolean isSuccess() {
        return success;
    }
    public byte[] getSessionKey() {return sessionKey;}
}
