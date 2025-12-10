package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseADDREPORT implements Reponse {
    private boolean success;

    public ReponseADDREPORT(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
