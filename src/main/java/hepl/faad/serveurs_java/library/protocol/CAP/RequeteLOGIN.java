package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteLOGIN implements Requete {
    private int idMedecin;
    private String nom;
    private String password;

    public RequeteLOGIN(int id, String nom, String password) {
        this.idMedecin = id;
        this.nom = nom;
        this.password = password;
    }

    public int getIdMedecin() {
        return idMedecin;
    }

    public String getNom() {
        return nom;
    }

    public String getPassword() {
        return password;
    }
}
