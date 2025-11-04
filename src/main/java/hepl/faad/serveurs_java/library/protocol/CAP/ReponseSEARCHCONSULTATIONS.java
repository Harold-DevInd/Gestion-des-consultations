package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Reponse;
import hepl.faad.serveurs_java.model.entity.Consultation;

import java.util.List;

public class ReponseSEARCHCONSULTATIONS implements Reponse {
    private List<Consultation> consultations;

    public ReponseSEARCHCONSULTATIONS(List<Consultation> consults) {
        this.consultations = consults;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }
}
