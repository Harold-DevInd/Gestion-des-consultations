package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteADDPATIENT implements Requete {
    private String nom;
    private String prenom;

    public RequeteADDPATIENT(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }
}
