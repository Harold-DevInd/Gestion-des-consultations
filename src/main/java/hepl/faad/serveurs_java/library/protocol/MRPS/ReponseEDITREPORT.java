package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseEDITREPORT implements Reponse {
    private boolean success;

    public ReponseEDITREPORT(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
