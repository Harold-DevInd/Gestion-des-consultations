package hepl.faad.serveurs_java.protocol;

import java.io.IOException;
import java.net.Socket;

public interface Protocole {
    String getNom();
    Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException, IOException;
}
