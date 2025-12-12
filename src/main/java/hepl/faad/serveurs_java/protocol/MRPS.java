package hepl.faad.serveurs_java.protocol;

import hepl.faad.serveurs_java.library.CryptoManagement;
import hepl.faad.serveurs_java.library.protocol.MRPS.*;
import hepl.faad.serveurs_java.library.protocol.Protocole;
import hepl.faad.serveurs_java.library.serveur.FinConnexionException;
import hepl.faad.serveurs_java.library.serveur.Logger;
import hepl.faad.serveurs_java.library.serveur.Reponse;
import hepl.faad.serveurs_java.library.serveur.Requete;
import hepl.faad.serveurs_java.model.dao.ConsultationDAO;
import hepl.faad.serveurs_java.model.dao.DoctorDAO;
import hepl.faad.serveurs_java.model.dao.PatientDAO;
import hepl.faad.serveurs_java.model.dao.ReportDAO;
import hepl.faad.serveurs_java.model.entity.Consultation;
import hepl.faad.serveurs_java.model.entity.Doctor;
import hepl.faad.serveurs_java.model.entity.Patient;
import hepl.faad.serveurs_java.model.entity.Report;
import hepl.faad.serveurs_java.model.viewmodel.ConsultationSearchVM;
import hepl.faad.serveurs_java.model.viewmodel.DoctorSearchVM;
import hepl.faad.serveurs_java.model.viewmodel.PatientSearchVM;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class MRPS implements Protocole {
    private Logger logger;
    private ConsultationDAO consultationDAO;
    private DoctorDAO doctorDAO;
    private PatientDAO patientDAO;
    private ReportDAO reportDAO;
    private Map<Socket, SecretKey> clientConnecte;

    public MRPS(Logger log) {
        this.logger = log;
        this.consultationDAO = new ConsultationDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
        this.reportDAO = new ReportDAO();
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
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | KeyStoreException |
                 CertificateException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private Reponse TraiteRequeteLOGIN(RequeteLOGIN requete, Socket socket) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, CertificateException, KeyStoreException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
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
                    logger.Trace("Docteur avec id : " +requete.getIdMedecin() + " existant \n");
                    reponse.setSel(generateSalt());
                }
                else
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
                    logger.Trace(doctor.getLastName() + " " + doctor.getFirstName() + " correctement loggé \n");
                    KeyGenerator cleGen = KeyGenerator.getInstance("DES","BC");
                    cleGen.init(new SecureRandom());
                    SecretKey cleSession = cleGen.generateKey();

                    byte[] cleSessionChiffre = CryptoManagement.CryptAsymRSA(RecupereClePubliqueClient(), cleSession.getEncoded());

                    reponse.setSessionKey(cleSessionChiffre);
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

    private Reponse TraiteRequeteADDREPORT(RequeteADDREPORT requete, Socket socket) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        logger.Trace("RequeteADDREPORT reçue de " + socket);
        Report rapport = null;

        if(socket != null) {
            SecretKey cleSession = clientConnecte.get(socket);
            byte[] reponseByte = CryptoManagement.CryptSymDES(cleSession, "non".getBytes());
            ReponseADDREPORT reponse = new ReponseADDREPORT(reponseByte);

            if(cleSession != null) {
                logger.Trace("Client connecté, traitement de la requête...");
                byte[] rapportByte = CryptoManagement.DecryptSymDES(cleSession, requete.getMessage());

                rapport = Report.convertByteToReport(rapportByte);
                List<Patient> patients = new PatientDAO().load(new PatientSearchVM(rapport.getPatientId(), null, null));
                List<Doctor> doctors = new DoctorDAO().load(new DoctorSearchVM(rapport.getDoctorId(), null, null, null));

                if(patients.isEmpty() || doctors.isEmpty()) {
                    logger.Trace("Patient ou docteur inexistant, rejet de la requête.\n");
                    return reponse;
                }

                List<Consultation> consultations = consultationDAO.load(new ConsultationSearchVM(null, doctors.get(0), patients.get(0), null, null));

                if(consultations.isEmpty()) {
                    logger.Trace("Aucune consultation trouvée entre le patient et le docteur, rejet de la requête.\n");
                    return reponse;
                }

                reportDAO.save(rapport);
                logger.Trace("Rapport ajouté avec succès.\n");
                reponseByte = CryptoManagement.CryptSymDES(cleSession, "oui".getBytes());
                reponse = new ReponseADDREPORT(reponseByte);

                return reponse;
            } else {
                logger.Trace("Client non connecté, rejet de la requête.\n");
                return reponse;
            }
        } else {
            logger.Trace("Socket nulle, impossible de traiter la requête.\n");
        }
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

    public static PrivateKey RecupereClePrivee() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\keyStore\\serveur\\KeystoreServeur.jks"),
                "oracle".toCharArray());
        PrivateKey cle = (PrivateKey) ks.getKey("serveur","oracle".toCharArray());
        return cle;
    }
    public static PublicKey RecupereClePublique() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\keyStore\\serveur\\KeystoreServeur.jks"),
                "oracle".toCharArray());
        PublicKey cle = (PublicKey) ks.getKey("serveur","oracle".toCharArray());
        return cle;
    }

    public static PublicKey RecupereClePubliqueClient() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
    // Récupération de la clé publique du client dans le keystore du serveur
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\keyStore\\serveur\\KeystoreServeur.jks"),
                "oracle".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("clientDoctor");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }
}
