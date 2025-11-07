package hepl.faad.serveurs_java.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Patient implements Entity,Serializable {

    private Integer idPatient;
    private String lastName;
    private String firstName;
    private LocalDate dateNaissance;

    public Patient() {}

    public Patient(Integer idPatient, String lastName, String firstName, LocalDate dateNaissance) {
        this.idPatient = idPatient;
        this.lastName = lastName;
        this.firstName = firstName;
        this.dateNaissance = dateNaissance;
    }

    public Integer getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(Integer idPatient) {
        this.idPatient = idPatient;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "idPatient=" + idPatient +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", dateNaissance=" + dateNaissance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(idPatient, patient.idPatient) && Objects.equals(lastName, patient.lastName) && Objects.equals(firstName, patient.firstName) && Objects.equals(dateNaissance, patient.dateNaissance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPatient, lastName, firstName, dateNaissance);
    }
}
