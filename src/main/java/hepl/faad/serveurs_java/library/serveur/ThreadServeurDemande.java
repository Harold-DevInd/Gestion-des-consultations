package hepl.faad.serveurs_java.library.serveur;

import hepl.faad.serveurs_java.library.protocol.Protocole;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadServeurDemande extends ThreadServeur {
    public ThreadServeurDemande(int port, Protocole protocole, Logger logger) throws IOException
    {
        super(port, protocole, logger);
    }

    @Override
    public void run()
    {
        logger.Trace("Démarrage du TH Serveur (Demande)...");
        logger.Trace("En attente de connexions.....");
        while(!this.isInterrupted())
        {
            Socket csocket;
            try
            {
                ssocket.setSoTimeout(2000);
                csocket = ssocket.accept();
                logger.Trace("Connexion acceptée, création TH Client");
                Thread th = new ThreadClientDemande(protocole,csocket,logger);
                th.start();
            }
            catch (SocketTimeoutException ex)
            {
                // Pour vérifier si le thread a été interrompu
            }
            catch (IOException ex)
            {
                logger.Trace("Erreur I/O");
            }
        }
        logger.Trace("TH Serveur (Demande) interrompu.");
        try { ssocket.close(); }
        catch (IOException ex) { logger.Trace("Erreur I/O"); }
    }
}
