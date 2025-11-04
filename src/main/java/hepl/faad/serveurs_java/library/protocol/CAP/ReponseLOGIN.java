package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseLOGIN implements Reponse {
    private boolean success;

    public ReponseLOGIN(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
