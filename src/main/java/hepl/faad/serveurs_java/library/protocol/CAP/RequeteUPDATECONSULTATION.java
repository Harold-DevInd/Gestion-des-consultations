package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

public class RequeteUPDATECONSULTATION implements Requete {
    private int idConsultation;
    private LocalDate dateConsultation;
    private LocalTime timeConsultation;
    private Patient patient;
    private String reason;

    public RequeteUPDATECONSULTATION(int id, LocalDate date, LocalTime time,Patient p, String reason) {
        this.idConsultation = id;
        this.dateConsultation = date;
        this.timeConsultation = time;
        this.patient = p;
        this.reason = reason;
    }

    public int getIdConsultation() {
        return idConsultation;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }

    public LocalTime getTimeConsultation() {
        return timeConsultation;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getReason() {
        return reason;
    }
}
