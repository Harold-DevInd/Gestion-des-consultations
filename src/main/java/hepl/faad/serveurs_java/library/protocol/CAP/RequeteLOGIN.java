package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteLOGIN implements Requete {
    private String nom;
    private String password;

    public RequeteLOGIN(String nom, String password) {
        this.nom = nom;
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public String getPassword() {
        return password;
    }
}
