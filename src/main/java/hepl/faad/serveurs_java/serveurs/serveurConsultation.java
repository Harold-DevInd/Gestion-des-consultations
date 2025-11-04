package hepl.faad.serveurs_java.serveurs;

import hepl.faad.serveurs_java.protocol.Logger;
import hepl.faad.serveurs_java.protocol.Protocole;
import hepl.faad.serveurs_java.protocol.ThreadServeur;
import hepl.faad.serveurs_java.protocol.ThreadServeurPool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class serveurConsultation implements Logger {
    ThreadServeur threadServeur;
    Protocole protocole;
    int port, taillePool;

    public void main(String[] args) throws IOException {
        String chemin = "serveur.conf";

        try {
            Map<String, String> config = lireConfiguration(chemin);
            port = Integer.parseInt(config.get("PORT_RESERVATION"));
            taillePool = Integer.parseInt(config.get("TAILLE_POOL"));
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        }
        protocole = null;

        threadServeur = new ThreadServeurPool(port, protocole, this, "SERVEUR_CONSULTATION", taillePool);

        threadServeur.start();
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
