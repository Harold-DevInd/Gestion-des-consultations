package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Reponse;

import java.util.List;

public class ReponseLISTREPORTS implements Reponse {
    private List<byte[]> listeReports;
    private byte[] hmacReponse;

    public ReponseLISTREPORTS(List<byte[]> listeReports, byte[] hmacReponse) {
        this.listeReports = listeReports;
        this.hmacReponse = hmacReponse;
    }

    public List<byte[]> getListeReports() { return listeReports; }
    public byte[] getHmacReponse() { return hmacReponse; }
}
