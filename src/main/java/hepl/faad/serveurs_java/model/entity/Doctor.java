package hepl.faad.serveurs_java.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class Doctor implements Entity{

    private Integer idDoctor;
    private Specialty specialty;
    private String lastName;
    private String firstName;

    public Doctor() {}

    public Doctor(Integer idDoctor, Specialty specialty, String lastName, String firstName) {
        this.idDoctor = idDoctor;
        this.specialty = specialty;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Integer getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(Integer idDoctor) {
        this.idDoctor = idDoctor;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
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

    @Override
    public String toString() {
        return "Doctor{" +
                "idDoctor=" + idDoctor +
                ", specialty=" + specialty +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(idDoctor, doctor.idDoctor) && Objects.equals(specialty, doctor.specialty) && Objects.equals(lastName, doctor.lastName) && Objects.equals(firstName, doctor.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDoctor, specialty, lastName, firstName);
    }
}
