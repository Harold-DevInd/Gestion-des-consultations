package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;

import java.time.LocalDate;
import java.time.LocalTime;

public class RequeteADDCONSULTATION implements Requete {
    LocalDate date;
    LocalTime time;
    int dure;
    int nombreConsultation;

    public RequeteADDCONSULTATION(LocalDate date, LocalTime time, int dure, int nombreConsultation) {
        this.date = date;
        this.time = time;
        this.dure = dure;
        this.nombreConsultation = nombreConsultation;
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
