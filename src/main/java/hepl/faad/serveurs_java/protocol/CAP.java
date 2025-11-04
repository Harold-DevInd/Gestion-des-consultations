package hepl.faad.serveurs_java.protocol;

import java.net.Socket;

public class CAP {
    String requette;
    String reponse;
    Socket socket;

    public CAP(String requette, String reponse, Socket socket) {
        this.requette = requette;
        this.reponse = reponse;
        this.socket = socket;

        String[] options = requette.split("#");

        if(options[0] == "LOGIN")
        {

        }
    }


}
