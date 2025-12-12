package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteLOGIN implements Requete {
    private int idMedecin;
    private byte[] disget;
    private long sel;

    public RequeteLOGIN() {}

    public RequeteLOGIN(int id, long sel, byte[] disget) {
        this.idMedecin = id;
        this.sel = sel;
        this.disget = disget;
    }

    public int getIdMedecin() {
        return idMedecin;
    }
    public byte[] getDisget() { return disget; }
    public long getSel() {
        return sel;
    }

}
