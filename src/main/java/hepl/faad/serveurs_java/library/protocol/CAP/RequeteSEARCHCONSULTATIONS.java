package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;

public class RequeteSEARCHCONSULTATIONS implements Requete {
    private Integer idConsultation;
    private Doctor doctor;
    private Patient patient;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public RequeteSEARCHCONSULTATIONS(Integer id, Doctor doctor, Patient patient, LocalDate dateDebut, LocalDate dateFin) {
        this.idConsultation = id;
        this.doctor = doctor;
        this.patient = patient;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Integer getIdConsultation() {
        return idConsultation;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }
}
