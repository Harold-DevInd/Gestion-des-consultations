package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Reponse;

public class ReponseADDPATIENT implements Reponse {
    private Integer idPatient;

    public ReponseADDPATIENT(Integer idPatient) {
        this.idPatient = idPatient;
    }

    public Integer getIdPatient() {
        return idPatient;
    }
}
