package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;

public class RequeteSEARCHCONSULTATIONS implements Requete {
    private int idConsultation;
    private Doctor doctor;
    private Patient patient;
    private LocalDate date;

    public RequeteSEARCHCONSULTATIONS(int id, Doctor doctor, Patient patient, LocalDate date) {
        this.idConsultation = id;
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
    }

    public int getIdConsultation() {
        return idConsultation;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDate getDate() {
        return date;
    }
}
