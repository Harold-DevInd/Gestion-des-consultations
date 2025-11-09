package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;

public class RequeteADDCONSULTATION implements Requete {
    private Doctor doctor;
    private LocalDate date;
    private LocalTime time;
    private int dure;
    private int nombreConsultation;

    public RequeteADDCONSULTATION(Doctor d, LocalDate date, LocalTime time, int dure, int nombreConsultation) {
        this.doctor = d;
        this.date = date;
        this.time = time;
        this.dure = dure;
        this.nombreConsultation = nombreConsultation;
    }

    public Doctor getDoctor(){
        return doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getDure() {
        return dure;
    }

    public int getNombreConsultation() {
        return nombreConsultation;
    }


}
