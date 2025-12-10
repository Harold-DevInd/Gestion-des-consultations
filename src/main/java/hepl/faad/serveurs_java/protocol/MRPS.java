package hepl.faad.serveurs_java.protocol;


import hepl.faad.serveurs_java.library.protocol.CAP.*;
import hepl.faad.serveurs_java.library.protocol.CAP.ReponseLOGIN;
import hepl.faad.serveurs_java.library.protocol.CAP.RequeteLOGIN;
import hepl.faad.serveurs_java.library.protocol.CAP.RequeteLOGOUT;
import hepl.faad.serveurs_java.library.protocol.MRPS.*;
import hepl.faad.serveurs_java.library.protocol.Protocole;
import hepl.faad.serveurs_java.library.serveur.FinConnexionException;
import hepl.faad.serveurs_java.library.serveur.Logger;
import hepl.faad.serveurs_java.library.serveur.Reponse;
import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.dao.ConsultationDAO;
import hepl.faad.serveurs_java.model.dao.DoctorDAO;
import hepl.faad.serveurs_java.model.dao.PatientDAO;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.viewmodel.DoctorSearchVM;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class MRPS implements Protocole {
    private Logger logger;
    private ConsultationDAO consultationDAO;
    private DoctorDAO doctorDAO;
    private PatientDAO patientDAO;

    public MRPS(Logger log) {
        this.logger = log;
        this.consultationDAO = new ConsultationDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
    }

    @Override
    public String getNom() {
        return "MPRS";
    }

    @Override
    public Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException {
        //try {
            if(requete instanceof RequeteLOGIN)
                return TraiteRequeteLOGIN((RequeteLOGIN) requete, socket);

            if(requete instanceof RequeteLOGOUT) {
                TraiteRequeteLOGOUT((RequeteLOGOUT) requete, socket);
                return null;
            }

            if(requete instanceof RequeteADDREPORT)
                return TraiteRequeteADDREPORT((RequeteADDREPORT) requete, socket);

            if(requete instanceof RequeteEDITREPORT)
                return TraiteRequeteEDITREPORT((RequeteEDITREPORT) requete, socket);

            if(requete instanceof RequeteLISTREPORTS)
                return TraiteRequeteLISTREPORTS((RequeteLISTREPORTS) requete, socket);

        /*} catch (SQLException e) {
            logger.Trace("Erreur lors du traitement de la requete : " + requete.getClass().getSimpleName());

            if(requete instanceof RequeteLOGIN)
                return new ReponseADDPATIENT(-1);

            if(requete instanceof RequeteADDREPORT)
                return new ReponseADDREPORT(false);

            if(requete instanceof RequeteADDCONSULTATION)
                return new ReponseADDCONSULTATION(false);

            if(requete instanceof RequeteEDITREPORT)
                return new ReponseEDITREPORT(false);

            if(requete instanceof RequeteLISTREPORTS)
                return new ReponseLISTREPORTS(null, null);
        }*/

        return null;
    }

    private Reponse TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket) {
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        if(socket != null)
        {
            int id = requete.getIdMedecin();
            String nom = requete.getNom();
            String password  = requete.getPassword();
            boolean valide = false;

            List<Doctor> docs = doctorDAO.load(new DoctorSearchVM(id, null, nom, password));
            valide = docs.stream().anyMatch((x) -> (x.getLastName().equals(nom) && x.getFirstName().equals(password)));

            if(valide) {
                logger.Trace(requete.getNom() + " correctement loggé \n");
                return new ReponseLOGIN(valide);
            }
            logger.Trace("\nErreur de connexion de " + requete.getNom());
            return new ReponseLOGIN(valide);
        }
        logger.Trace("\nErreur client " + requete.getNom() + ", socket null");
        return new ReponseLOGIN(false);
    }

    private void TraiteRequeteLOGOUT(RequeteLOGOUT requete, Socket socket) throws FinConnexionException {
        logger.Trace("RequeteLOGOUT reçue de " + requete.getDoctor().getLastName() + requete.getDoctor().getFirstName());

        if(socket != null) {
            logger.Trace(requete.getDoctor().getLastName() + requete.getDoctor().getFirstName() + " correctement deconnecte\n");
            throw new FinConnexionException(null);
        }
        else
            logger.Trace(requete.getDoctor().getLastName() + requete.getDoctor().getFirstName() + " non conneté \n");
    }

    private Reponse TraiteRequeteADDREPORT(RequeteADDREPORT requete, Socket socket) {
        return null;
    }

    private Reponse TraiteRequeteEDITREPORT(RequeteEDITREPORT requete, Socket socket) {
        return null;
    }

    private Reponse TraiteRequeteLISTREPORTS(RequeteLISTREPORTS requete, Socket socket) {
        return null;
    }
}
