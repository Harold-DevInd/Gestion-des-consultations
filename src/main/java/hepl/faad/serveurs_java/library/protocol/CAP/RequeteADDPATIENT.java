package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteADDPATIENT implements Requete {
    private String lastName;
    private String firstName;

    public RequeteADDPATIENT(String nom, String prenom) {
        this.lastName = nom;
        this.firstName = prenom;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
