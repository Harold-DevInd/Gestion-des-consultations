package hepl.faad.serveurs_java.model.viewmodel;

import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;

public class ConsultationSearchVM {
    private Integer idConsultation;
    private Doctor doctor;
    private Patient patient;
    private LocalDate dateDebutConsultation;
    private LocalDate dateFinConsultation;

    public ConsultationSearchVM() {};

    public ConsultationSearchVM(Integer id, Doctor doctor, Patient patient, LocalDate dateDebutConsultation, LocalDate dateFinConsultation) {
        this.idConsultation = id;
        this.doctor = doctor;
        this.patient = patient;
        this.dateDebutConsultation = dateDebutConsultation;
        this.dateFinConsultation = dateFinConsultation;
    }

    public Integer getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Integer idConsultation) {
        this.idConsultation = idConsultation;
    }

    public Doctor getDoctor() {
        if(doctor != null)
            return doctor;
        else
            return new Doctor();
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        if(patient != null)
            return patient;
        else
            return new Patient();
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDateDebutConsultation() {
        return dateDebutConsultation;
    }
    public void setDateDebutConsultation(LocalDate dateDebutConsultation) {
        this.dateDebutConsultation = dateDebutConsultation;
    }

    public LocalDate getDateFinConsultation() {
        return dateFinConsultation;
    }

    public void setDateFinConsultation(LocalDate dateFinConsultation) {
        this.dateFinConsultation = dateFinConsultation;
    }
}
