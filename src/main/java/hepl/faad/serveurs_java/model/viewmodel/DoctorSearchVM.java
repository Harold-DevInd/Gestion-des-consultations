package hepl.faad.serveurs_java.model.viewmodel;

import hepl.faad.serveurs_java.model.entity.Specialty;

public class DoctorSearchVM {
    private Integer idDoctor;
    private Specialty specialty;
    private String lastName;
    private String firstName;

    public DoctorSearchVM() {};

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
}
