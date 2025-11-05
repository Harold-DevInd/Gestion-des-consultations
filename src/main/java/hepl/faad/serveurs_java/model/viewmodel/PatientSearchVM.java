package hepl.faad.serveurs_java.model.viewmodel;

import java.util.Date;

public class PatientSearchVM {
    private Integer idPatient;
    private String lastName;
    private String firstName;

    public PatientSearchVM() {};

    public PatientSearchVM(Integer idPatient, String lastName, String firstName) {
        this.idPatient = idPatient;
        this.lastName = lastName;
        this.firstName = firstName;
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
}
