package hepl.faad.serveurs_java.protocol;

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
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MRPS implements Protocole {
    private Logger logger;
    private ConsultationDAO consultationDAO;
    private DoctorDAO doctorDAO;
    private PatientDAO patientDAO;
    private Map<Socket, SecretKey> clientConnecte;

    public MRPS(Logger log) {
        this.logger = log;
        this.consultationDAO = new ConsultationDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
        this.clientConnecte = new HashMap<Socket, SecretKey>();
    }

    @Override
    public String getNom() {
        return "MPRS";
    }

    @Override
    public Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException {
        Security.addProvider(new BouncyCastleProvider());
        try {
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

        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            logger.Trace("Erreur lors du traitement de la requete : " + requete.getClass().getSimpleName());
            return null;
        }

        return null;
    }

    private Reponse TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        logger.Trace("Requete " + requete.getClass() + " recu de " + socket);
        boolean valide = false;
        ReponseLOGIN reponse = new ReponseLOGIN();
        reponse.setSuccess(valide);

        if(socket != null)
        {
            if(requete.getDisget() == null) {
                int id = requete.getIdMedecin();

                List<Doctor> docs = doctorDAO.load(new DoctorSearchVM(id, null, null, null));
                valide = docs.stream().anyMatch((x) -> (x.getIdDoctor() == id));
                reponse.setSuccess(valide);

                if (valide) {
                    logger.Trace(requete.getIdMedecin() + " correctement loggé \n");
                    reponse.setSel(generateSalt());
                }
                logger.Trace("\nErreur de connexion de " + requete.getIdMedecin());
                return reponse;
            }
            else {
                List<Doctor> docs = doctorDAO.load(new DoctorSearchVM(requete.getIdMedecin(), null, null, null));
                Doctor doctor = docs.get(0);

                byte[] digestServeur;
                MessageDigest md = MessageDigest.getInstance("SHA-1","BC");
                md.update(doctor.getLastName().getBytes());
                md.update(doctor.getFirstName().getBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.write(doctor.getIdDoctor());
                dos.writeLong(requete.getSel());
                md.update(baos.toByteArray());
                digestServeur = md.digest();

                valide = MessageDigest.isEqual(digestServeur, requete.getDisget());
                reponse.setSuccess(valide);

                if(valide) {
                    logger.Trace(doctor.getLastName() + doctor.getFirstName() + " correctement loggé \n");
                    KeyGenerator cleGen = KeyGenerator.getInstance("DES","BC");
                    cleGen.init(new SecureRandom());
                    SecretKey cleSession = cleGen.generateKey();

                    reponse.setSessionKey(cleSession);
                    reponse.setSel(requete.getSel());
                    clientConnecte.put(socket, cleSession);
                }
                else
                    logger.Trace("\nErreur de connexion de " + requete.getIdMedecin());

                return reponse;
            }
        }
        logger.Trace("\nErreur client " + requete.getIdMedecin() + ", socket null");
        return null;
    }

    private void TraiteRequeteLOGOUT(RequeteLOGOUT requete, Socket socket) throws FinConnexionException {
        logger.Trace("RequeteLOGOUT reçue de " + requete.getDoctor().getLastName() + requete.getDoctor().getFirstName());

        if(socket != null) {
            logger.Trace(requete.getDoctor().getLastName() + requete.getDoctor().getFirstName() + " correctement deconnecte\n");
            clientConnecte.remove(socket);
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

    private long generateSalt(){
        long temps;
        double alea;

        temps = new Date().getTime();
        alea = Math.random();

        return (long) (temps + alea);
    }
}
