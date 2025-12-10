package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

import java.io.Serializable;

public class ReponseLISTREPORTS implements Reponse {
    private byte[] listeReports;
    private byte[] hmacReponse;

    public ReponseLISTREPORTS(byte[] listeReports, byte[] hmacReponse) {
        this.listeReports = listeReports;
        this.hmacReponse = hmacReponse;
    }

    public byte[] getListeReports() { return listeReports; }
    public byte[] getHmacReponse() { return hmacReponse; }
}
