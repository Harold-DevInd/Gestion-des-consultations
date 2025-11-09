package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

public class RequeteUPDATECONSULTATION implements Requete {
    private Integer idConsultation;
    private Doctor doctor;
    private Patient patient;
    private LocalDate dateConsultation;
    private LocalTime timeConsultation;
    private String reason;

    public RequeteUPDATECONSULTATION(Integer id, Doctor d, Patient p, LocalDate date, LocalTime time, String reason) {
        this.idConsultation = id;
        this.patient = p;
        this.doctor = d;
        this.dateConsultation = date;
        this.timeConsultation = time;
        this.reason = reason;
    }

    public Integer getIdConsultation() {
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

    public Doctor getDoctor() {
        return doctor;
    }

    public String getReason() {
        return reason;
    }
}
