package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteLOGOUT implements Requete {
    private String login;

    public RequeteLOGOUT(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
