package hepl.faad.serveurs_java.library.serveur;

import hepl.faad.serveurs_java.library.protocol.Protocole;

import java.io.IOException;
import java.net.Socket;

public class ThreadClientDemande extends ThreadClient{
    public ThreadClientDemande(Protocole protocole, Socket csocket, Logger logger) throws IOException
    {
        super(protocole, csocket, logger);
    }
    @Override
    public void run()
    {
        logger.Trace("TH Client (Demande) démarre...");
        super.run();
        logger.Trace("TH Client (Demande) se termine.");
    }
}
