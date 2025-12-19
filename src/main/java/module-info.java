module hepl.faad.serveurs_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires org.bouncycastle.provider;
    requires jdk.httpserver;


    opens hepl.faad.serveurs_java to javafx.fxml;
    exports hepl.faad.serveurs_java;
}