package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;

public class RequeteSEARCHCONSULTATIONS implements Requete {
    private Patient patient;
    private LocalDate date;

    public RequeteSEARCHCONSULTATIONS(Patient patient, LocalDate date) {
        this.patient = patient;
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDate getDate() {
        return date;
    }
}
