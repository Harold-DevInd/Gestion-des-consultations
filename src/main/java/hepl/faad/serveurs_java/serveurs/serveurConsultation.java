package hepl.faad.serveurs_java.serveurs;

import hepl.faad.serveurs_java.library.serveur.Logger;
import hepl.faad.serveurs_java.library.protocol.Protocole;
import hepl.faad.serveurs_java.library.serveur.ThreadServeur;
import hepl.faad.serveurs_java.library.serveur.ThreadServeurPool;
import hepl.faad.serveurs_java.protocol.CAP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.mysql.cj.conf.PropertyKey.logger;

public class serveurConsultation implements Logger {
    ThreadServeur threadServeur;
    Protocole protocole;
    int port, taillePool;

    public serveurConsultation() {}

    public void runServeur() throws IOException, SQLException {
        String chemin = "C:\\Users\\harol\\Documents\\HEPL\\Bach 3\\Q1\\Developpement logiciel RTI\\Labo\\Serveurs JAVA\\src\\main\\java\\hepl\\faad\\serveurs_java\\serveurs\\serveur.conf";
        try {
            Map<String, String> config = lireConfiguration(chemin);
            port = Integer.parseInt(config.get("PORT_CONSULTATION"));
            taillePool = Integer.parseInt(config.get("TAILLE_POOL"));
        } catch (IOException e)  {
            System.err.println("Erreur de lecture du fichier : " + chemin + " : " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format dans le fichier de configuration (port ou taille pool) : " + e.getMessage());
            return;
        }
        protocole = new CAP(this);

        threadServeur = new ThreadServeurPool(port, protocole, this, "SERVEUR_CONSULTATION", taillePool);
        threadServeur.start();
        Trace("Serveur de Consultation démarré sur le port " + port + " avec un pool de " + taillePool + " threads.");
    }

    public static void main(String[] args) throws IOException, SQLException {
        serveurConsultation serveur = new serveurConsultation();
        serveur.runServeur();
    }

    @Override
    public void Trace(String msg) {
        System.out.println("\n" + msg);
    }

    public static Map<String, String> lireConfiguration(String cheminFichier) throws IOException {
        Map<String, String> config = new HashMap<>();

        try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                ligne = ligne.trim();
                if (!ligne.isEmpty() && ligne.contains("=")) {
                    String[] parts = ligne.split("=", 2);
                    String cle = parts[0].trim();
                    String valeur = parts[1].trim();
                    config.put(cle, valeur);
                }
            }
        }
        return config;
    }
}
