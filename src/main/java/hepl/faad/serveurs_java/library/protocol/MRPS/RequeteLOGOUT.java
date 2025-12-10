package hepl.faad.serveurs_java.library.protocol.MRPS;

import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.entity.Doctor;

public class RequeteLOGOUT implements Requete {
    private Doctor doctor;

    public RequeteLOGOUT(Doctor d) {
        this.doctor = d;
    }

    public Doctor getDoctor() {
        return doctor;
    }
}
