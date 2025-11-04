package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

public class RequeteDELETECONSULTATION implements Requete {
    private int idConsultation;

    public RequeteDELETECONSULTATION(int idConsultation) {
        this.idConsultation = idConsultation;
    }

    int getIdConsultation() {
        return idConsultation;
    }
}
