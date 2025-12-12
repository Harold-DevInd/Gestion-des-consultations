package hepl.faad.serveurs_java.serveurs;

import hepl.faad.serveurs_java.library.protocol.Protocole;
import hepl.faad.serveurs_java.library.serveur.Logger;
import hepl.faad.serveurs_java.library.serveur.ThreadServeur;
import hepl.faad.serveurs_java.library.serveur.ThreadServeurDemande;
import hepl.faad.serveurs_java.library.serveur.ThreadServeurPool;
import hepl.faad.serveurs_java.protocol.CAP;
import hepl.faad.serveurs_java.protocol.MRPS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.security.*;
import java.security.cert.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class serveurRapportMedical implements Logger {
    ThreadServeur threadServeur;
    Protocole protocole;
    int port;

    public serveurRapportMedical() {}

    public void runServeur() throws IOException, SQLException {
        String chemin = "C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\serveurs\\serveur.conf";
        try {
            Map<String, String> config = lireConfiguration(chemin);
            port = Integer.parseInt(config.get("PORT_REPORT_SECURE"));
        } catch (IOException e)  {
            System.err.println("Erreur de lecture du fichier : " + chemin + " : " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format dans le fichier de configuration (port ou taille pool) : " + e.getMessage());
            return;
        }
    protocole = new MRPS(this);

        threadServeur = new ThreadServeurDemande(port, protocole, this);
        threadServeur.start();
        Trace("Serveur de Rapport Medicaux à démarré sur le port " + port+ " .");
    }

    public static void main(String[] args) throws IOException, SQLException {
        serveurRapportMedical serveur = new serveurRapportMedical();
        serveur.runServeur();
    }

    @Override
    public void Trace(String msg) {
        System.out.println("\n" + msg);
    }

    public static Map<String, String> lireConfiguration(String cheminFichier) throws IOException {
        return serveurConsultation.lireConfiguration(cheminFichier);
    }
}
