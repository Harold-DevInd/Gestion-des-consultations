package hepl.faad.serveurs_java.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

public class Consultation implements Entity, Serializable {

    private Integer idConsultation;
    Doctor doctor;
    Patient patient;
    LocalDate dateConsultation;
    String heureConsultation;
    String raison;

    public Consultation(Integer idConsultation, Doctor doctor, Patient patient, LocalDate dateConsultation, String heureConsultation, String raison) {
        this.idConsultation = idConsultation;
        this.doctor = doctor;
        this.patient = patient;
        this.dateConsultation = dateConsultation;
        this.heureConsultation = heureConsultation;
        this.raison = raison;
    }

    public Consultation() {}

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

    public String getHeureConsultation() {
        return heureConsultation;
    }

    public void setHeureConsultation(String heureConsultation) {
        this.heureConsultation = heureConsultation;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "idConsultation=" + idConsultation +
                ", doctor=" + doctor +
                ", patient=" + patient +
                ", dateConsultation=" + dateConsultation +
                ", heureConsultation=" + heureConsultation +
                ", raison='" + raison + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Consultation that = (Consultation) o;
        return Objects.equals(idConsultation, that.idConsultation) && Objects.equals(doctor, that.doctor) && Objects.equals(patient, that.patient) && Objects.equals(dateConsultation, that.dateConsultation) && Objects.equals(heureConsultation, that.heureConsultation) && Objects.equals(raison, that.raison);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idConsultation, doctor, patient, dateConsultation, heureConsultation, raison);
    }
}
