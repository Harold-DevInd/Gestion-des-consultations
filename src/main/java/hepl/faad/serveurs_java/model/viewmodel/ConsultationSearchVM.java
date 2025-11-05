package hepl.faad.serveurs_java.model.viewmodel;

import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;

import java.time.LocalDate;

public class ConsultationSearchVM {
    private Integer idConsultation;
    private Doctor doctor;
    private Patient patient;
    private LocalDate dateConsultation;

    public ConsultationSearchVM() {};

    public ConsultationSearchVM(Integer id, Doctor doctor, Patient patient, LocalDate dateConsultation) {
        this.idConsultation = id;
        this.doctor = doctor;
        this.patient = patient;
        this.dateConsultation = dateConsultation;
    }

    public Integer getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(Integer idConsultation) {
        this.idConsultation = idConsultation;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDate dateConsultation) {
        this.dateConsultation = dateConsultation;
    }
}
