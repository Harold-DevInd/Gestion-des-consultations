package hepl.faad.serveurs_java.library.serveur;

public class FinConnexionException extends Exception {
    private Reponse reponse;

    public FinConnexionException(Reponse reponse) {
        super("Fin de connexion decide par le protocol");
        this.reponse = reponse;
    }

    public Reponse getReponse() {
        return reponse;
    }
}
