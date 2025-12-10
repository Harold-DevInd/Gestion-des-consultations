package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteLOGIN implements Requete {
    private int idMedecin;
    private String nom;
    private String password;
    private byte[] disget;

    public RequeteLOGIN(int id, String nom, String password, byte[] disget) {
        this.idMedecin = id;
        this.nom = nom;
        this.password = password;
        this.disget = disget;
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

    public byte[] getDisget() { return disget; }
}
