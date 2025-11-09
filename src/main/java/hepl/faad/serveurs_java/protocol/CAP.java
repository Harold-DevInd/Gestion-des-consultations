package hepl.faad.serveurs_java.protocol;

import hepl.faad.serveurs_java.library.protocol.CAP.*;
import hepl.faad.serveurs_java.library.protocol.Protocole;
import hepl.faad.serveurs_java.library.serveur.FinConnexionException;
import hepl.faad.serveurs_java.library.serveur.Logger;
import hepl.faad.serveurs_java.library.serveur.Reponse;
import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.dao.ConsultationDAO;
import hepl.faad.serveurs_java.model.dao.DoctorDAO;
import hepl.faad.serveurs_java.model.dao.PatientDAO;
import hepl.faad.serveurs_java.model.dao.SpecialtyDAO;
import hepl.faad.serveurs_java.model.entity.Consultation;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;
import hepl.faad.serveurs_java.model.viewmodel.ConsultationSearchVM;
import hepl.faad.serveurs_java.model.viewmodel.DoctorSearchVM;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CAP implements Protocole {
    private Logger logger;
    private HashMap<String, Socket> clientsConnectes;

    private ConsultationDAO consultationDAO;
    private DoctorDAO doctorDAO;
    private PatientDAO patientDAO;

    public CAP(Logger log) throws SQLException {
        this.logger = log;
        clientsConnectes = new HashMap<>();

        this.consultationDAO = new ConsultationDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
    }

    @Override
    public String getNom() {
        return "CAP";
    }

    @Override
    public synchronized Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException {

        try {
            if(requete instanceof RequeteLOGIN)
                return TraiteRequeteLOGIN((RequeteLOGIN) requete, socket);

            if(requete instanceof RequeteLOGOUT) {
                TraiteRequeteLOGOUT((RequeteLOGOUT) requete, socket);
                return null;
            }

            if(requete instanceof RequeteADDCONSULTATION)
                return TraiteRequeteADDCONSULTATION((RequeteADDCONSULTATION) requete, socket);

            if(requete instanceof RequeteADDPATIENT)
                return TraiteRequeteADDPATIENT((RequeteADDPATIENT) requete, socket);

            if(requete instanceof RequeteUPDATECONSULTATION)
                return TraiteRequeteUPDATECONSULTATION((RequeteUPDATECONSULTATION) requete, socket);

            if(requete instanceof RequeteSEARCHCONSULTATIONS)
                return TraiteRequeteSEARCHCONSULTATIONS((RequeteSEARCHCONSULTATIONS) requete, socket);

            if(requete instanceof RequeteDELETECONSULTATION)
                return TraiteRequeteDELETECONSULTATION((RequeteDELETECONSULTATION) requete, socket);
        } catch (SQLException e) {
            logger.Trace("Erreur lors du traitement de la requete : " + requete.getClass().getSimpleName());

            if(requete instanceof RequeteLOGIN)
                return new ReponseADDPATIENT(-1);

            if(requete instanceof RequeteSEARCHCONSULTATIONS)
                return new ReponseSEARCHCONSULTATIONS(null);

            if(requete instanceof RequeteADDCONSULTATION)
                return new ReponseADDCONSULTATION(false);

            if(requete instanceof RequeteUPDATECONSULTATION)
                return new ReponseUPDATECONSULTATION(false);

            if(requete instanceof RequeteDELETECONSULTATION)
                return new ReponseDELETECONSULTATION(false);
        }

        return null;
    }

    private ReponseLOGIN TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket) throws SQLException {
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        if(!estPresent(socket))
        {
            int id = requete.getIdMedecin();
            String nom = requete.getNom();
            String password  = requete.getPassword();
            boolean valide = false;

            List<Doctor> docs = doctorDAO.load(new DoctorSearchVM(id, null, nom, password));
            valide = docs.stream().anyMatch((x) -> (x.getLastName().equals(nom) && x.getFirstName().equals(password)));

            if(valide) {
                logger.Trace(requete.getNom() + " correctement loggé \n");
                clientsConnectes.put(requete.getNom(), socket);
                return new ReponseLOGIN(valide);
            }
            logger.Trace("\nErreur de connexion de " + requete.getNom());
            return new ReponseLOGIN(valide);
        }
        logger.Trace("\nErreur client " + requete.getNom() + "Deja connecté");
        return new ReponseLOGIN(false);
    }

    private void TraiteRequeteLOGOUT(RequeteLOGOUT requete, Socket socket) throws FinConnexionException, SQLException {
        logger.Trace("RequeteLOGOUT reçue de " + requete.getDoctor().getLastName() + requete.getDoctor().getFirstName());

        if(estPresent(socket)) {
            clientsConnectes.remove(requete.getDoctor().getLastName());
            logger.Trace(requete.getDoctor().getLastName() + requete.getDoctor().getFirstName() + " correctement deconnecte\n");
            throw new FinConnexionException(null);
        }
        else
            logger.Trace(requete.getDoctor().getLastName() + requete.getDoctor().getFirstName() + " non conneté \n");
    }

    private ReponseADDPATIENT TraiteRequeteADDPATIENT(RequeteADDPATIENT requete, Socket socket) throws SQLException{
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        Patient patient = null;

        if(estPresent(socket)) {
            String lastName = requete.getLastName();
            String firstName = requete.getFirstName();

            patient = new Patient(null, lastName, firstName, LocalDate.now());

            patientDAO.save(patient);

            logger.Trace("\nAjout du patient avec succes");
            return new ReponseADDPATIENT(patient.getIdPatient());
        }
        else {
            for(Socket sock : clientsConnectes.values()) {
                if(sock.equals(socket)) {
                    logger.Trace("Erreur requete" +requete.getClass().toString()+ " car  client " + socket + " non conneté \n");
                }
            }
        }
        return new ReponseADDPATIENT(-1);
    }

    private ReponseSEARCHCONSULTATIONS TraiteRequeteSEARCHCONSULTATIONS(RequeteSEARCHCONSULTATIONS requete, Socket socket) throws SQLException {
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        if(estPresent(socket)) {
            List<Consultation> listeConsultations;
            System.out.println("\n(Protocol) Ajout de la consultations debut");
            ConsultationSearchVM consultationSearchVM = new ConsultationSearchVM(requete.getIdConsultation(), requete.getDoctor(), requete.getPatient(),
                    requete.getDateDebut(), requete.getDateFin());

            listeConsultations = consultationDAO.load(consultationSearchVM);

            logger.Trace("(Protocol) Liste de consultation trouve");
            return new ReponseSEARCHCONSULTATIONS(listeConsultations);
        }
        else {
            for(Socket sock : clientsConnectes.values()) {
                if(sock.equals(socket)) {
                    logger.Trace("Erreur requete" +requete.getClass().toString()+ " car  client " + socket + " non conneté \n");
                }
            }
        }
        return null;
    }

    private ReponseADDCONSULTATION TraiteRequeteADDCONSULTATION(RequeteADDCONSULTATION requete, Socket socket) throws SQLException {
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        List<Consultation> listeConsultation = new ArrayList<>();
        Doctor doctor = requete.getDoctor();
        LocalTime heure = requete.getTime();
        LocalTime LIMITE_HEURE = LocalTime.of(17, 0);
        int duree = requete.getDure();

        if(estPresent(socket)) {
            for (int i = 0; i < requete.getNombreConsultation(); i++)
            {
                LocalTime heureConsultation = heure.plusMinutes(i * duree);
                // Si la consultation dépasse la limite, on refuse
                if (heureConsultation.isAfter(LIMITE_HEURE) || heureConsultation.equals(LIMITE_HEURE)) {
                    logger.Trace("\nConsultation demandée au-delà de 17h00 !");
                    return new ReponseADDCONSULTATION(false);
                }
                listeConsultation.add(new Consultation(null, doctor, null, requete.getDate(),
                        heureConsultation.toString(), null)
                );
            }

            for(Consultation consultation : listeConsultation)
                consultationDAO.save(consultation);

            logger.Trace("\nAjout des consultations avec succes");
            return new ReponseADDCONSULTATION(true);
        }
        else {
            for(Socket sock : clientsConnectes.values()) {
                if(sock.equals(socket)) {
                    logger.Trace("Erreur requete" +requete.getClass().toString()+ " car  client " + socket + " non conneté \n");
                }
            }
        }
        return null;
    }

    private ReponseUPDATECONSULTATION TraiteRequeteUPDATECONSULTATION(RequeteUPDATECONSULTATION requete, Socket socket) throws SQLException {
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        if(estPresent(socket)) {
            Consultation consultation = new Consultation(requete.getIdConsultation(), requete.getDoctor(), requete.getPatient(),
                    requete.getDateConsultation(), requete.getTimeConsultation().toString(), requete.getReason());

            consultationDAO.save(consultation);

            logger.Trace("\nAjout de la consultations avec succes");
            return new ReponseUPDATECONSULTATION(true);
        }
        else {
            for(Socket sock : clientsConnectes.values()) {
                if(sock.equals(socket)) {
                    logger.Trace("Erreur requete" +requete.getClass().toString()+ " car  client " + socket + " non conneté \n");
                }
            }
        }
        return new ReponseUPDATECONSULTATION(false);
    }

    private ReponseDELETECONSULTATION TraiteRequeteDELETECONSULTATION(RequeteDELETECONSULTATION requete, Socket socket) throws SQLException {
        logger.Trace("Requete " + requete.getClass().toString() + " recu de " + socket);

        boolean res = false;

        if(estPresent(socket)) {
            res = consultationDAO.delete(requete.getIdConsultation());

            logger.Trace("Suppression de la consultation");
            return new ReponseDELETECONSULTATION(res);
        }
        else {
            for(Socket sock : clientsConnectes.values()) {
                if(sock.equals(socket)) {
                    logger.Trace("Erreur requete" +requete.getClass().toString()+ " car  client " + socket + " non conneté \n");
                }
            }
        }
        return new ReponseDELETECONSULTATION(res);
    }

    boolean estPresent(Socket socket) {
        return clientsConnectes.containsValue(socket);
    }
}
