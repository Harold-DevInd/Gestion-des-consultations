package hepl.faad.serveurs_java.library.protocol.CAP;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Doctor;

import javax.print.Doc;

public class RequeteLOGOUT implements Requete {
    private Doctor doctor;

    public RequeteLOGOUT(Doctor d) {
        this.doctor = d;
    }

    public Doctor getDoctor() {
        return doctor;
    }
}
