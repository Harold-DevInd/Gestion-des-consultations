package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

import javax.crypto.SecretKey;

public class ReponseLOGIN implements Reponse {
    private boolean success;
    private byte[] sessionKey;
    private long sel;

    public ReponseLOGIN() {
    }

    public boolean isSuccess() {
        return success;
    }
    public byte[] getSessionKey() {return sessionKey;}
    public long getSel() {return sel;}
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }
    public void setSel(long sel) {
        this.sel = sel;
    }
}
